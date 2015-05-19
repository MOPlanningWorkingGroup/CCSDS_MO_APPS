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
	
	protected void checkPrLists(LongList ids, PlanningRequestStatusDetailsList stats) throws MALException {
		if (null == ids) {
			throw new MALException("pr ids list is null");
		}
		if (null == stats) {
			throw new MALException("pr statuses list is null");
		}
		if (ids.isEmpty()) {
			throw new MALException("pr ids list is empty");
		}
		if (ids.size() != stats.size()) {
			throw new MALException("pr ids count does not match pr statuses count");
		}
	}
	
	protected void checkPrElements(int i, LongList ids, PlanningRequestStatusDetailsList stats) throws MALException {
		Long id = ids.get(i);
		if (null == id) {
			throw new MALException("pr id[" + i + "] is null");
		}
		PlanningRequestStatusDetails stat = stats.get(i);
		if (null == stat) {
			throw new MALException("pr status[" + i + "] is null");
		}
	}
	
	public void updatePrStatus(LongList prIds, PlanningRequestStatusDetailsList prStats, MALInteraction interaction)
			throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "{2}.updatePrStatus(prIds={0}, prStatuses={1})",
				new Object[] { prIds, Dumper.prStats(prStats), Dumper.received(interaction) });
		checkPrLists(prIds, prStats);
		for (int i = 0; (null != prIds) && (i < prIds.size()); ++i) {
			checkPrElements(i , prIds, prStats);
			Long id = prIds.get(i);
			PlanningRequestStatusDetails stat = prStats.get(i);
			prov.getInstStore().setPrStatus(id, stat);
			prov.publishPr(UpdateType.UPDATE, stat);
		}
		LOG.log(Level.INFO, "{0}.updatePrStatus() response: returning nothing", Dumper.sending(interaction));
	}
	
	protected void checkTaskLists(LongList ids, TaskStatusDetailsList stats) throws MALException {
		if (null == ids) {
			throw new MALException("task ids list is null");
		}
		if (null == stats) {
			throw new MALException("task statuses list is null");
		}
		if (ids.isEmpty()) {
			throw new MALException("task ids list is empty");
		}
		if (ids.size() != stats.size()) {
			throw new MALException("task ids count differs from task statuses count");
		}
	}
	
	protected void checkTaskElements(int i, LongList ids, TaskStatusDetailsList stats) throws MALException {
		Long id = ids.get(i);
		if (null == id) {
			throw new MALException("task id[" + i + "] is null");
		}
		TaskStatusDetails stat = stats.get(i);
		if (null == stat) {
			throw new MALException("task statutus[" + i + "] is null");
		}
	}
	
	public void updateTaskStatus(LongList taskIds, TaskStatusDetailsList taskStats, MALInteraction interaction)
			throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "{2}.updateTaskStatus(taskIds={0}, taskStatuses={1})",
				new Object[] { taskIds, Dumper.taskStats(taskStats), Dumper.received(interaction) });
		checkTaskLists(taskIds, taskStats);
		for (int i = 0; (null != taskIds) && (i < taskIds.size()); ++i) {
			checkTaskElements(i, taskIds, taskStats);
			Long id = taskIds.get(i);
			TaskStatusDetails stat = taskStats.get(i);
			prov.getInstStore().setTaskStatus(id, stat);
			prov.publishTask(UpdateType.UPDATE, stat);
		}
		LOG.log(Level.INFO, "{0}.updateTaskStatus() response: returning nothing", Dumper.sending(interaction));
	}
}
