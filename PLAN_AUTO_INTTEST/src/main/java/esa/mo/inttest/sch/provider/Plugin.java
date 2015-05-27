package esa.mo.inttest.sch.provider;

import org.ccsds.moims.mo.automation.schedule.structures.ScheduleInstanceDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleInstanceDetailsList;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleStatusDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleStatusDetailsList;

public interface Plugin {

	/**
	 * Set provider to use.
	 * @param prov
	 */
	void setProv(ScheduleProvider prov);
	
	/**
	 * @param sch
	 * @param stat
	 */
	void onSubmit(ScheduleInstanceDetails sch, ScheduleStatusDetails stat);
	
	/**
	 * @param sch
	 * @param stat
	 */
	void onUpdate(ScheduleInstanceDetails sch, ScheduleStatusDetails stat);
	
	/**
	 * @param schId
	 */
	void onRemove(Long schId);
	
	/**
	 * @param removed
	 * @param updated
	 * @param added
	 * @param stats
	 */
	void onPatch(ScheduleInstanceDetailsList removed, ScheduleInstanceDetailsList updated,
			ScheduleInstanceDetailsList added, ScheduleStatusDetailsList stats);
	
	/**
	 * @param sch
	 * @param stat
	 */
	void onStart(ScheduleInstanceDetails sch, ScheduleStatusDetails stat);
	
	/**
	 * @param sch
	 * @param stat
	 */
	void onPause(ScheduleInstanceDetails sch, ScheduleStatusDetails stat);
	
	/**
	 * @param sch
	 * @param stat
	 */
	void onResume(ScheduleInstanceDetails sch, ScheduleStatusDetails stat);
	
	/**
	 * @param sch
	 * @param stat
	 */
	void onTerminate(ScheduleInstanceDetails sch, ScheduleStatusDetails stat);
}
