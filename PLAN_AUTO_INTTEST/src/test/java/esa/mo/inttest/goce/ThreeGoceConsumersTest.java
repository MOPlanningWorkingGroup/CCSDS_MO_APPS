package esa.mo.inttest.goce;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.util.Random;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.EntityKey;
import org.ccsds.moims.mo.mal.structures.EntityKeyList;
import org.ccsds.moims.mo.mal.structures.EntityRequest;
import org.ccsds.moims.mo.mal.structures.EntityRequestList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mal.transport.MALNotifyBody;
import org.ccsds.moims.mo.planning.planningrequest.consumer.PlanningRequestAdapter;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskStatusDetailsList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import esa.mo.inttest.goce.GoceConsumer;
import esa.mo.inttest.pr.consumer.PlanningRequestConsumerFactory;
import esa.mo.inttest.pr.provider.PlanningRequestProviderFactory;

/**
 * Simultaneous GOCE consumers test. One consumer managing defs, second managing instances, third only monitoring.
 */
public class ThreeGoceConsumersTest {

	private static final Logger LOG = Logger.getLogger(ThreeGoceConsumersTest.class.getName());
	
	private PlanningRequestProviderFactory provFct;
	
	private PlanningRequestConsumerFactory consFct;

	private GoceConsumer goce1;
	private GoceConsumer goce2;
	private GoceConsumer goce3;
	
	@Before
	public void setUp() throws Exception {
		LOG.entering(getClass().getName(), "setUp");
		String props = "testInt.properties";
		
		provFct = new PlanningRequestProviderFactory();
		provFct.setPropertyFile(props);
		provFct.start();
		
		consFct = new PlanningRequestConsumerFactory();
		consFct.setPropertyFile(props);
		consFct.setProviderUri(provFct.getProviderUri());
		consFct.setBrokerUri(provFct.getBrokerUri());
		
		goce1 = new GoceConsumer(consFct.start()); // start a new instance of consumer
		goce2 = new GoceConsumer(consFct.start());
		goce3 = new GoceConsumer(consFct.start());
		LOG.exiting(getClass().getName(), "setUp");
	}

	@After
	public void tearDown() throws Exception {
		LOG.entering(getClass().getName(), "tearDown");
		if (consFct != null) {
			consFct.stop(goce3.getStub());
			consFct.stop(goce2.getStub());
			consFct.stop(goce1.getStub());
		}
		goce3 = null;
		goce2 = null;
		goce1 = null;
		consFct = null;
		
		if (provFct != null) {
			provFct.stop();
		}
		provFct = null;
		LOG.exiting(getClass().getName(), "tearDown");
	}

	/// sleep() somewhere inbetween minimum and maximum millis
	private void sleeep(long n, long x) {
		try {
			int d = (int)(x - n);
			long t = (d > 0 ? new Random().nextInt(d) : 0) + n;
			Thread.sleep(t);
		} catch (InterruptedException e) {
			// ignore
		}
	}
	
