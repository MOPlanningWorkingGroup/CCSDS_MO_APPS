package esa.mo.inttest.pr.provider;

import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetails;

/**
 * Planning Request Service plugin. Hooks to PR submit, update and remove methods.
 */
public interface Plugin {

	/**
	 * Set provider to use.
	 * @param prov
	 */
	void setProv(PlanningRequestProvider prov);
	
	/**
	 * Called when new PR is added.
	 * @param prDefId
	 * @param prInstId
	 * @param prInst
	 * @param taskDefIds
	 * @param taskInstIds
	 * @param prStat
	 */
	void onPrSubmit(Long prDefId, Long prInstId, PlanningRequestInstanceDetails prInst, LongList taskDefIds,
			LongList taskInstIds, PlanningRequestStatusDetails prStat);
	
	/**
	 * Called when PR has been updated.
	 * @param prDefId
	 * @param prInstId
	 * @param prInst
	 * @param taskDefIds
	 * @param taskInstIds
	 * @param prStat
	 */
	void onPrUpdate(Long prDefId, Long prInstId, PlanningRequestInstanceDetails prInst, LongList taskDefIds,
			LongList taskInstIds, PlanningRequestStatusDetails prStat);
	
	/**
	 * Called when PR has been removed.
	 * @param prInstId
	 */
	void onPrRemove(Long prInstId);
}