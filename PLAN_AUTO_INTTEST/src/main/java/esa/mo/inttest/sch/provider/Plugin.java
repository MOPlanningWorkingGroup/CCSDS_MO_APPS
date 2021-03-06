/*******************************************************************************
 * This source code is proprietary of CGI Estonia AS and covered by copyright.
 * European Space Agency is granted a non-exclusive, free, worldwide license
 * to use this source code without the right to commercialize it. 
 * You may not use this code without prior written consent of CGI Estonia AS.
 *******************************************************************************/
package esa.mo.inttest.sch.provider;

import org.ccsds.moims.mo.automation.schedule.structures.ScheduleInstanceDetailsList;
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
	void onSubmit(ScheduleInstanceDetailsList schs);
	
	/**
	 * Schedule update callback.
	 * @param scheds
	 * @param stats
	 */
	void onUpdate(ScheduleInstanceDetailsList schs, ScheduleStatusDetailsList stats);
	
	/**
	 * Schedule remove callback.
	 * @param schIds
	 */
	void onRemove(LongList schIds);
	
	/**
	 * Schedules patched callback.
	 * @param removed
	 * @param updated
	 * @param added
	 * @param stats
	 */
	void onPatch(ScheduleInstanceDetailsList changes, ScheduleStatusDetailsList stats);
	
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
