package esa.mo.inttest.pr.provider;

import java.util.List;

import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.EntityKey;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.planning.planningrequest.structures.BaseDefinition;
import org.ccsds.moims.mo.planning.planningrequest.structures.BaseDefinitionList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestResponseInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestResponseInstanceDetailsList;
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
import org.ccsds.moims.mo.planningdatatypes.structures.StatusRecord;
import org.ccsds.moims.mo.planningdatatypes.structures.StatusRecordList;
import org.ccsds.moims.mo.planningdatatypes.structures.TimeTrigger;
import org.ccsds.moims.mo.planningdatatypes.structures.TriggerDetails;
import org.ccsds.moims.mo.planningdatatypes.structures.TriggerDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.TriggerName;
import org.ccsds.moims.mo.planningdatatypes.structures.TriggerType;

/**
 * Pretty printing for planning request data structures.
 */
public final class Dumper {

	private static final String STEP = "  ";
	private static final String NULL = "null";
	
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
	
	private static String quote(String s) {
		String z = NULL;
		if (null != s && !s.equals(NULL)) {
			z = "\"" + s + "\"";
		}
		return z;
	}
	
	private static String dumpId(Identifier id) {
		StringBuilder s = new StringBuilder();
		if (null != id) {
			s.append(quote(id.getValue()));
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	private static String dumpArg(ArgumentDefinitionDetails arg) {
		StringBuilder s = new StringBuilder();
		if (null != arg) {
			s.append("{ name=").append(dumpId(arg.getName()));
			s.append(", attrType=").append(dumpAttr(arg.getAttributeType()));
			s.append(", attrArea=").append(arg.getArea());
			s.append(" }");
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	@SuppressWarnings("rawtypes")
	private static void openList(StringBuilder s, List list) {
		s.append("list (size=").append(list.size()).append(") [").append(list.isEmpty() ? "]" : "").append("\n");
	}
	
	@SuppressWarnings("rawtypes")
	private static void closeList(StringBuilder s, List list, String ind) {
		s.append(list.isEmpty() ? "" : ind + "]");
	}
	
	private static String dumpArgDefs(ArgumentDefinitionDetailsList args, String ind) {
		StringBuilder s = new StringBuilder();
		if (null != args) {
			openList(s, args);
			for (int i = 0; i < args.size(); ++i) {
				s.append(ind).append(STEP).append(i).append(": ").append(dumpArg(args.get(i))).append("\n");
			}
			closeList(s, args, ind);
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	private static String dumpTaskDef(TaskDefinitionDetails td, String ind) {
		StringBuilder s = new StringBuilder();
		if (null != td) {
			s.append("{\n");
			s.append(ind).append(STEP).append("name=").append(dumpId(td.getName())).append(",\n");
			s.append(ind).append(STEP).append("description=").append(quote(td.getDescription())).append(",\n");
			s.append(ind).append(STEP).append("prDefName=").append(dumpId(td.getPrDefName())).append(",\n");
			s.append(ind).append(STEP).append("argDefs=").append(dumpArgDefs(td.getArgumentDefs(), ind+STEP)).append("\n");
			s.append(ind).append("}");
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	public static String taskDefs(TaskDefinitionDetailsList tdl) {
		StringBuilder s = new StringBuilder();
		if (null != tdl) {
			openList(s, tdl);
			for (int i = 0; i < tdl.size(); ++i) {
				s.append(STEP).append(i).append(": ").append(dumpTaskDef(tdl.get(i), STEP)).append("\n");
			}
			closeList(s, tdl, "");
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	private static String dumpNames(IdentifierList il, String ind) {
		StringBuilder s = new StringBuilder();
		if (null != il) {
			openList(s, il);
			for (int i = 0; i < il.size(); ++i) {
				s.append(ind).append(STEP).append(i).append(": \"").append(il.get(i)).append("\"\n");
			}
			closeList(s, il, ind);
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	public static String names(IdentifierList il) {
		return dumpNames(il, "");
	}
	
	private static String dumpPrDef(PlanningRequestDefinitionDetails prd, String ind) {
		StringBuilder s = new StringBuilder();
		if (null != prd) {
			s.append("{\n");
			s.append(ind).append(STEP).append("name=").append(dumpId(prd.getName())).append(",\n");
			s.append(ind).append(STEP).append("description=").append(quote(prd.getDescription())).append(",\n");
			s.append(ind).append(STEP).append("argDefs=").append(dumpArgDefs(prd.getArgumentDefs(), ind+STEP)).append(",\n");
			s.append(ind).append(STEP).append("taskDefNames=").append(dumpNames(prd.getTaskDefNames(), ind+STEP)).append("\n");
			s.append(ind).append("}");
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	public static String prDefs(PlanningRequestDefinitionDetailsList prdl) {
		StringBuilder s = new StringBuilder();
		if (null != prdl) {
			openList(s, prdl);
			for (int i = 0; i < prdl.size(); ++i) {
				s.append(STEP).append(i).append(": ").append(dumpPrDef(prdl.get(i), STEP)).append("\n");
			}
			closeList(s, prdl, "");
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	private static String dumpAttrVal(AttributeValue av) {
		StringBuilder s = new StringBuilder();
		if (null != av) {
			s.append("{ value=").append(av.getValue());
			s.append(", type=").append(dumpAttr((byte)(0xff & av.getValue().getTypeShortForm())));
			s.append(" }");
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	private static String dumpAttrVals(AttributeValueList avl, String ind) {
		StringBuilder s = new StringBuilder();
		if (null != avl) {
			openList(s, avl);
			for (int i = 0; i < avl.size(); ++i) {
				s.append(ind).append(STEP).append(i).append(": ").append(dumpAttrVal(avl.get(i))).append("\n");
			}
			closeList(s, avl, ind);
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	private static String dumpTimeTrig(TimeTrigger t) {
		StringBuilder s = new StringBuilder();
		if (null != t) {
			s.append("{ value=").append(t.getTimeValue());
			s.append(", isAbsoluteTime=").append(t.getIsAbsoluteTime());
			s.append(" }");
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	private static String dumpTrigName(TriggerName tn) {
		return (null != tn) ? quote(tn.toString()) : NULL;
	}
	
	private static String dumpTrigType(TriggerType tt) {
		return (null != tt) ? quote(tt.toString()) : NULL;
	}
	
	private static String dumpTrigger(TriggerDetails t, String ind) {
		StringBuilder s = new StringBuilder();
		if (null != t) {
			s.append("{\n");
			s.append(ind).append(STEP).append("triggerName=").append(dumpTrigName(t.getTriggerName())).append(",\n");
			s.append(ind).append(STEP).append("triggerType=").append(dumpTrigType(t.getTriggerType())).append(",\n");
			s.append(ind).append(STEP).append("timeTrigger=").append(dumpTimeTrig(t.getTimeTrigger())).append(",\n");
			s.append(ind).append(STEP).append("eventTrigger=").append(t.getEventTrigger()).append("\n");
			s.append(ind).append("}");
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	private static String dumpTriggers(TriggerDetailsList tl, String ind) {
		StringBuilder s = new StringBuilder();
		if (null != tl) {
			openList(s, tl);
			for (int i = 0; i < tl.size(); ++i) {
				s.append(ind).append(STEP).append(i).append(": ").append(dumpTrigger(tl.get(i), ind+STEP)).append("\n");
			}
			closeList(s, tl, ind);
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	private static String dumpTaskInst(TaskInstanceDetails ti, String ind) {
		StringBuilder s = new StringBuilder();
		if (null != ti) {
			s.append("{\n");
			s.append(ind).append(STEP).append("name=").append(dumpId(ti.getName())).append(",\n");
			s.append(ind).append(STEP).append("prName=").append(dumpId(ti.getPrName())).append(",\n");
			s.append(ind).append(STEP).append("description=").append(quote(ti.getDescription())).append(",\n");
			s.append(ind).append(STEP).append("argDefNames=").append(dumpNames(ti.getArgumentDefNames(), ind+STEP)).append(",\n");
			s.append(ind).append(STEP).append("argValues=").append(dumpAttrVals(ti.getArgumentValues(), ind+STEP)).append(",\n");
			s.append(ind).append(STEP).append("timingConstraints=").append(dumpTriggers(ti.getTimingConstraints(), ind+STEP)).append("\n");
			s.append(ind).append("}");
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	private static String dumpTaskInsts(TaskInstanceDetailsList til, String ind) {
		StringBuilder s = new StringBuilder();
		if (null != til) {
			openList(s, til);
			for (int i = 0; i < til.size(); ++i) {
				s.append(ind).append(STEP).append(i).append(": ").append(dumpTaskInst(til.get(i), ind+STEP)).append("\n");
			}
			closeList(s, til, ind);
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	public static String prInst(PlanningRequestInstanceDetails pri) {
		StringBuilder s = new StringBuilder();
		if (null != pri) {
			s.append("{\n");
			s.append(STEP).append("name=").append(dumpId(pri.getName())).append(",\n");
			s.append(STEP).append("description=").append(quote(pri.getDescription())).append(",\n");
			s.append(STEP).append("argDefNames=").append(dumpNames(pri.getArgumentDefNames(), STEP)).append(",\n");
			s.append(STEP).append("argValues=").append(dumpAttrVals(pri.getArgumentValues(), STEP)).append(",\n");
			s.append(STEP).append("timingConstraints=").append(dumpTriggers(pri.getTimingConstraints(), STEP)).append(",\n");
			s.append(STEP).append("tasks=").append(dumpTaskInsts(pri.getTasks(), STEP)).append("\n");
			s.append("}");
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	private static String dumpEntKey(EntityKey ek) {
		StringBuilder s = new StringBuilder();
		s.append("{ first=").append(dumpId(ek.getFirstSubKey()));
		s.append(", second=").append(ek.getSecondSubKey());
		s.append(", third=").append(ek.getThirdSubKey());
		s.append(", fourth=").append(ek.getFourthSubKey());
		s.append(" }");
		return s.toString();
	}
	
	private static String dumpUpdHdr(UpdateHeader uh, String ind) {
		StringBuilder s = new StringBuilder("{\n");
		s.append(ind).append(STEP).append("key=").append(dumpEntKey(uh.getKey())).append(",\n");
		s.append(ind).append(STEP).append("sourceUri=").append(uh.getSourceURI()).append(",\n");
		s.append(ind).append(STEP).append("timeStamp=").append(uh.getTimestamp()).append(",\n");
		s.append(ind).append(STEP).append("updateType=").append(uh.getUpdateType()).append("\n");
		s.append(STEP).append("}");
		return s.toString();
	}
	
	public static String updHdrs(UpdateHeaderList uhl) {
		StringBuilder s = new StringBuilder();
		if (null != uhl) {
			openList(s, uhl);
			for (int i = 0; i < uhl.size(); ++i) {
				s.append(STEP).append(i).append(": ").append(dumpUpdHdr(uhl.get(i), STEP)).append("\n");
			}
			closeList(s, uhl, "");
		} else {
			s.append(NULL);
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
	
	public static String objType(ObjectType ot) {
		StringBuilder s = new StringBuilder();
		if (null != ot) {
			s.append("{ area=").append(ot.getArea());
			s.append(", service=").append(ot.getService());
			s.append(", version=").append(ot.getVersion());
			s.append(", number=").append(ot.getNumber());
			s.append(" }");
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	private static String dumpObjId(ObjectId oi, String ind) {
		StringBuilder s = new StringBuilder("{\n");
		s.append(ind).append(STEP).append("key=").append(dumpObjKey(oi.getKey(), ind+STEP)).append(",\n");
		s.append(ind).append(STEP).append("type=").append(objType(oi.getType())).append("\n");
		s.append(ind).append("}");
		return s.toString();
	}
	
	public static String objIds(ObjectIdList oil) {
		StringBuilder s = new StringBuilder();
		if (null != oil) {
			openList(s, oil);
			for (int i = 0; i < oil.size(); ++i) {
				s.append(STEP).append(i).append(": ").append(dumpObjId(oil.get(i), STEP)).append("\n");
			}
			closeList(s, oil, "");
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	private static String dumpStat(StatusRecord sr) {
		StringBuilder s = new StringBuilder();
		s.append("{ date=").append(sr.getDate());
		s.append(", state=").append(sr.getState());
		s.append(", comment=").append(quote(sr.getComment()));
		s.append(" }");
		return s.toString();
	}
	
	private static String dumpStats(StatusRecordList srl, String ind) {
		StringBuilder s = new StringBuilder();
		if (null != srl) {
			openList(s, srl);
			for (int i = 0; i < srl.size(); ++i) {
				s.append(ind).append(STEP).append(i).append(": ").append(dumpStat(srl.get(i))).append("\n");
			}
			closeList(s, srl, ind);
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	private static String dumpTaskStat(TaskStatusDetails ts, String ind) {
		StringBuilder s = new StringBuilder();
		if (null != ts) {
			s.append("{\n");
			s.append(ind).append(STEP).append("taskInstName=").append(dumpId(ts.getTaskInstName())).append(",\n");
			s.append(ind).append(STEP).append("status=").append(dumpStats(ts.getStatus(), ind+STEP)).append("\n");
			s.append(ind).append("}");
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	private static String dumpTaskStats(TaskStatusDetailsList tsl, String ind) {
		StringBuilder s = new StringBuilder();
		if (null != tsl) {
			openList(s, tsl);
			for (int i = 0; i < tsl.size(); ++i) {
				s.append(ind).append(STEP).append(i).append(": ").append(dumpTaskStat(tsl.get(i), ind+STEP)).append("\n");
			}
			closeList(s, tsl, ind);
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	private static String dumpPrStat(PlanningRequestStatusDetails prs, String ind) {
		StringBuilder s = new StringBuilder();
		if (null != prs) {
			s.append("{\n");
			s.append(ind).append(STEP).append("prInstName=").append(dumpId(prs.getPrInstName())).append(",\n");
			s.append(ind).append(STEP).append("status=").append(dumpStats(prs.getStatus(), ind+STEP)).append(",\n");
			s.append(ind).append(STEP).append("taskStatuses=").append(dumpTaskStats(prs.getTaskStatuses(), ind+STEP)).append("\n");
			s.append(ind).append("}");
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	public static String prStats(PlanningRequestStatusDetailsList prsl) {
		StringBuilder s = new StringBuilder();
		if (null != prsl) {
			openList(s, prsl);
			for (int i = 0; i < prsl.size(); ++i) {
				s.append(STEP).append(i).append(": ").append(dumpPrStat(prsl.get(i), STEP)).append("\n");
			}
			closeList(s, prsl, "");
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	public static String taskStats(TaskStatusDetailsList tsl) {
		return dumpTaskStats(tsl, "");
	}
	
	private static String dumpBaseDef(BaseDefinition bd, String ind) {
		StringBuilder s = new StringBuilder();
		if (null != bd) {
			if (bd instanceof TaskDefinitionDetails) {
				TaskDefinitionDetails td = (TaskDefinitionDetails)bd;
				s.append(dumpTaskDef(td, ind));
			} else if (bd instanceof PlanningRequestDefinitionDetails) {
				PlanningRequestDefinitionDetails prd = (PlanningRequestDefinitionDetails)bd;
				s.append(dumpPrDef(prd, ind));
			} else {
				s.append(bd);
			}
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	@SuppressWarnings("rawtypes")
	public static String baseDefs(BaseDefinitionList bdl) {
		StringBuilder s = new StringBuilder();
		if (null != bdl) {
			openList(s, bdl);
			for (int i = 0; i < bdl.size(); ++i) {
				s.append(STEP).append(i).append(": ").append(dumpBaseDef((BaseDefinition)bdl.get(i), STEP)).append("\n");
			}
			closeList(s, bdl, "");
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	private static String dumpPrResp(PlanningRequestResponseInstanceDetails prr, String ind) {
		StringBuilder s = new StringBuilder();
		if (null != prr) {
			s.append("{\n");
			s.append(ind).append(STEP).append("prInstName=").append(dumpId(prr.getPrInstName())).append(",\n");
			s.append(ind).append(STEP).append("date=").append(prr.getDate()).append(",\n");
			s.append(ind).append(STEP).append("argDefNames=").append(dumpNames(prr.getArgumentDefNames(), ind+STEP)).append(",\n");
			s.append(ind).append(STEP).append("argDefValues=").append(dumpAttrVals(prr.getArgumentValues(), ind+STEP)).append("\n");
			s.append(ind).append("}");
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	public static String prResps(PlanningRequestResponseInstanceDetailsList prrl) {
		StringBuilder s = new StringBuilder();
		if (null != prrl) {
			openList(s, prrl);
			for (int i = 0; i < prrl.size(); ++i) {
				s.append(STEP).append(i).append(": ").append(dumpPrResp(prrl.get(i), STEP)).append("\n");
			}
			closeList(s, prrl, "");
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
}
