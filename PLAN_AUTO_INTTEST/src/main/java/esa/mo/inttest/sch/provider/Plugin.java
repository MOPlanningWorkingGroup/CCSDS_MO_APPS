package esa.mo.inttest.sch.provider;

import org.ccsds.moims.mo.automation.schedule.structures.ScheduleInstanceDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleInstanceDetailsList;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleStatusDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleStatusDetailsList;
import org.ccsds.moims.mo.mal.structures.LongList;

public interface Plugin {

	/**
	 * Set provider to use.
	 * @param prov
	 */
	void setProv(ScheduleProvider prov);
	
	/**
	 * Schedule submission callback.
	 * @param sch
	 * @param stats
	 */
	void onSubmit(ScheduleInstanceDetails sch);
	
	/**
	 * Schedule update callback.
	 * @param scheds
	 * @param stats
	 */
	void onUpdate(ScheduleInstanceDetails sch, ScheduleStatusDetails stat);
	
	/**
	 * Schedule remove callback.
	 * @param schIds
	 */
	void onRemove(Long schId);
	
	/**
	 * Schedules patched callback.
	 * @param removed
	 * @param updated
	 * @param added
	 * @param stats
	 */
	void onPatch(ScheduleInstanceDetailsList removed, ScheduleInstanceDetailsList updated,
			ScheduleInstanceDetailsList added, ScheduleStatusDetailsList stats);
	
	/**
	 * Schedule started callback.
	 * @param ids
	 * @param stats
	 */
	void onStart(LongList ids, ScheduleStatusDetailsList stats);
	
	/**
	 * Schedule paused callback.
	 * @param ids
	 * @param stats
	 */
	void onPause(LongList ids, ScheduleStatusDetailsList stats);
	
	/**
	 * Schedule resumed callback.
	 * @param ids
	 * @param stats
	 */
	void onResume(LongList ids, ScheduleStatusDetailsList stats);
	
	/**
	 * Schedule terminated callback.
	 * @param ids
	 * @param stats
	 */
	void onTerminate(LongList ids, ScheduleStatusDetailsList stats);
}
