/*******************************************************************************
 * This source code is proprietary of CGI Estonia AS and covered by copyright.
 * European Space Agency is granted a non-exclusive, free, worldwide license
 * to use this source code without the right to commercialize it. 
 * You may not use this code without prior written consent of CGI Estonia AS.
 *******************************************************************************/
package esa.mo.inttest.ca.consumer;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccsds.moims.mo.com.archive.consumer.ArchiveAdapter;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveStub;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

/**
 * COM Archive consumer for testing.
 */
public class ComArchiveConsumer {

	public static class MyAdapter extends ArchiveAdapter {
		
		@SuppressWarnings("rawtypes")
		@Override
		public void retrieveAckReceived(MALMessageHeader msgHeader, Map qosProperties) {
			LOG.log(Level.INFO, "retrieve ack");
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public void retrieveAckErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
				Map qosProperties) {
			LOG.log(Level.INFO, "retrieve ack error={0}", error);
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public void retrieveResponseReceived(MALMessageHeader msgHeader,
				ArchiveDetailsList objDetails, ElementList objBodies, Map qosProperties) {
			LOG.log(Level.INFO, "retrieve resp: objDetails={0}, objBodies={1}",
					new Object[] { objDetails,  objBodies });
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public void retrieveResponseErrorReceived(MALMessageHeader msgHeader,
				MALStandardError error, Map qosProperties) {
			LOG.log(Level.INFO, "retrieve error={0}", error);
		}
	}
	
	private static final Logger LOG = Logger.getLogger(ComArchiveConsumer.class.getName());

	private ArchiveStub stub;

	public ComArchiveConsumer(ArchiveStub stub) {
		this.stub = stub;
	}
	
	public void retrieveSomething() throws MALException, MALInteractionException {
		
		ObjectType objType = new ObjectType(new UShort(0), new UShort(0), new UOctet((short)0), new UShort(0));
		
		IdentifierList domain = new IdentifierList();
		domain.add(new Identifier("test"));
		
		LongList objIds = new LongList();
		objIds.add(new Long(1L));
		
		LOG.log(Level.INFO, "retrieving..");
		stub.retrieve(objType, domain, objIds, new MyAdapter());
		LOG.log(Level.INFO, "retrieved.");
	}
}
