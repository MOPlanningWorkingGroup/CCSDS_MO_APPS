package esa.mo.inttest.pr.provider;

import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.EntityKey;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskInstanceDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskStatusDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskStatusDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentDefinitionDetails;
import org.ccsds.moims.mo.planningdatatypes.structures.ArgumentDefinitionDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.AttributeValue;
import org.ccsds.moims.mo.planningdatatypes.structures.AttributeValueList;
import org.ccsds.moims.mo.planningdatatypes.structures.TimeTrigger;
import org.ccsds.moims.mo.planningdatatypes.structures.TriggerDetails;
import org.ccsds.moims.mo.planningdatatypes.structures.TriggerDetailsList;

/**
 * Pretty printing for planning request data structures.
 */
public final class Dumper {

	private static final String STEP = "  ";
	
	private Dumper() {
	}
	
	private static String dumpAttr(Byte attr) {
		String s = attr.toString();
		if (Attribute.STRING_TYPE_SHORT_FORM == (short)attr) {
			s = "String";
		} else if (Attribute.TIME_TYPE_SHORT_FORM == (short)attr) {
			s = "Time";
		} else if (Attribute.OCTET_TYPE_SHORT_FORM == (short)attr) {
			s = "Octet";
		} else if (Attribute.UOCTET_TYPE_SHORT_FORM == (short)attr) {
			s = "UOctet";
		}
		return s;
	}
	
	private static String dumpArg(ArgumentDefinitionDetails arg) {
		StringBuilder s = new StringBuilder();
		if (null != arg) {
			s.append("{ name=\"").append(arg.getName()).append("\", ");
//			s.append("description=\"").append(arg.getDescription()).append("\", ");
			s.append("attrType=").append(dumpAttr(arg.getAttributeType())).append(", ");
			s.append("attrArea=").append(arg.getArea()).append(" }");
		} else {
			s.append("null");
		}
		return s.toString();
	}
	
	private static String dumpArgDefs(ArgumentDefinitionDetailsList args, String ind) {
		StringBuilder s = new StringBuilder();
		if (null != args) {
			s.append("list (size=").append(args.size()).append(") [").append(args.isEmpty() ? "]" : "").append("\n");
			for (int i = 0; i < args.size(); ++i) {
				s.append(ind).append(STEP).append(i).append(": ").append(dumpArg(args.get(i))).append("\n");
			}
			s.append(args.isEmpty() ? "" : ind + "]");
		} else {
			s.append("null");
		}
		return s.toString();
	}
	
	private static String dumpTaskDef(TaskDefinitionDetails td, String ind) {
		StringBuilder s = new StringBuilder();
		if (null != td) {
			s.append("{\n");
			s.append(ind).append(STEP).append("name=\"").append(td.getName()).append("\",\n");
			s.append(ind).append(STEP).append("description=\"").append(td.getDescription()).append("\",\n");
			s.append(ind).append(STEP).append("prDefName=\"").append(td.getPrDefName()).append("\",\n");
//			s.append(ind).append(STEP).append("fields=").append(dumpArgDefs(td.getFields(), ind+STEP)).append(",\n");
//			s.append(ind).append(STEP).append("arguments=").append(dumpArgDefs(td.getArguments(), ind+STEP)).append("\n");
			s.append(ind).append(STEP).append("argDefs=").append(dumpArgDefs(td.getArgumentDefs(), ind+STEP)).append("\n");
			s.append(ind).append("}");
		} else {
			s.append("null");
		}
		return s.toString();
	}
	
	public static String taskDefs(TaskDefinitionDetailsList tdl) {
		StringBuilder s = new StringBuilder();
		if (null != tdl) {
			s.append("list (size=").append(tdl.size()).append(") [").append(tdl.isEmpty() ? "]" : "").append("\n");
			for (int i = 0; i < tdl.size(); ++i) {
				s.append(STEP).append(i).append(": ").append(dumpTaskDef(tdl.get(i), STEP)).append("\n");
			}
			s.append(tdl.isEmpty() ? "" : "]");
		} else {
			s.append("null");
		}
		return s.toString();
	}
	
	private static String dumpNames(IdentifierList il, String ind) {
		StringBuilder s = new StringBuilder();
		if (null != il) {
			s.append("list (size=").append(il.size()).append(") [").append(il.isEmpty() ? "]" : "").append("\n");
			for (int i = 0; i < il.size(); ++i) {
				s.append(ind).append(STEP).append(i).append(": \"").append(il.get(i)).append("\"\n");
			}
			s.append(il.isEmpty() ? "" : ind + "]");
		} else {
			s.append("null");
		}
		return s.toString();
	}
	
