package esa.mo.inttest.pr.provider;

import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetailsList;

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
	 * Called when new PRs are added.
	 * @param prs
	 */
	void onPrSubmit(PlanningRequestInstanceDetailsList prs);
	
	/**
	 * Called when PR has been updated.
	 * @param prs
	 * @param stats
	 */
	void onPrUpdate(PlanningRequestInstanceDetailsList prs, PlanningRequestStatusDetailsList stats);
	
	/**
	 * Called when PR has been removed.
	 * @param prInstIds
	 */
	void onPrRemove(LongList prIds);
}
