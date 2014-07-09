package org.ccsds.moims.mo.mal.planning.test;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.planning.consumer.PlanningRequestServiceConsumer;
import org.ccsds.moims.mo.mal.planning.consumer.PlanningRequestServiceConsumerAdapter;
import org.ccsds.moims.mo.mal.structures.EntityKey;
import org.ccsds.moims.mo.mal.structures.EntityKeyList;
import org.ccsds.moims.mo.mal.structures.EntityRequest;
import org.ccsds.moims.mo.mal.structures.EntityRequestList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.Subscription;

public class PlanningRequestServiceUtil {
	
	public static PlanningRequestServiceConsumerAdapter subscribeConsumer(PlanningRequestServiceConsumer consumer, String name, Long id) throws MALInteractionException, MALException {
		final Identifier subscriptionId = new Identifier(name);
		// set up the wildcard subscription
		final EntityKey entitykey = new EntityKey(new Identifier(id.toString()), 0L, 0L,
				0L);
		final EntityKeyList entityKeys = new EntityKeyList();
		entityKeys.add(entitykey);
		final EntityRequest entity = new EntityRequest(null, false, false,
				false, false, entityKeys);
		final EntityRequestList entities = new EntityRequestList();
		entities.add(entity);
		Subscription subRequestWildcard = new Subscription(subscriptionId,
				entities);
		PlanningRequestServiceConsumerAdapter listener = new PlanningRequestServiceConsumerAdapter(name);
		consumer.getPlanningRequestService().monitorRegister(
				subRequestWildcard, listener);
		return listener;
	}
	
	public static void unsubscribeConsumer(PlanningRequestServiceConsumer consumer, String name) throws MALInteractionException, MALException {
		final Identifier subscriptionId = new Identifier(name);
		final IdentifierList subLst = new IdentifierList();
		subLst.add(subscriptionId);
		consumer.getPlanningRequestService().monitorDeregister(subLst);
	}

}