	private static String dumpPrDef(PlanningRequestDefinitionDetails prd, String ind) {
		StringBuilder s = new StringBuilder();
		if (null != prd) {
			s.append("{\n");
			s.append(ind).append(STEP).append("name=\"").append(prd.getName()).append("\",\n");
			s.append(ind).append(STEP).append("description=\"").append(prd.getDescription()).append("\",\n");
//			s.append(ind).append(STEP).append("fields=").append(dumpArgDefs(prd.getFields(), ind+STEP)).append(",\n");
			s.append(ind).append(STEP).append("argDefs=").append(dumpArgDefs(prd.getArgumentDefs(), ind+STEP)).append(",\n");
			s.append(ind).append(STEP).append("taskDefNames=").append(dumpNames(prd.getTaskDefNames(), ind+STEP)).append("\n");
			s.append(ind).append("}");
		} else {
			s.append("null");
		}
		return s.toString();
	}
	
	public static String prDefs(PlanningRequestDefinitionDetailsList prdl) {
		StringBuilder s = new StringBuilder();
		if (null != prdl) {
			s.append("(size=").append(prdl.size()).append(") [").append(prdl.isEmpty() ? "]" : "").append("\n");
			for (int i = 0; i < prdl.size(); ++i) {
				s.append(STEP).append(i).append(": ").append(dumpPrDef(prdl.get(i), STEP)).append("\n");
			}
			s.append(prdl.isEmpty() ? "" : "]");
		} else {
			s.append("null");
		}
		return s.toString();
	}
	
	private static String dumpAttrVal(AttributeValue av) {
		StringBuilder s = new StringBuilder();
		if (null != av) {
			s.append("{ value=\"").append(av.getValue()).append("\", ");
			s.append("type=").append(dumpAttr((byte)(0xff & av.getValue().getTypeShortForm()))).append(" }");
		} else {
			s.append("null");
		}
		return s.toString();
	}
	
	private static String dumpAttrVals(AttributeValueList avl, String ind) {
		StringBuilder s = new StringBuilder();
		if (null != avl) {
			s.append("list (size=").append(avl.size()).append(") [").append(avl.isEmpty() ? "]" : "").append("\n");
			for (int i = 0; i < avl.size(); ++i) {
				s.append(ind).append(STEP).append(i).append(": ").append(dumpAttrVal(avl.get(i))).append("\n");
			}
			s.append(ind).append(avl.isEmpty() ? "" : "]");
		} else {
			s.append("null");
		}
		return s.toString();
	}
	
	private static String dumpTimeTrig(TimeTrigger t) {
		StringBuilder s = new StringBuilder();
		if (null != t) {
			s.append("{ value=").append(t.getTimeValue()).append(", ");
			s.append("isAbsoluteTime=").append(t.getIsAbsoluteTime()).append(" }");
		} else {
			s.append("null");
		}
		return s.toString();
	}
	
	private static String dumpTrigger(TriggerDetails t, String ind) {
		StringBuilder s = new StringBuilder();
		if (null != t) {
			s.append("{\n");
			s.append(ind).append(STEP).append("triggerName=\"").append(t.getTriggerName()).append("\",\n");
			s.append(ind).append(STEP).append("triggerType=\"").append(t.getTriggerType()).append("\",\n");
			s.append(ind).append(STEP).append("timeTrigger=").append(dumpTimeTrig(t.getTimeTrigger())).append(",\n");
			s.append(ind).append(STEP).append("eventTrigger=").append(t.getEventTrigger()).append("\n");
			s.append(ind).append("}");
		} else {
			s.append("null");
		}
		return s.toString();
	}
	
	private static String dumpTriggers(TriggerDetailsList tl, String ind) {
		StringBuilder s = new StringBuilder();
		if (null != tl) {
			s.append("list (size=").append(tl.size()).append(") [").append(tl.isEmpty() ? "]" : "").append("\n");
			for (int i = 0; i < tl.size(); ++i) {
				s.append(ind).append(STEP).append(i).append(": ").append(dumpTrigger(tl.get(i), ind+STEP)).append("\n");
			}
			s.append(ind).append(tl.isEmpty() ? "" : "]");
		} else {
			s.append("null");
		}
		return s.toString();
	}
	
	private static String dumpTaskInst(TaskInstanceDetails ti, String ind) {
		StringBuilder s = new StringBuilder();
		if (null != ti) {
			s.append("{\n");
			s.append(ind).append(STEP).append("name=\"").append(ti.getName()).append("\",\n");
			s.append(ind).append(STEP).append("prName=\"").append(ti.getPrName()).append("\",\n");
			s.append(ind).append(STEP).append("description=\"").append(ti.getDescription()).append("\",\n");
//			s.append(ind).append(STEP).append("fieldValues=").append(dumpAttrVals(ti.getFieldValues(), ind+STEP)).append(",\n");
			s.append(ind).append(STEP).append("argDefNames=").append(dumpNames(ti.getArgumentDefNames(), ind+STEP)).append(",\n");
			s.append(ind).append(STEP).append("argValues=").append(dumpAttrVals(ti.getArgumentValues(), ind+STEP)).append(",\n");
			s.append(ind).append(STEP).append("timingConstraints=").append(dumpTriggers(ti.getTimingConstraints(), ind+STEP)).append("\n");
			s.append(ind).append("}");
		} else {
			s.append("null");
		}
		return s.toString();
	}
	
