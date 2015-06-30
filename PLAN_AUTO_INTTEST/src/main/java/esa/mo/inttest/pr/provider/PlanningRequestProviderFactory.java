/*******************************************************************************
 * This source code is proprietary of CGI Estonia AS and covered by copyright.
 * European Space Agency is granted a non-exclusive, free, worldwide license
 * to use this source code without the right to commercialize it. 
 * You may not use this code without prior written consent of CGI Estonia AS.
 *******************************************************************************/
package esa.mo.inttest.pr.provider;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import org.ccsds.moims.mo.planning.planningrequest.PlanningRequestHelper;
import org.ccsds.moims.mo.planning.planningrequest.provider.MonitorPlanningRequestsPublisher;

import esa.mo.inttest.pr.PlanningRequestFactory;

/**
 * Planning request provider factory. Produces single provider with Task and PR publishing.
 */
public class PlanningRequestProviderFactory extends PlanningRequestFactory {

	private static final Logger LOG = Logger.getLogger(PlanningRequestProviderFactory.class.getName());
	
	private PlanningRequestProvider prov = null;
	private URI brokerUri = null;
	private MonitorPlanningRequestsPublisher prPub = null;
	private MALProviderManager malProvMgr = null;
	private MALProvider malProv = null;
	
	/**
	 * Set broker to use. If null, provider will create one itself.
	 * @param broker
	 */
	public void setBrokerUri(URI broker) {
		brokerUri = broker;
	}
	
	/**
	 * Creates Service.
	 * @param name
	 * @throws MALException
	 */
	private void initProvider(String name) throws MALException {
		LOG.entering(getClass().getName(), "initProvider");
		
		prov = new PlanningRequestProvider();
		prov.setDomain(domain);
		
		String provName = (null != name && !name.isEmpty()) ? name : "PrProv";
		String proto = "rmi";
		Blob authId = new Blob("".getBytes());
		QoSLevel[] expQos = { QoSLevel.ASSURED, };
		UInteger priority = new UInteger(1L);
		boolean isPublisher = true;
		
		malProv = malProvMgr.createProvider(provName, proto, PlanningRequestHelper.PLANNINGREQUEST_SERVICE,
				authId, prov, expQos, priority, System.getProperties(), isPublisher, brokerUri);
		
		prov.setUri(malProv.getURI());
		
		LOG.exiting(getClass().getName(), "initProvider");
	}

	/**
	 * Creates PR publisher and registers it.
	 * @throws MALException
	 * @throws MALInteractionException
	 */
	private void initPrPublisher() throws MALException, MALInteractionException {
		LOG.entering(getClass().getName(), "initPrPublisher");
		
		Identifier network = new Identifier("junit");
		SessionType sessionType = SessionType.LIVE;
		Identifier sessionName = new Identifier("test");
		QoSLevel qos = QoSLevel.BESTEFFORT;
		UInteger priority = new UInteger(0L);
		
		prPub = prov.createMonitorPlanningRequestsPublisher(domain, network, sessionType, sessionName, qos,
				System.getProperties(), priority);
		
		EntityKeyList keyList = new EntityKeyList();
		keyList.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
		
		prPub.register(keyList, null); // no async calls - no listener needed
		
		prov.setPrPub(prPub);
		
		LOG.exiting(getClass().getName(), "initPrPublisher");
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
		initPrPublisher();
		
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
		
		if (null != prov) {
			prov.setPrPub(null);
		}
		if (prPub != null) {
			try {
				prPub.deregister();
			} catch (MALInteractionException e) { // ignore
				LOG.log(Level.WARNING, "pr pub de-reg: {0}", e.getStandardError());
			}
			prPub.close();
		}
		prPub = null;
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
