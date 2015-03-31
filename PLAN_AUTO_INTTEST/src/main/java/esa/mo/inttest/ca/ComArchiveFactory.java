package esa.mo.inttest.ca;

import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.archive.ArchiveHelper;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALService;

import esa.mo.inttest.FactoryBase;

/**
 * Common factory for PR. Initializes same Helpers on consumer and provider side.
 */
public abstract class ComArchiveFactory extends FactoryBase {

	protected void initHelpers() throws MALException {
		MALService tmp = COMHelper.COM_AREA.getServiceByName(ArchiveHelper.ARCHIVE_SERVICE_NAME);
		if (tmp == null) {
			MALHelper.init(MALContextFactory.getElementFactoryRegistry());
			COMHelper.init(MALContextFactory.getElementFactoryRegistry());
			ArchiveHelper.init(MALContextFactory.getElementFactoryRegistry());
		}
	}
}