	private static String dumpTaskInsts(TaskInstanceDetailsList til, String ind) {
		StringBuilder s = new StringBuilder();
		if (null != til) {
			s.append("list (size=").append(til.size()).append(") [").append(til.isEmpty() ? "]" : "").append("\n");
			for (int i = 0; i < til.size(); ++i) {
				s.append(ind).append(STEP).append(i).append(": ").append(dumpTaskInst(til.get(i), ind+STEP)).append("\n");
			}
			s.append(ind).append(til.isEmpty() ? "" : "]");
		} else {
			s.append("null");
		}
		return s.toString();
	}
	
	public static String prInst(PlanningRequestInstanceDetails pri) {
		StringBuilder s = new StringBuilder();
		if (null != pri) {
			s.append("{\n");
			s.append(STEP).append("name=\"").append(pri.getName()).append("\",\n");
			s.append(STEP).append("description=\"").append(pri.getDescription()).append("\",\n");
//			s.append(STEP).append("fieldValues=").append(dumpAttrVals(pri.getFieldValues(), STEP)).append(",\n");
			s.append(STEP).append("argDefNames=").append(dumpNames(pri.getArgumentDefNames(), STEP)).append(",\n");
			s.append(STEP).append("argValues=").append(dumpAttrVals(pri.getArgumentValues(), STEP)).append(",\n");
			s.append(STEP).append("timingConstraints=").append(dumpTriggers(pri.getTimingConstraints(), STEP)).append(",\n");
			s.append(STEP).append("tasks=").append(dumpTaskInsts(pri.getTasks(), STEP)).append("\n");
			s.append("}");
		} else {
			s.append("null");
		}
		return s.toString();
	}
	
	private static String dumpEntKey(EntityKey ek) {
		StringBuilder s = new StringBuilder();
		s.append("{ first=\"").append(ek.getFirstSubKey().getValue()).append("\", ");
		s.append("second=").append(ek.getSecondSubKey()).append(", ");
		s.append("third=").append(ek.getThirdSubKey()).append(", ");
		s.append("fourth=").append(ek.getFourthSubKey()).append(" }");
		return s.toString();
	}
	
	private static String dumpUpdHdr(UpdateHeader uh, String ind) {
		StringBuilder s = new StringBuilder("{\n");
		s.append(ind).append(STEP).append("key=").append(dumpEntKey(uh.getKey())).append(",\n");
		s.append(ind).append(STEP).append("sourceUri=\"").append(uh.getSourceURI().getValue()).append("\",\n");
		s.append(ind).append(STEP).append("timeStamp=").append(uh.getTimestamp()).append(",\n");
		s.append(ind).append(STEP).append("updateType=").append(uh.getUpdateType()).append(",\n");
		s.append(STEP).append("}");
		return s.toString();
	}
	
	public static String updHdrs(UpdateHeaderList uhl) {
		StringBuilder s = new StringBuilder();
		if (null != uhl) {
			s.append("list (size=").append(uhl.size()).append(") [").append(uhl.isEmpty() ? "]" : "").append("\n");
			for (int i = 0; i < uhl.size(); ++i) {
				s.append(STEP).append(i).append(": ").append(dumpUpdHdr(uhl.get(i), STEP)).append("\n");
			}
			s.append(uhl.isEmpty() ? "" : "]");
		} else {
			s.append("null");
		}
		return s.toString();
	}
	
	private static String dumpObjKey(ObjectKey ok, String ind) {
		StringBuilder s = new StringBuilder("{\n");
		s.append(ind).append(STEP).append("instId=").append(ok.getInstId()).append(",\n");
		s.append(ind).append(STEP).append("domain=").append(dumpNames(ok.getDomain(), ind+STEP)).append("\n");
		s.append(ind).append("}");
		return s.toString();
	}
	
	private static String dumpObjId(ObjectId oi, String ind) {
		StringBuilder s = new StringBuilder("{\n");
		s.append(ind).append(STEP).append("key=").append(dumpObjKey(oi.getKey(), ind+STEP)).append("\n");
		s.append(ind).append(STEP).append("type=").append(oi.getType()).append("\n");
		s.append(ind).append("}");
		return s.toString();
	}
	
