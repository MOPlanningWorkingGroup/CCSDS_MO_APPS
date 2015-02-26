package esa.mo.inttest.ca.provider;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.archive.ArchiveHelper;
import org.ccsds.moims.mo.mal.MALContext;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALService;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.provider.MALProviderManager;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.URI;

/**
 * COM Archive provider factory.
 */
public class ComArchiveProviderFactory {

	private String propertyFile = null;
	private MALContext malCtx = null;
	private ComArchiveProvider prov = null;
	private MALProviderManager malProvMgr = null;
	private MALProvider malProv = null;
	private URI sharedBrokerUri = null;

	public void setPropertyFile(String fn) {
		propertyFile = fn;
	}
	
	private void initProperties() throws IOException {
		InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(propertyFile);
		Properties props = new Properties();
		props.load(is);
		is.close();
		System.getProperties().putAll(props);
	}
	
	private void initContext() throws MALException {
		malCtx = MALContextFactory.newFactory().createMALContext(System.getProperties());
	}

	private void initHelpers() throws MALException {
		MALHelper.init(MALContextFactory.getElementFactoryRegistry());
		COMHelper.init(MALContextFactory.getElementFactoryRegistry());
		MALService tmp = COMHelper.COM_AREA.getServiceByName(ArchiveHelper.ARCHIVE_SERVICE_NAME);
		if (tmp == null) {
			ArchiveHelper.init(MALContextFactory.getElementFactoryRegistry());
		}
	}
	
	public void setSharedBrokerUri(URI uri) {
		sharedBrokerUri = uri;
	}
	
	private void initProvider() throws MALException {
		malProvMgr = malCtx.createProviderManager();
		prov = new ComArchiveProvider();
		String provName = "testCaProv";
		String proto = "rmi";
		Blob authId = new Blob("".getBytes());
		QoSLevel[] expQos = { QoSLevel.ASSURED, };
		UInteger priority = new UInteger(1L);
		boolean isPublisher = true;
		
		malProv = malProvMgr.createProvider(provName, proto, ArchiveHelper.ARCHIVE_SERVICE,
				authId, prov, expQos, priority, System.getProperties(), isPublisher, sharedBrokerUri);
	}

	public void start() throws IOException, MALException, MALInteractionException {
		initProperties();
		initContext();
		initHelpers();
		initProvider();
	}
	
	public URI getProviderUri() {
		return malProv.getURI();
	}
	
	public URI getBrokerUri() {
		return malProv.getBrokerURI();
	}
	
	public void stop() throws MALException, MALInteractionException {
		if (malProv != null) {
			malProv.close();
		}
		malProv = null;
		if (malProvMgr != null) {
			malProvMgr.close();
		}
		malProvMgr = null;
		prov = null;
		if (malCtx != null) {
			malCtx.close();
		}
		malCtx = null;
	}
}
