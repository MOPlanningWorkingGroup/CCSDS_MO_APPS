package esa.mo.plan.comarc.provider;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.archive.ArchiveHelper;
import org.ccsds.moims.mo.mal.MALContext;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.provider.MALProviderManager;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.URI;

public class ComArchiveProviderFactory {

	private String propertyFile = null;
	private MALContext malCtx = null;
	private ComArchiveProvider prov = null;
//	private MonitorTasksPublisher taskPub = null;
//	private MonitorPlanningRequestsPublisher pub = null;
	private MALProviderManager malProvMgr = null;
	private MALProvider malProv = null;
	private URI sharedBrokerUri = null;

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
		COMHelper.init(MALContextFactory.getElementFactoryRegistry());
		ArchiveHelper.init(MALContextFactory.getElementFactoryRegistry());
//		PlanningHelper.init(MALContextFactory.getElementFactoryRegistry());
//		PlanningDataTypesHelper.init(MALContextFactory.getElementFactoryRegistry());
//		MALService tmp = PlanningHelper.PLANNING_AREA.getServiceByName(PlanningRequestHelper.PLANNINGREQUEST_SERVICE_NAME);
//		if (tmp == null) { // re-init error workaround
//			PlanningRequestHelper.init(MALContextFactory.getElementFactoryRegistry());
//		}
	}
	
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
	
	public void setSharedBrokerUri(URI uri) {
		sharedBrokerUri = uri;
	}
	
	private void initProvider() throws MALException {
		malProvMgr = malCtx.createProviderManager();
		prov = new ComArchiveProvider();
		String provName = "testCaProv";
		String proto = "rmi";
		Blob authId = new Blob("".getBytes());
		QoSLevel[] expQos = { QoSLevel.ASSURED, };
		UInteger priority = new UInteger(1L);
		boolean isPublisher = true;
//		URI brokerUri = null;
		
		malProv = malProvMgr.createProvider(provName, proto, ArchiveHelper.ARCHIVE_SERVICE,
				authId, prov, expQos, priority, System.getProperties(), isPublisher, sharedBrokerUri);
	}

//	private void initTaskPublisher() throws MALException, MALInteractionException {
//		IdentifierList domain = new IdentifierList();
//		domain.add(new Identifier("desd"));
//		Identifier network = new Identifier("junit");
//		SessionType sessionType = SessionType.LIVE;
//		Identifier sessionName = new Identifier("test");
//		QoSLevel qos = QoSLevel.BESTEFFORT;
//		UInteger priority = new UInteger(0L);
//		
//		taskPub = prov.createMonitorTasksPublisher(domain, network, sessionType, sessionName, qos,
//				System.getProperties(), priority);
//		
//		EntityKeyList keyList = new EntityKeyList();
//		keyList.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
//		taskPub.register(keyList, prov);
//		prov.setTaskPub(taskPub);
//	}

	public void start() throws IOException, MALException, MALInteractionException {
		initProperties();
		initContext();
		initHelpers();
		initProvider();
//		initTaskPublisher();
//		initPublisher();
	}
	
	public URI getProviderUri() {
		return malProv.getURI();
	}
	
	public URI getBrokerUri() {
		return malProv.getBrokerURI();
	}
	
	public void stop() throws MALException, MALInteractionException {
//		if (taskPub != null) {
//			try {
//				taskPub.deregister();
//			} catch (MALInteractionException e) { // ignore
//				System.out.println("PRPF: stop: task pub de-reg: " + e);
//			}
//			taskPub.close();
//		}
//		taskPub = null;
		if (malProv != null) {
			malProv.close();
		}
		malProv = null;
		if (malProvMgr != null) {
			malProvMgr.close();
		}
		malProvMgr = null;
//		pub.deregister();
//		pub.close();
//		pub = null;
		prov = null;
		if (malCtx != null) {
			malCtx.close();
		}
		malCtx = null;
	}
}