	public static String objIds(ObjectIdList oil) {
		StringBuilder s = new StringBuilder();
		if (null != oil) {
			s.append("list (size=").append(oil.size()).append(") [").append(oil.isEmpty() ? "]" : "").append("\n");
			for (int i = 0; i < oil.size(); ++i) {
				s.append(STEP).append(i).append(": ").append(dumpObjId(oil.get(i), STEP)).append("\n");
			}
			s.append(oil.isEmpty() ? "" : "]");
		} else {
			s.append("null");
		}
		return s.toString();
	}
	
	private static String dumpPrStat(PlanningRequestStatusDetails prs, String ind) {
		StringBuilder s = new StringBuilder("{\n");
		s.append(ind).append(STEP).append("accepted=").append(prs.getAccepted()).append(",\n");
		s.append(ind).append(STEP).append("distForExec=").append(prs.getDistributedForExecution()).append(",\n");
		s.append(ind).append(STEP).append("execCompleted=").append(prs.getExecuteCompleted()).append(",\n");
		s.append(ind).append(STEP).append("execFailed=").append(prs.getExecuteFailed()).append(",\n");
		s.append(ind).append(STEP).append("invalid=").append(prs.getInvalid()).append(",\n");
		s.append(ind).append(STEP).append("lastModif=").append(prs.getLastModified()).append(",\n");
		s.append(ind).append(STEP).append("planConflict=").append(prs.getPlanConflict()).append(",\n");
		s.append(ind).append(STEP).append("planned=").append(prs.getPlanned()).append(",\n");
		s.append(ind).append(STEP).append("prInstName=").append(prs.getPrInstName()).append(",\n");
		s.append(ind).append(STEP).append("rejected=").append(prs.getRejected()).append(",\n");
		s.append(ind).append(STEP).append("scheduled=").append(prs.getScheduled()).append(",\n");
		s.append(ind).append(STEP).append("submitted=").append(prs.getSubmitted()).append(",\n");
		s.append(ind).append(STEP).append("taskStatuses=").append(prs.getTaskStatuses()).append(",\n");
		s.append(ind).append(STEP).append("validated=").append(prs.getValidated()).append("\n");
		s.append(ind).append("}");
		return s.toString();
	}
	
	public static String prStats(PlanningRequestStatusDetailsList prsl) {
		StringBuilder s = new StringBuilder();
		if (null != prsl) {
			s.append("list (size=").append(prsl.size()).append(") [").append(prsl.isEmpty() ? "]" : "").append("\n");
			for (int i = 0; i < prsl.size(); ++i) {
				s.append(STEP).append(i).append(": ").append(dumpPrStat(prsl.get(i), STEP)).append("\n");
			}
			s.append(prsl.isEmpty() ? "" : "]");
		} else {
			s.append("null");
		}
		return s.toString();
	}
	
	private static String dumpTaskStat(TaskStatusDetails ts, String ind) {
		StringBuilder s = new StringBuilder("{\n");
		s.append(ind).append(STEP).append("accepted=").append(ts.getAccepted()).append(",\n");
		s.append(ind).append(STEP).append("distForExec=").append(ts.getDistributedForExecution()).append(",\n");
		s.append(ind).append(STEP).append("execCompleted=").append(ts.getExecuteCompleted()).append(",\n");
		s.append(ind).append(STEP).append("execFailed=").append(ts.getExecuteFailed()).append(",\n");
		s.append(ind).append(STEP).append("invalid=").append(ts.getInvalid()).append(",\n");
		s.append(ind).append(STEP).append("lastModif=").append(ts.getLastModified()).append(",\n");
		s.append(ind).append(STEP).append("planConflict=").append(ts.getPlanConflict()).append(",\n");
		s.append(ind).append(STEP).append("planned=").append(ts.getPlanned()).append(",\n");
		s.append(ind).append(STEP).append("rejected=").append(ts.getRejected()).append(",\n");
		s.append(ind).append(STEP).append("scheduled=").append(ts.getScheduled()).append(",\n");
		s.append(ind).append(STEP).append("submitted=").append(ts.getSubmitted()).append(",\n");
		s.append(ind).append(STEP).append("taskInstName=").append(ts.getTaskInstName()).append(",\n");
		s.append(ind).append(STEP).append("validated=").append(ts.getValidated()).append(",\n");
		s.append(ind).append("}");
		return s.toString();
	}
	
	public static String taskStats(TaskStatusDetailsList tsl) {
		StringBuilder s = new StringBuilder();
		if (null != tsl) {
			s.append("list (size=").append(tsl.size()).append(") [").append(tsl.isEmpty() ? "]" : "").append("\n");
			for (int i = 0; i < tsl.size(); ++i) {
				s.append(STEP).append(i).append(": ").append(dumpTaskStat(tsl.get(i), STEP)).append("\n");
			}
			s.append(tsl.isEmpty() ? "" : "]");
		} else {
			s.append("null");
		}
		return s.toString();
	}
}
