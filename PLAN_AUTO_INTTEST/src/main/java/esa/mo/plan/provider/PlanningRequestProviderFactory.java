package esa.mo.plan.provider;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.ccsds.moims.mo.mal.MALContext;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALService;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.provider.MALProviderManager;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.planning.PlanningHelper;
import org.ccsds.moims.mo.planning.planningrequest.PlanningRequestHelper;
import org.ccsds.moims.mo.planningdatatypes.PlanningDataTypesHelper;

public class PlanningRequestProviderFactory {

	private String propertyFile = null;
	private MALContext malCtx = null;
	private PlanningRequestProvider prov = null;
//	private MonitorTasksPublisher taskPub = null;
//	private MonitorPlanningRequestsPublisher pub = null;
	private MALProviderManager malProvMgr = null;
	private MALProvider malProv = null;

	public void setPropertyFile(String fn) {
		propertyFile = fn;
	}
	
	private void initProperties() throws IOException {
		InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(propertyFile);
		Properties props = new Properties();
		props.load(is);
		is.close();
		System.getProperties().putAll(props);
//		System.out.println("PRPF:initProperties: rmi transport: " + System.getProperty("org.ccsds.moims.mo.mal.transport.protocol.rmi"));
//		System.out.println("PRPF:initProperties: string encoder: " + System.getProperty("org.ccsds.moims.mo.mal.encoding.protocol.rmi"));
//		System.out.println("PRPF:initProperties: gen wrap: " + System.getProperty("org.ccsds.moims.mo.mal.transport.gen.wrap"));
	}
	
	private void initContext() throws MALException {
		malCtx = MALContextFactory.newFactory().createMALContext(System.getProperties());
	}

	private void initHelpers() throws MALException {
		MALHelper.init(MALContextFactory.getElementFactoryRegistry());
		PlanningHelper.init(MALContextFactory.getElementFactoryRegistry());
		PlanningDataTypesHelper.init(MALContextFactory.getElementFactoryRegistry());
		MALService tmp = PlanningHelper.PLANNING_AREA.getServiceByName(PlanningRequestHelper.PLANNINGREQUEST_SERVICE_NAME);
		if (tmp == null) {
			PlanningRequestHelper.init(MALContextFactory.getElementFactoryRegistry());
		}
	}

//	private void initTaskPublisher() throws MALException, MALInteractionException {
//		IdentifierList domain = new IdentifierList();
//		domain.add(new Identifier("desd"));
//		Identifier network = new Identifier("junit");
//		SessionType sessionType = SessionType.LIVE;
//		Identifier sessionName = new Identifier("test");
//		QoSLevel qos = QoSLevel.BESTEFFORT;
//		UInteger priority = new UInteger(0L);
//		taskPub = prov.createMonitorTasksPublisher(domain, network, sessionType, sessionName, qos,
//				System.getProperties(), priority);
//		EntityKeyList keyList = new EntityKeyList();
//		keyList.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
//		taskPub.register(keyList, new MALPublishInteractionListener() {
//			
//			public void publishRegisterErrorReceived(MALMessageHeader header, MALErrorBody body, Map qosProperties)
//					throws MALException {
//				System.out.println("task.pub.reg.err");
//			}
//			
//			public void publishRegisterAckReceived(MALMessageHeader header, Map qosProperties) throws MALException {
//				System.out.println("task.pub.reg.ack");
//			}
//			
//			public void publishErrorReceived(MALMessageHeader header, MALErrorBody body, Map qosProperties) throws MALException {
//				System.out.println("task.pub.err");
//			}
//			
//			public void publishDeregisterAckReceived(MALMessageHeader header, Map qosProperties) throws MALException {
//				System.out.println("task.pub.dereg");
//			}
//		});
//	}
//	
//	private void initPublisher() throws MALException, MALInteractionException {
//		IdentifierList domain = new IdentifierList();
//		domain.add(new Identifier("desd"));
//		Identifier network = new Identifier("junit");
//		SessionType sessionType = SessionType.LIVE;
//		Identifier sessionName = new Identifier("test");
//		QoSLevel qos = QoSLevel.BESTEFFORT;
//		UInteger priority = new UInteger(0L);
//		pub = prov.createMonitorPlanningRequestsPublisher(domain, network, sessionType, sessionName, qos,
//				System.getProperties(), priority);
//		EntityKeyList keyList = new EntityKeyList();
//		keyList.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
//		pub.register(keyList, new MALPublishInteractionListener() {
//			
//			public void publishRegisterErrorReceived(MALMessageHeader header, MALErrorBody body, Map qosProperties)
//					throws MALException {
//				System.out.println("pr.pub.reg.err");
//			}
//			
//			public void publishRegisterAckReceived(MALMessageHeader header, Map qosProperties) throws MALException {
//				System.out.println("pr.pub.reg.ack");
//			}
//			
//			public void publishErrorReceived(MALMessageHeader header, MALErrorBody body, Map qosProperties) throws MALException {
//				System.out.println("pr.pub.err");
//			}
//			
//			public void publishDeregisterAckReceived(MALMessageHeader header, Map qosProperties) throws MALException {
//				System.out.println("pr.pub.dereg");
//			}
//		});
//	}
	
	private void initProvider() throws MALException {
		malProvMgr = malCtx.createProviderManager();
		prov = new PlanningRequestProvider();
		String provName = "testProv";
		String proto = "rmi";
		Blob authId = new Blob("".getBytes());
		QoSLevel[] expQos = { QoSLevel.ASSURED, };
		UInteger priority = new UInteger(0L);
		boolean isPublisher = true;
		URI brokerUri = null;
		malProv = malProvMgr.createProvider(provName, proto, PlanningRequestHelper.PLANNINGREQUEST_SERVICE,
				authId, prov, expQos, priority, System.getProperties(), isPublisher, brokerUri);
	}

	public void start() throws IOException, MALException, MALInteractionException {
		initProperties();
		initContext();
		initHelpers();
//		initTaskPublisher();
//		initPublisher();
		initProvider();
	}
	
	public URI getProviderUri() {
		return malProv.getURI();
	}
	
	public URI getBrokerUri() {
		return malProv.getBrokerURI();
	}
	
	public void stop() throws MALException, MALInteractionException {
		malProv.close();
		malProv = null;
		malProvMgr.close();
		malProvMgr = null;
//		pub.deregister();
//		pub.close();
//		pub = null;
//		taskPub.deregister();
//		taskPub.close();
//		taskPub = null;
		prov = null;
		malCtx.close();
		malCtx = null;
	}
}
