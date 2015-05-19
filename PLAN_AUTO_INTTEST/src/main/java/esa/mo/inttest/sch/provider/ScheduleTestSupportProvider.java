package esa.mo.inttest.sch.provider;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccsds.moims.mo.automation.schedule.structures.ScheduleStatusDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleStatusDetailsList;
import org.ccsds.moims.mo.automationprototype.scheduletest.provider.ScheduleTestInheritanceSkeleton;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.UpdateType;

import esa.mo.inttest.Dumper;

/**
 * Planning request test support provider. Implemented as little as necessary.
 */
public class ScheduleTestSupportProvider extends ScheduleTestInheritanceSkeleton {

	private static final Logger LOG = Logger.getLogger(ScheduleTestSupportProvider.class.getName());
	
	private ScheduleProvider prov;
	
	/**
	 * Default ctor.
	 */
	public ScheduleTestSupportProvider() {
	}
	
	/**
	 * Set PR provider to use.
	 * @param prov
	 */
	public void setProvider(ScheduleProvider prov) {
		this.prov = prov;
	}
	
	protected void checkLists(LongList ids, ScheduleStatusDetailsList stats) throws MALException {
		if (null == ids) {
			throw new MALException("schedule ids list is null");
		}
		if (null == stats) {
			throw new MALException("schedule statuses list is null");
		}
		if (ids.isEmpty()) {
			throw new MALException("schedule ids list is empty");
		}
		if (ids.size() != stats.size()) {
			throw new MALException("schedule ids count differs from schedule statuses count");
		}
	}
	
	protected void checkElements(int i, LongList ids, ScheduleStatusDetailsList stats) throws MALException {
		Long id = ids.get(i);
		if (null == id) {
			throw new MALException("schedule id[" + i + "] is null");
		}
		ScheduleStatusDetails stat = stats.get(i);
		if (null == stat) {
			throw new MALException("schedule status[" + i + "] is null");
		}
	}
	
	public void updateScheduleStatus(LongList schIds, ScheduleStatusDetailsList schStats, MALInteraction interaction)
			throws MALInteractionException, MALException {
		LOG.log(Level.INFO, "{2}.updateScheduleStatus(schIds={0}, schStatuses={1})",
				new Object[] { schIds, Dumper.schStats(schStats), Dumper.received(interaction) });
		checkLists(schIds, schStats);
		for (int i = 0; (null != schIds) && (i < schIds.size()); ++i) {
			checkElements(i, schIds, schStats);
			Long id = schIds.get(i);
			ScheduleStatusDetails stat = schStats.get(i);
			prov.getInstStore().setStatus(id, stat);
			prov.publish(UpdateType.UPDATE, stat);
		}
		LOG.log(Level.INFO, "{0}.updateScheduleStatus() response: returning nothing", Dumper.sending(interaction));
	}
}
