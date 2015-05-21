package esa.mo.inttest.bepicolombo;

import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentDefinitionDetails;

public class CommandRequestResponseFile {

	protected ArgumentDefinitionDetails createArgDef(String name, String desc, int type) {
		byte aType = (byte)(type & 0xff);
		return new ArgumentDefinitionDetails(new Identifier(name), desc, aType, null, null, null, null);
	}
	
}