	@Test
	public void testPpf() throws MALException, MALInteractionException, ParseException {
		LOG.entering(getClass().getName(), "testPpf");
		assertNotNull(goce1);
		assertNotNull(goce2);
		assertNotNull(goce3);
		Thread worker1 = new Thread("PrProv") {
			@Override
			public void run() {
				LOG.entering(getName(), "run");
				while (!isInterrupted()) {
					sleeep(1000, 3000);
					try {
						goce1.createPpfDefsIfMissing();
					} catch (Exception e) {
						LOG.log(Level.WARNING, getName() + ": createPpfDefs", e);
						e.printStackTrace();
						throw new RuntimeException(e);
					}
				}
				LOG.exiting(getName(), "run");
			}
		};
		Thread worker2 = new Thread() {
			@Override
			public void run() {
				LOG.entering(getName(), "run");
				while (!isInterrupted()) {
					sleeep(1000, 3000);
					try {
						goce2.createPpfInstsIfMissingAndDefsExist();
					} catch (Exception e) {
						LOG.log(Level.WARNING, getName() + ": createPpfInst", e);
						e.printStackTrace();
						throw new RuntimeException(e);
					}
				}
				LOG.exiting(getName(), "run");
			}
		};
		Subscription taskSub = new Subscription();
		String taskSubId = "prCons3taskSubId";
		taskSub.setSubscriptionId(new Identifier(taskSubId));
		EntityRequestList taskReqs = new EntityRequestList();
		EntityKeyList taskKeys = new EntityKeyList();
		taskKeys.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
		taskReqs.add(new EntityRequest(null, true, true, true, false, taskKeys));
		taskSub.setEntities(taskReqs);
		goce3.getStub().monitorTasksRegister(taskSub, new PlanningRequestAdapter() {
			@SuppressWarnings("rawtypes")
			@Override
			public void monitorTasksRegisterAckReceived(MALMessageHeader msgHeader, Map qosProps) {
				LOG.log(Level.INFO, "task monitor registration ack");
			}

			@SuppressWarnings("rawtypes")
			@Override
			public void monitorTasksRegisterErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
					Map qosProps) {
				LOG.log(Level.INFO, "task monitor registration error");
			}

			@SuppressWarnings("rawtypes")
			@Override
			public void monitorTasksNotifyReceived(MALMessageHeader msgHeader, Identifier subId,
					UpdateHeaderList updHdrs, ObjectIdList objIds, TaskStatusDetailsList taskStats, Map qosProps) {
				LOG.log(Level.INFO, "task monitor notify");
			}

			@SuppressWarnings("rawtypes")
			@Override
			public void monitorTasksNotifyErrorReceived(MALMessageHeader msgHeader, MALStandardError error, Map qosProps) {
				LOG.log(Level.INFO, "task monitor notify error");
			}

			@SuppressWarnings("rawtypes")
			@Override
			public void monitorTasksDeregisterAckReceived(MALMessageHeader msgHeader, Map qosProps) {
				LOG.log(Level.INFO, "task monitor de-registration ack");
			}

			@SuppressWarnings("rawtypes")
			@Override
			public void notifyReceivedFromOtherService(MALMessageHeader msgHeader, MALNotifyBody body, Map qosProps)
					throws MALException {
				LOG.log(Level.INFO, "task other notify");
			}
		});
		Subscription prSub = new Subscription();
		String prSubId = "prCons3prSubId";
		prSub.setSubscriptionId(new Identifier(prSubId));
		EntityRequestList prReqs = new EntityRequestList();
		EntityKeyList prKeys = new EntityKeyList();
		prKeys.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
		prReqs.add(new EntityRequest(null, true, true, true, false, prKeys));
		prSub.setEntities(prReqs);
		goce3.getStub().monitorPlanningRequestsRegister(prSub, new PlanningRequestAdapter() {
			@SuppressWarnings("rawtypes")
			@Override
			public void monitorPlanningRequestsRegisterAckReceived(MALMessageHeader msgHeader, Map qosProps) {
				LOG.log(Level.INFO, "pr monitor registration ack");
			}

			@SuppressWarnings("rawtypes")
			@Override
			public void monitorPlanningRequestsRegisterErrorReceived(MALMessageHeader msgHeader,
					MALStandardError error, Map qosProps) {
				LOG.log(Level.INFO, "pr monitor registration error");
			}

			@SuppressWarnings("rawtypes")
			@Override
			public void monitorPlanningRequestsNotifyReceived(MALMessageHeader msgHeader, Identifier subId,
					UpdateHeaderList updHdrs, ObjectIdList objIds, PlanningRequestStatusDetailsList prStats,
					Map qosProps) {
				LOG.log(Level.INFO, "pr monitor notify");
			}

			@SuppressWarnings("rawtypes")
			@Override
			public void monitorPlanningRequestsNotifyErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
					Map qosProps) {
				LOG.log(Level.INFO, "pr monitor notify error");
			}

			@SuppressWarnings("rawtypes")
			@Override
			public void monitorPlanningRequestsDeregisterAckReceived(MALMessageHeader msgHeader, Map qosProps) {
				LOG.log(Level.INFO, "pr monitor de-registration ack");
			}

			@SuppressWarnings("rawtypes")
			@Override
			public void notifyReceivedFromOtherService(MALMessageHeader msgHeader, MALNotifyBody body, Map qosProps)
					throws MALException {
				LOG.log(Level.INFO, "pr other notify");
			}
		});
		worker1.start();
		worker2.start();
		LOG.log(Level.INFO, "sleeping..");
		sleeep(10*1000, 0); // 10 sec
		LOG.log(Level.INFO, "waking..");
		worker1.interrupt();
		worker2.interrupt();
		try {
			worker1.join(3*1000);
		} catch (InterruptedException e) {
			// ignore
		}
		try {
			worker2.join(3*1000);
		} catch (InterruptedException e) {
			// ignore
		}
		LOG.exiting(getClass().getName(), "testPpf");
	}

//	@Test
//	public void testPif() throws MALException, MALInteractionException, ParseException {
//		goce1.pif();
//	}
//
//	@Test
//	public void testSpf() throws MALException, MALInteractionException, ParseException {
//		goce1.spf();
//	}
//
//	@Test
//	public void testOpf() throws MALException, MALInteractionException, ParseException {
//		goce1.opf();
//	}

}
