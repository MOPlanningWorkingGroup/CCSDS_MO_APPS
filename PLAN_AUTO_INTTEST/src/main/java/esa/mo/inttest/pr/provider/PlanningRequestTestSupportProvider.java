package esa.mo.inttest.pr.provider;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.UpdateType;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskStatusDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskStatusDetailsList;
import org.ccsds.moims.mo.planningprototype.planningrequesttest.provider.PlanningRequestTestInheritanceSkeleton;

import esa.mo.inttest.Dumper;

/**
 * Planning request test support provider. Implemented as little as necessary.
 * Provides methods to update PR and task statuses.
 */
public class PlanningRequestTestSupportProvider extends PlanningRequestTestInheritanceSkeleton {

	private static final Logger LOG = Logger.getLogger(PlanningRequestTestSupportProvider.class.getName());
	
	private PlanningRequestProvider prov;
	
	/**
	 * Default ctor.
	 */
	public PlanningRequestTestSupportProvider() {
	}
	
	/**
	 * Set PR provider to use.
	 * @param prov
	 */
	public void setProvider(PlanningRequestProvider prov) {
		this.prov = prov;
	}
	
	public void updatePrStatus(LongList prIds, PlanningRequestStatusDetailsList prStats, MALInteraction interaction)
			throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "{2}.updatePrStatus(prIds={0}, prStats={1})",
				new Object[] { prIds, Dumper.prStats(prStats), Dumper.received(interaction) });
		if (null == prIds) {
			throw new MALException("pr ids list is null");
		}
		if (null == prStats) {
			throw new MALException("pr statuses list is null");
		}
		if (prIds.size() != prStats.size()) {
			throw new MALException("pr ids list size doesnt match pr statuses list size");
		}
		for (int i = 0; (null != prIds) && (i < prIds.size()); ++i) {
			Long id = prIds.get(i);
			PlanningRequestStatusDetails stat = prStats.get(i);
			if (null != id && null != stat) {
				prov.getInstStore().setPrStatus(id, stat);
				prov.publishPr(UpdateType.UPDATE, id, stat);
			}
		}
		LOG.log(Level.INFO, "{0}.updatePrStatus() response: returning nothing", Dumper.sending(interaction));
	}

	public void updateTaskStatus(LongList taskIds, TaskStatusDetailsList taskStats, MALInteraction interaction)
			throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "{2}.updateTaskStatus(taskIds={0}, taskStats={1})",
				new Object[] { taskIds, Dumper.taskStats(taskStats), Dumper.received(interaction) });
		
		for (int i = 0; (null != taskIds) && (i < taskIds.size()); ++i) {
			Long id = taskIds.get(i);
			TaskStatusDetails stat = taskStats.get(i);
			if (null != id && null != stat) {
				prov.getInstStore().setTaskStatus(id, stat);
				prov.publishTask(UpdateType.UPDATE, id, stat);
			}
		}
		LOG.log(Level.INFO, "{0}.updateTaskStatus() response: returning nothing", Dumper.sending(interaction));
	}
}
