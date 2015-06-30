/*******************************************************************************
 * This source code is proprietary of CGI Estonia AS and covered by copyright.
 * European Space Agency is granted a non-exclusive, free, worldwide license
 * to use this source code without the right to commercialize it. 
 * You may not use this code without prior written consent of CGI Estonia AS.
 *******************************************************************************/
package esa.mo.inttest.pr.consumer;

import java.io.IOException;
import java.util.logging.Logger;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.consumer.MALConsumer;
import org.ccsds.moims.mo.mal.consumer.MALConsumerManager;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.planning.planningrequest.PlanningRequestHelper;
import org.ccsds.moims.mo.planning.planningrequest.consumer.PlanningRequestStub;

import esa.mo.inttest.pr.PlanningRequestFactory;

/**
 * Planning request consumer for testing. Can produce several consumers.
 */
public class PlanningRequestConsumerFactory extends PlanningRequestFactory {

	private static final Logger LOG = Logger.getLogger(PlanningRequestConsumerFactory.class.getName());
	
	private MALConsumerManager malConsMgr = null;
	private URI provUri = null;
	private URI brokerUri = null;
	
	/**
	 * Set provider to use.
	 * @param uri
	 */
	public void setProviderUri(URI uri) {
		provUri = uri;
	}

	/**
	 * Set broker to use.
	 * @param uri
	 */
	public void setBrokerUri(URI uri) {
		brokerUri = uri;
	}

	private PlanningRequestStub initConsumer(String name) throws MALException {
		LOG.entering(getClass().getName(), "initConsumer");
		
		String consName = (null != name && !name.isEmpty()) ? name : "PrCons";
		Blob authId = new Blob("".getBytes());
		Identifier network = new Identifier("junit");
		SessionType sessionType = SessionType.LIVE;
		Identifier sessionName = new Identifier("test");
		QoSLevel qos = QoSLevel.ASSURED;
		UInteger priority = new UInteger(0L);
		
		MALConsumer malCons = malConsMgr.createConsumer(consName, provUri, brokerUri,
				PlanningRequestHelper.PLANNINGREQUEST_SERVICE, authId, domain, network, sessionType, sessionName,
				qos, System.getProperties(), priority);
		
		PlanningRequestStub cons = new PlanningRequestStub(malCons);
		
		LOG.exiting(getClass().getName(), "initConsumer");
		return cons;
	}

	/**
	 * Start (create) PR Consumer.
	 * @param name
	 * @return
	 * @throws IOException
	 * @throws MALException
	 */
	public PlanningRequestStub start(String name) throws IOException, MALException {
		LOG.entering(getClass().getName(), "start");
		
		super.init();
		
		if (malConsMgr == null) {
			malConsMgr = malCtx.createConsumerManager();
		}
		PlanningRequestStub stub = initConsumer(name);
		
		LOG.exiting(getClass().getName(), "start");
		return stub;
	}

	/**
	 * Stop (dispose) PR Consumer.
	 * @param cons
	 * @throws MALException
	 */
	public void stop(PlanningRequestStub cons) throws MALException {
		LOG.entering(getClass().getName(), "stop");
		
		if (cons != null && cons.getConsumer() != null) {
			cons.getConsumer().close();
		}
		if (malConsMgr != null) {
			malConsMgr.close();
		}
		malConsMgr = null;
		
		super.close();
		
		LOG.exiting(getClass().getName(), "stop");
	}
}
