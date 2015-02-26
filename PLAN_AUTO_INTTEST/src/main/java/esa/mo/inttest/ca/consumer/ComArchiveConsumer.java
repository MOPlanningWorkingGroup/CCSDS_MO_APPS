package esa.mo.inttest.ca.consumer;

import java.util.Map;

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
		ArchiveAdapter adapter = new ArchiveAdapter() {
			
			@SuppressWarnings("rawtypes")
			@Override
			public void retrieveAckReceived(MALMessageHeader msgHeader, Map qosProperties) {
			}
			
			@SuppressWarnings("rawtypes")
			@Override
			public void retrieveAckErrorReceived(MALMessageHeader msgHeader, MALStandardError error, Map qosProperties) {
			}
			
			@SuppressWarnings("rawtypes")
			@Override
			public void retrieveResponseReceived(MALMessageHeader msgHeader, ArchiveDetailsList objDetails,
					ElementList objBodies, Map qosProperties) {
			}
			
			@SuppressWarnings("rawtypes")
			@Override
			public void retrieveResponseErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
					Map qosProperties) {
			}
		};
		stub.retrieve(objType, domain, objIds, adapter);
	}
}
