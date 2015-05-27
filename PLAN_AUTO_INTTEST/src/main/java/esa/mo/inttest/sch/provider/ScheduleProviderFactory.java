package esa.mo.inttest.sch.provider;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccsds.moims.mo.automation.schedule.ScheduleHelper;
import org.ccsds.moims.mo.automation.schedule.provider.MonitorSchedulesPublisher;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.provider.MALProviderManager;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.EntityKey;
import org.ccsds.moims.mo.mal.structures.EntityKeyList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.URI;

import esa.mo.inttest.sch.ScheduleFactory;

/**
 * Schedule provider factory. Produces single provider with Schedule publishing.
 */
public class ScheduleProviderFactory extends ScheduleFactory {

	private static final Logger LOG = Logger.getLogger(ScheduleProviderFactory.class.getName());
	
	private ScheduleProvider prov = null;
	private URI brokerUri = null;
	private MALProviderManager malProvMgr = null;
	private MALProvider malProv = null;
	private MonitorSchedulesPublisher schPub = null;

	/**
	 * Set broker to use. If null, provider will create one itself.
	 * @param broker
	 */
	public void setBrokerUri(URI broker) {
		brokerUri = broker;
	}
	
	private void initProvider(String name) throws MALException {
		LOG.entering(getClass().getName(), "initProvider");
		
		prov = new ScheduleProvider();
		prov.setDomain(domain);
		
		String provName = (null != name && !name.isEmpty()) ? name : "SchProv";
		String proto = "rmi";
		Blob authId = new Blob("".getBytes());
		QoSLevel[] expQos = { QoSLevel.ASSURED, };
		UInteger priority = new UInteger(1L);
		boolean isPublisher = true;
		
		malProv = malProvMgr.createProvider(provName, proto, ScheduleHelper.SCHEDULE_SERVICE,
				authId, prov, expQos, priority, System.getProperties(), isPublisher, brokerUri);
		
		prov.setUri(malProv.getURI());
		
		LOG.exiting(getClass().getName(), "initProvider");
	}

	private void initSchedulesPublisher() throws MALException, MALInteractionException {
		LOG.entering(getClass().getName(), "initSchedulesPublicher");
		
		Identifier network = new Identifier("junit");
		SessionType sessionType = SessionType.LIVE;
		Identifier sessionName = new Identifier("test");
		QoSLevel qos = QoSLevel.BESTEFFORT;
		UInteger priority = new UInteger(0L);
		
		schPub = prov.createMonitorSchedulesPublisher(domain, network, sessionType, sessionName, qos,
				System.getProperties(), priority);
		
		EntityKeyList keyList = new EntityKeyList();
		keyList.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
		
		schPub.register(keyList, null); // no async calls - no listener needed
		
		prov.setSchPub(schPub);
		
		LOG.exiting(getClass().getName(), "initSchedulesPublisher");
	}

	/**
	 * Creates provider and gets it up and running.
	 * @throws IOException
	 * @throws MALException
	 * @throws MALInteractionException
	 */
	public void start(String name) throws IOException, MALException, MALInteractionException {
		LOG.entering(getClass().getName(), "start");
		
		super.init();
		
		if (null == malProvMgr) {
			malProvMgr = malCtx.createProviderManager();
		}
		initProvider(name);
		initSchedulesPublisher();
		
		LOG.exiting(getClass().getName(), "start");
	}

	/**
	 * Sets plugin to use to provider.
	 * @param p
	 */
	public void setPlugin(Plugin p) {
		p.setProv(prov);
		prov.setPlugin(p);
	}

	/**
	 * Returns provider URI for consumer to connect to.
	 * @return
	 */
	public URI getProviderUri() {
		return malProv.getURI();
	}

	/**
	 * Returns used broker URI.
	 * @return
	 */
	public URI getBrokerUri() {
		return malProv.getBrokerURI();
	}

	/**
	 * Stops provider.
	 * @throws MALException
	 * @throws MALInteractionException
	 */
	public void stop() throws MALException, MALInteractionException {
		LOG.entering(getClass().getName(), "stop");
		
		if (schPub != null) {
			try {
				schPub.deregister();
			} catch (MALInteractionException e) { // ignore
				LOG.log(Level.WARNING, "schedules pub de-reg: {0}", e.getStandardError());
			}
			schPub.close();
		}
		schPub = null;
		
		if (malProv != null) {
			malProv.close();
		}
		malProv = null;
		if (malProvMgr != null) {
			malProvMgr.close();
		}
		malProvMgr = null;
		
		prov = null;
		
		super.close();
		
		LOG.exiting(getClass().getName(), "stop");
	}
}
