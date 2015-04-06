package esa.mo.inttest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.ccsds.moims.mo.mal.MALContext;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;

/**
 * Basic factory. Loads properties and initializes MAL context.
 */
public abstract class FactoryBase {

	protected String propertyFile = null;
	protected MALContext malCtx = null;
	protected IdentifierList domain = new IdentifierList();
	
	/**
	 * Default ctor.
	 */
	public FactoryBase() {
		domain.add(new Identifier("desd"));
	}
	
	/**
	 * Set domain to use.
	 * @param domain
	 */
	public void setDomain(IdentifierList domain) {
		this.domain = domain;
	}
	
	/**
	 * Returns used domain.
	 * @return
	 */
	public IdentifierList getDomain() {
		return this.domain;
	}
	
	/**
	 * Set proprty file to load properties from.
	 * @param fn
	 */
	public void setPropertyFile(String fn) {
		propertyFile = fn;
	}
	
	/**
	 * Loads properties from file to System.properties.
	 * @throws IOException
	 */
	protected void initProperties() throws IOException {
		InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(propertyFile);
		Properties props = new Properties();
		props.load(is);
		is.close();
		System.getProperties().putAll(props);
	}
	
	/**
	 * Initializes MAL context.
	 * @throws MALException
	 */
	protected void initContext() throws MALException {
		if (null == malCtx) {
			malCtx = MALContextFactory.newFactory().createMALContext(System.getProperties());
		}
	}
	
	/**
	 * Initialize provider/consumer specific Helpers.
	 * @throws MALException
	 */
	protected abstract void initHelpers() throws MALException;
	
	/**
	 * Initializes.
	 * @throws IOException
	 * @throws MALException
	 */
	protected void init() throws IOException, MALException {
		initProperties();
		initContext();
		initHelpers();
	}
	
	/**
	 * Closes MAL context.
	 * @throws MALException
	 */
	protected void close() throws MALException {
		if (malCtx != null) {
			malCtx.close();
		}
		malCtx = null;
	}
}
