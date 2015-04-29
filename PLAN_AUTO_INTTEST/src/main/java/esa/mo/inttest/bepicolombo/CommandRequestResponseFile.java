package esa.mo.inttest.bepicolombo;

import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestResponseDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestResponseInstanceDetails;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentDefinitionDetails;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentDefinitionDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.AttributeValue;
import org.ccsds.moims.mo.planningdatatypes.structures.AttributeValueList;

import esa.mo.inttest.Util;

public class CommandRequestResponseFile {

	protected ArgumentDefinitionDetails createArgDef(String name, String desc, int type) {
		byte aType = (byte)(type & 0xff);
		return new ArgumentDefinitionDetails(new Identifier(name), desc, aType, null, null, null, null);
	}
	
	public PlanningRequestResponseDefinitionDetails createRespDef(String prDefName) {
		PlanningRequestResponseDefinitionDetails def = new PlanningRequestResponseDefinitionDetails();
		def.setName(new Identifier("CRR-Def"));
		def.setPrDefName(new Identifier(prDefName));
		ArgumentDefinitionDetailsList argDefs = new ArgumentDefinitionDetailsList();
		argDefs.add(createArgDef("errorCount", null, Attribute.USHORT_TYPE_SHORT_FORM));
		// pipe char '|' messes up MAL encoding
		argDefs.add(createArgDef("error", "Error in format 'errorCode:message'", Attribute.STRING_TYPE_SHORT_FORM));
		def.setArgumentDefs(argDefs);
		return def;
	}
	
	protected void addRespArg(PlanningRequestResponseInstanceDetails inst, String name, Attribute val) {
		if (null == inst.getArgumentDefNames()) {
			inst.setArgumentDefNames(new IdentifierList());
		}
		if (null == inst.getArgumentValues()) {
			inst.setArgumentValues(new AttributeValueList());
		}
		inst.getArgumentDefNames().add(new Identifier(name));
		inst.getArgumentValues().add((null != val) ? new AttributeValue(val) : null);
	}
	
	public PlanningRequestResponseInstanceDetails createRespInst(String prInstName) {
		PlanningRequestResponseInstanceDetails inst = new PlanningRequestResponseInstanceDetails();
		inst.setPrInstName(new Identifier(prInstName));
		inst.setTimestamp(Util.currentTime());
		addRespArg(inst, "errorCount", new UShort(2));
		// pipe char '|' messes up MAL encoding
		addRespArg(inst, "error", new Union("12:The first error (missing informatino)"));
		addRespArg(inst, "error", new Union("64:The second error (invalid execution time value)"));
		return inst;
	}
}
