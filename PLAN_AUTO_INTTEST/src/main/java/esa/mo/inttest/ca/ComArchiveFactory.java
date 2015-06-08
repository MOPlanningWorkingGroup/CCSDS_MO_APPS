package esa.mo.inttest.ca;

import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.archive.ArchiveHelper;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALService;

import esa.mo.inttest.FactoryBase;

/**
 * Common factory for COM Archive. Initializes same Helpers on provider and consumer side.
 */
public abstract class ComArchiveFactory extends FactoryBase {

	/**
	 * Implements Helper(s) initialization for Service.
	 * @see esa.mo.inttest.FactoryBase#initHelpers()
	 */
	protected void initHelpers() throws MALException {
		MALService tmp = COMHelper.COM_AREA.getServiceByName(ArchiveHelper.ARCHIVE_SERVICE_NAME);
		if (tmp == null) {
			MALHelper.init(MALContextFactory.getElementFactoryRegistry());
			COMHelper.init(MALContextFactory.getElementFactoryRegistry());
			ArchiveHelper.init(MALContextFactory.getElementFactoryRegistry());
		}
	}
}
