/*******************************************************************************
 * This source code is proprietary of CGI Estonia AS and covered by copyright.
 * European Space Agency is granted a non-exclusive, free, worldwide license
 * to use this source code without the right to commercialize it. 
 * You may not use this code without prior written consent of CGI Estonia AS.
 *******************************************************************************/
package esa.mo.inttest.sch.consumer;

import org.ccsds.moims.mo.automation.schedule.consumer.ScheduleStub;
import org.ccsds.moims.mo.mal.structures.EntityKey;
import org.ccsds.moims.mo.mal.structures.EntityKeyList;
import org.ccsds.moims.mo.mal.structures.EntityRequest;
import org.ccsds.moims.mo.mal.structures.EntityRequestList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.junit.After;
import org.junit.Before;

import esa.mo.inttest.sch.provider.ScheduleProviderFactory;

public class ScheduleStubTestBase {

	private ScheduleProviderFactory schProvFct;
	private ScheduleConsumerFactory schConsFct;
	protected ScheduleStub schCons;

	@Before
	public void setUp() throws Exception {
		String fn = "testInt.properties";
		
		schProvFct = new ScheduleProviderFactory();
		schProvFct.setPropertyFile(fn);
		schProvFct.start(null);
		
		schConsFct = new ScheduleConsumerFactory();
		schConsFct.setPropertyFile(fn);
		schConsFct.setProviderUri(schProvFct.getProviderUri());
		schConsFct.setBrokerUri(schProvFct.getBrokerUri());
		schCons = schConsFct.start(null);
	}

	@After
	public void tearDown() throws Exception {
		schConsFct.stop(schCons);
		schCons = null;
		schProvFct.stop();
	}

	protected Subscription createSub(String subId) {
		EntityKey entityKey = new EntityKey();
		entityKey.setFirstSubKey(new Identifier("*"));
		entityKey.setSecondSubKey(0L);
		entityKey.setThirdSubKey(0L);
		entityKey.setFourthSubKey(0L);
		
		EntityKeyList entityKeys = new EntityKeyList();
		entityKeys.add(entityKey);
		
		EntityRequest entityReq = new EntityRequest();
		entityReq.setEntityKeys(entityKeys);
		entityReq.setAllAreas(true);
		entityReq.setAllOperations(true);
		entityReq.setAllServices(true);
		entityReq.setOnlyOnChange(false);
		entityReq.setSubDomain(null);
		
		EntityRequestList entityReqs = new EntityRequestList();
		entityReqs.add(entityReq);
		
		Subscription sub = new Subscription();
		sub.setSubscriptionId(new Identifier(subId));
		sub.setEntities(entityReqs);
		
		return sub;
	}
}