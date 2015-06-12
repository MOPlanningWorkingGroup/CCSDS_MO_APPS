package esa.mo.inttest.ev;

import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.event.EventHelper;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;

import esa.mo.inttest.FactoryBase;

/**
 * Common factory for Event service. Initializes same Helpers on provider and consumer side.
 */
public abstract class EventFactory extends FactoryBase {

	/**
	 * Implements Helper(s) initialization for Service.
	 * @see esa.mo.inttest.FactoryBase#initHelpers()
	 */
	protected void initHelpers() throws MALException {
		MALHelper.init(MALContextFactory.getElementFactoryRegistry());
		COMHelper.init(MALContextFactory.getElementFactoryRegistry());
		try {
			EventHelper.init(MALContextFactory.getElementFactoryRegistry());
		} catch (MALException e) {
			// ignore "service already included in area" error
		}
	}
}
