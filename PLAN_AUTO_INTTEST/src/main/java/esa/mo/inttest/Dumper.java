package esa.mo.inttest;

import java.util.List;

import org.ccsds.moims.mo.automation.schedule.structures.PatchOperation;
import org.ccsds.moims.mo.automation.schedule.structures.PatchOperationList;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleDefinitionDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleDefinitionDetailsList;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleInstanceDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleItemInstanceDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleItemInstanceDetailsList;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleItemPatchOperations;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleItemPatchOperationsList;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleItemStatusDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleItemStatusDetailsList;
import org.ccsds.moims.mo.automation.schedule.structures.SchedulePatchOperations;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleStatusDetails;
import org.ccsds.moims.mo.automation.schedule.structures.ScheduleStatusDetailsList;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetails;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.com.structures.ObjectTypeList;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.EntityKey;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
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
import org.ccsds.moims.mo.planningdatatypes.structures.RelativeTime;
import org.ccsds.moims.mo.planningdatatypes.structures.StatusRecord;
import org.ccsds.moims.mo.planningdatatypes.structures.StatusRecordList;
import org.ccsds.moims.mo.planningdatatypes.structures.TimeTrigger;
import org.ccsds.moims.mo.planningdatatypes.structures.TriggerDetails;
import org.ccsds.moims.mo.planningdatatypes.structures.TriggerDetailsList;
import org.ccsds.moims.mo.planningdatatypes.structures.TriggerName;

/**
 * Pretty printing for planning request, schedule, etc data structures.
 */
public final class Dumper {

	private static final String STEP = "  ";
	private static final String NULL = "null";
	
	/**
	 * Hidden ctor.
	 */
	private Dumper() {
	}
	
	private static String dumpAttrType(Byte aType) {
		StringBuilder s = new StringBuilder(aType.toString());
		switch ((int)aType) {
			case Attribute._STRING_TYPE_SHORT_FORM:
				s.append("/String");
				break;
			case Attribute._TIME_TYPE_SHORT_FORM:
				s.append("/Time");
				break;
			case Attribute._OCTET_TYPE_SHORT_FORM:
				s.append("/Octet");
				break;
			case Attribute._UOCTET_TYPE_SHORT_FORM:
				s.append("/UOctet");
				break;
			case Attribute._IDENTIFIER_TYPE_SHORT_FORM:
				s.append("/Identifier");
				break;
			case Attribute._USHORT_TYPE_SHORT_FORM:
				s.append("/UShort");
				break;
			case Attribute._INTEGER_TYPE_SHORT_FORM:
				s.append("/Integer");
				break;
			case Attribute._LONG_TYPE_SHORT_FORM:
				s.append("/Long");
				break;
			default:
		}
		return s.toString();
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
			s.append(", desc=").append(quote(arg.getDescription()));
			s.append(", attrType=").append(dumpAttrType(arg.getAttributeType()));
			s.append(", representation=").append(arg.getRepresentation());
			s.append(", radix=").append(arg.getRadix());
			s.append(", unit=").append(arg.getUnit());
			s.append(", defaultValue=").append(dumpAttr(arg.getDefaultValue()));
			s.append(" }");
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	@SuppressWarnings("rawtypes")
	private static void openList(StringBuilder s, List list) {
		s.append("list (size=").append(list.size()).append(") [").append(list.isEmpty() ? "]" : "\n");
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
				s.append(ind).append(STEP).append(i).append(": ").append(dumpArg(args.get(i))).append(",\n");
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
			s.append(ind).append(STEP).append("desc=").append(quote(td.getDescription())).append(",\n");
			s.append(ind).append(STEP).append("argDefs=").append(dumpArgDefs(td.getArgumentDefs(), ind+STEP)).append("\n");
			s.append(ind).append("}");
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	/**
	 * Outputs Task Definitions list.
	 * @param tdl
	 * @return
	 */
	public static String taskDefs(TaskDefinitionDetailsList tdl) {
		StringBuilder s = new StringBuilder();
		if (null != tdl) {
			openList(s, tdl);
			for (int i = 0; i < tdl.size(); ++i) {
				s.append(STEP).append(i).append(": ").append(dumpTaskDef(tdl.get(i), STEP)).append(",\n");
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
				s.append(ind).append(STEP).append(i).append(": \"").append(il.get(i)).append("\",\n");
			}
			closeList(s, il, ind);
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	/**
	 * Outputs Idenfitiers list.
	 * @param il
	 * @return
	 */
	public static String names(IdentifierList il) {
		return dumpNames(il, STEP);
	}
	
	private static String dumpPrDef(PlanningRequestDefinitionDetails prd, String ind) {
		StringBuilder s = new StringBuilder();
		if (null != prd) {
			s.append("{\n");
			s.append(ind).append(STEP).append("name=").append(dumpId(prd.getName())).append(",\n");
			s.append(ind).append(STEP).append("desc=").append(quote(prd.getDescription())).append(",\n");
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
				s.append(STEP).append(i).append(": ").append(dumpPrDef(prdl.get(i), STEP)).append(",\n");
			}
			closeList(s, prdl, "");
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	private static String dumpAttr(Attribute a) {
		StringBuilder s = new StringBuilder();
		final boolean doQuote = (null != a) && ((a.getTypeShortForm() == Attribute.STRING_TYPE_SHORT_FORM)
				|| (a.getTypeShortForm() == Attribute.IDENTIFIER_TYPE_SHORT_FORM));
		if (doQuote) {
			s.append("\"");
		}
		s.append(a);
		if (doQuote) {
			s.append("\"");
		}
		return s.toString();
	}
	
	private static String dumpAttrVal(AttributeValue av) {
		StringBuilder s = new StringBuilder();
		if (null != av) {
			s.append("{ value=").append(dumpAttr(av.getValue()));
			s.append(", type=").append(dumpAttrType((byte)(0xff & av.getValue().getTypeShortForm())));
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
				s.append(ind).append(STEP).append(i).append(": ").append(dumpAttrVal(avl.get(i))).append(",\n");
			}
			closeList(s, avl, ind);
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	private static String dumpRelTime(RelativeTime rt) {
		StringBuilder s = new StringBuilder();
		if (null != rt) {
			s.append("{ relativeTime=").append(rt.getRelativeTime());
			s.append(", sign=").append(rt.getSignPositive());
			s.append(" }");
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	private static String dumpTimeTrig(TimeTrigger t) {
		StringBuilder s = new StringBuilder();
		if (null != t) {
			s.append("{ absoluteTime=").append(t.getAbsoluteTime());
			s.append(", relativeTime=").append(dumpRelTime(t.getRelativeTime()));
			s.append(" }");
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	private static String dumpTrigName(TriggerName tn) {
		return (null != tn) ? quote(tn.toString()) : NULL;
	}
	
	private static String dumpTrigger(TriggerDetails t, String ind) {
		StringBuilder s = new StringBuilder();
		if (null != t) {
			s.append("{\n");
			s.append(ind).append(STEP).append("triggerName=").append(dumpTrigName(t.getTriggerName())).append(",\n");
			s.append(ind).append(STEP).append("timeTrigger=").append(dumpTimeTrig(t.getTimeTrigger())).append(",\n");
			s.append(ind).append(STEP).append("eventTrigger=").append(t.getEventTrigger()).append(",\n");
			s.append(ind).append(STEP).append("repeat=").append(t.getRepeat()).append(",\n");
			s.append(ind).append(STEP).append("separation=").append(t.getSeparation()).append("\n");
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
				s.append(ind).append(STEP).append(i).append(": ").append(dumpTrigger(tl.get(i), ind+STEP)).append(",\n");
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
			s.append(ind).append(STEP).append("comment=").append(quote(ti.getComment())).append(",\n");
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
				s.append(ind).append(STEP).append(i).append(": ").append(dumpTaskInst(til.get(i), ind+STEP)).append(",\n");
			}
			closeList(s, til, ind);
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	private static String dumpPrInst(PlanningRequestInstanceDetails pri, String ind) {
		StringBuilder s = new StringBuilder();
		if (null != pri) {
			s.append("{\n");
			s.append(ind).append(STEP).append("name=").append(dumpId(pri.getName())).append(",\n");
			s.append(ind).append(STEP).append("comment=").append(quote(pri.getComment())).append(",\n");
			s.append(ind).append(STEP).append("argDefNames=").append(dumpNames(pri.getArgumentDefNames(), ind+STEP)).append(",\n");
			s.append(ind).append(STEP).append("argValues=").append(dumpAttrVals(pri.getArgumentValues(), ind+STEP)).append(",\n");
			s.append(ind).append(STEP).append("timingConstraints=").append(dumpTriggers(pri.getTimingConstraints(), ind+STEP)).append(",\n");
			s.append(ind).append(STEP).append("tasks=").append(dumpTaskInsts(pri.getTasks(), ind+STEP)).append("\n");
			s.append(ind).append("}");
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	/**
	 * Outputs PR Instance.
	 * @param pri
	 * @return
	 */
	public static String prInst(PlanningRequestInstanceDetails pri) {
		return dumpPrInst(pri, STEP);
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
		s.append(ind).append("}");
		return s.toString();
	}
	
	private static String dumpUpdHdrs(UpdateHeaderList uhl, String ind) {
		StringBuilder s = new StringBuilder();
		if (null != uhl) {
			openList(s, uhl);
			for (int i = 0; i < uhl.size(); ++i) {
				s.append(ind).append(STEP).append(i).append(": ").append(dumpUpdHdr(uhl.get(i), ind+STEP)).append(",\n");
			}
			closeList(s, uhl, ind);
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	/**
	 * Outputs UpdateHeaders list.
	 * @param uhl
	 * @return
	 */
	public static String updHdrs(UpdateHeaderList uhl) {
		return dumpUpdHdrs(uhl, STEP);
	}
	
	private static String dumpObjKey(ObjectKey ok, String ind) {
		StringBuilder s = new StringBuilder("{\n");
		s.append(ind).append(STEP).append("instId=").append(ok.getInstId()).append(",\n");
		s.append(ind).append(STEP).append("domain=").append(dumpNames(ok.getDomain(), ind+STEP)).append("\n");
		s.append(ind).append("}");
		return s.toString();
	}
	
	/**
	 * Outputs Object Types list.
	 * @param ot
	 * @return
	 */
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
	
	private static String dumpObjIds(ObjectIdList oil, String ind) {
		StringBuilder s = new StringBuilder();
		if (null != oil) {
			openList(s, oil);
			for (int i = 0; i < oil.size(); ++i) {
				s.append(ind).append(STEP).append(i).append(": ").append(dumpObjId(oil.get(i), ind+STEP)).append(",\n");
			}
			closeList(s, oil, ind);
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	/**
	 * Outputs Object Ids list.
	 * @param oil
	 * @return
	 */
	public static String objIds(ObjectIdList oil) {
		return dumpObjIds(oil, STEP);
	}
	
	private static String dumpStat(StatusRecord sr) {
		StringBuilder s = new StringBuilder();
		s.append("{ timeStamp=").append(sr.getTimeStamp());
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
				s.append(ind).append(STEP).append(i).append(": ").append(dumpStat(srl.get(i))).append(",\n");
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
				s.append(ind).append(STEP).append(i).append(": ").append(dumpTaskStat(tsl.get(i), ind+STEP)).append(",\n");
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
	
	private static String dumpPrStats(PlanningRequestStatusDetailsList prsl, String ind) {
		StringBuilder s = new StringBuilder();
		if (null != prsl) {
			openList(s, prsl);
			for (int i = 0; i < prsl.size(); ++i) {
				s.append(ind).append(STEP).append(i).append(": ").append(dumpPrStat(prsl.get(i), ind+STEP)).append(",\n");
			}
			closeList(s, prsl, ind);
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	/**
	 * Outputs PR Statuses list.
	 * @param prsl
	 * @return
	 */
	public static String prStats(PlanningRequestStatusDetailsList prsl) {
		return dumpPrStats(prsl, STEP);
	}
	
	/**
	 * Outputs Task Statuses list.
	 * @param tsl
	 * @return
	 */
	public static String taskStats(TaskStatusDetailsList tsl) {
		return dumpTaskStats(tsl, STEP);
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
	private static String dumpBaseDefs(BaseDefinitionList bdl, String ind) {
		StringBuilder s = new StringBuilder();
		if (null != bdl) {
			openList(s, bdl);
			for (int i = 0; i < bdl.size(); ++i) {
				s.append(ind).append(STEP).append(i).append(": ").append(dumpBaseDef((BaseDefinition)bdl.get(i), ind+STEP)).append(",\n");
			}
			closeList(s, bdl, ind);
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	/**
	 * Outputs PR BaseDefinitions list.
	 * @param bdl
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String baseDefs(BaseDefinitionList bdl) {
		return dumpBaseDefs(bdl, STEP);
	}
	
	private static String dumpPrResp(PlanningRequestResponseInstanceDetails prr, String ind) {
		StringBuilder s = new StringBuilder();
		if (null != prr) {
			s.append("{\n");
			s.append(ind).append(STEP).append("prInstName=").append(dumpId(prr.getPrInstName())).append(",\n");
			s.append(ind).append(STEP).append("timeStamp=").append(quote(prr.getTimestamp())).append(",\n");
			s.append(ind).append(STEP).append("argDefNames=").append(dumpNames(prr.getArgumentDefNames(), ind+STEP)).append(",\n");
			s.append(ind).append(STEP).append("argDefValues=").append(dumpAttrVals(prr.getArgumentValues(), ind+STEP)).append("\n");
			s.append(ind).append("}");
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	/**
	 * Outputs PR Response list.
	 * @param prrl
	 * @return
	 */
	public static String prResps(PlanningRequestResponseInstanceDetailsList prrl) {
		StringBuilder s = new StringBuilder();
		if (null != prrl) {
			openList(s, prrl);
			for (int i = 0; i < prrl.size(); ++i) {
				s.append(STEP).append(i).append(": ").append(dumpPrResp(prrl.get(i), STEP)).append(",\n");
			}
			closeList(s, prrl, "");
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	/**
	 * Outputs message transmission source.
	 * @param mmg
	 * @return
	 */
	public static String msgFrom(MALMessageHeader mmg) {
		String f = mmg.getURIFrom().getValue();
		int i = f.indexOf('-');
		return f.substring(i+1);
	}
	
	/**
	 * Outputs message transmission target.
	 * @param mmh
	 * @return
	 */
	public static String msgTo(MALMessageHeader mmh) {
		String t = mmh.getURITo().getValue();
		int i = t.indexOf('-'); // works for RMI
		return t.substring(i+1);
	}
	
	/**
	 * Outputs message transmission "from -> to".
	 * @param mi
	 * @return
	 */
	public static String received(MALInteraction mi) {
		return msgFrom(mi.getMessageHeader()) + " -> " + msgTo(mi.getMessageHeader());
	}
	
	/**
	 * Outputs message transmission "to <- from".
	 * @param mi
	 * @return
	 */
	public static String sending(MALInteraction mi) {
		return msgFrom(mi.getMessageHeader()) + " <- " + msgTo(mi.getMessageHeader());
	}
	
	/**
	 * Outputs message transmission "broker -> receiver".
	 * @param mmh
	 * @return
	 */
	public static String fromBroker(String broker, MALMessageHeader mmh) {
		return broker + " -> " + msgTo(mmh);
	}
	
	private static String dumpEventTypes(ObjectTypeList otl, String ind) {
		StringBuilder s = new StringBuilder();
		if (null != otl) {
			openList(s, otl);
			for (int i = 0; i < otl.size(); ++i) {
				s.append(ind).append(STEP).append(i).append(": ").append(objType(otl.get(i))).append(",\n");
			}
			closeList(s, otl, ind);
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	private static String dumpSchDef(ScheduleDefinitionDetails sd, String ind) {
		StringBuilder s = new StringBuilder();
		if (null != sd) {
			s.append("{\n");
			s.append(ind).append(STEP).append("name=").append(dumpId(sd.getName())).append(",\n");
			s.append(ind).append(STEP).append("desc=").append(quote(sd.getDescription())).append(",\n");
			s.append(ind).append(STEP).append("eventTypes=").append(dumpEventTypes(sd.getEventTypes(), ind+STEP)).append(",\n");
			s.append(ind).append(STEP).append("argDefs=").append(dumpArgDefs(sd.getArgumentDefs(), ind+STEP)).append("\n");
			s.append(ind).append("}");
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	private static String dumpSchDefs(ScheduleDefinitionDetailsList sdl, String ind) {
		StringBuilder s = new StringBuilder();
		if (null != sdl) {
			openList(s, sdl);
			for (int i = 0; i < sdl.size(); ++i) {
				s.append(ind).append(STEP).append(i).append(": ").append(dumpSchDef(sdl.get(i), ind+STEP)).append(",\n");
			}
			closeList(s, sdl, ind);
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	/**
	 * Outputs Schedule Definition.
	 * @param sdl
	 * @return
	 */
	public static String schDefs(ScheduleDefinitionDetailsList sdl) {
		return dumpSchDefs(sdl, STEP);
	}
	
	private static String dumpSchItem(ScheduleItemInstanceDetails sii, String ind) {
		StringBuilder s = new StringBuilder();
		s.append("{\n");
		s.append(ind).append(STEP).append("scheduleItemName=").append(dumpId(sii.getScheduleItemInstName())).append(",\n");
		s.append(ind).append(STEP).append("scheduleInstName=").append(dumpId(sii.getScheduleInstName())).append(",\n");
		s.append(ind).append(STEP).append("delegateItem=").append(dumpObjId(sii.getDelegateItem(), ind+STEP)).append(",\n");
		s.append(ind).append(STEP).append("argTypes=").append(dumpArgDefs(sii.getArgumentTypes(), ind+STEP)).append(",\n");
		s.append(ind).append(STEP).append("argValues=").append(dumpAttrVals(sii.getArgumentValues(), ind+STEP)).append(",\n");
		s.append(ind).append(STEP).append("timingConstraints=").append(dumpTriggers(sii.getTimingConstraints(), ind+STEP)).append("\n");
		s.append(ind).append("}");
		return s.toString();
	}
	
	private static String dumpSchItems(ScheduleItemInstanceDetailsList siil, String ind) {
		StringBuilder s = new StringBuilder();
		if (null != siil) {
			openList(s, siil);
			for (int i = 0; i < siil.size(); ++i) {
				s.append(ind).append(STEP).append(i).append(": ").append(dumpSchItem(siil.get(i), ind+STEP)).append(",\n");
			}
			closeList(s, siil, ind);
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	private static String dumpSchInst(ScheduleInstanceDetails si, String ind) {
		StringBuilder s = new StringBuilder();
		if (null != si) {
			s.append("{\n");
			s.append(ind).append(STEP).append("name=").append(dumpId(si.getName())).append(",\n");
			s.append(ind).append(STEP).append("comment=").append(quote(si.getComment())).append(",\n");
			s.append(ind).append(STEP).append("argDefNames=").append(dumpNames(si.getArgumentDefNames(), ind+STEP)).append(",\n");
			s.append(ind).append(STEP).append("argValues=").append(dumpAttrVals(si.getArgumentValues(), ind+STEP)).append(",\n");
			s.append(ind).append(STEP).append("scheduleItems=").append(dumpSchItems(si.getScheduleItems(), ind+STEP)).append(",\n");
			s.append(ind).append(STEP).append("timingConstraints=").append(dumpTriggers(si.getTimingConstraints(), ind+STEP)).append("\n");
			s.append(ind).append("}");
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	/**
	 * Outputs Schedule Instance.
	 * @param si
	 * @return
	 */
	public static String schInst(ScheduleInstanceDetails si) {
		return dumpSchInst(si, STEP);
	}
	
	private static String dumpSchItemStat(ScheduleItemStatusDetails sis, String ind) {
		StringBuilder s = new StringBuilder();
		if (null != sis) {
			s.append("{\n");
			s.append(ind).append(STEP).append("scheduleItemName=").append(dumpId(sis.getScheduleItemInstName())).append(",\n");
			s.append(ind).append(STEP).append("status=").append(dumpStats(sis.getStatus(), ind+STEP)).append("\n");
			s.append(ind).append("}");
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	private static String dumpSchItemStats(ScheduleItemStatusDetailsList sisl, String ind) {
		StringBuilder s = new StringBuilder();
		if (null != sisl) {
			openList(s, sisl);
			for (int i = 0; i < sisl.size(); ++i) {
				s.append(ind).append(STEP).append(i).append(": ").append(dumpSchItemStat(sisl.get(i), ind+STEP)).append(",\n");
			}
			closeList(s, sisl, ind);
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	private static String dumpSchStat(ScheduleStatusDetails ss, String ind) {
		StringBuilder s = new StringBuilder();
		if (null != ss) {
			s.append("{\n");
			s.append(ind).append(STEP).append("scheduleInstName=").append(dumpId(ss.getScheduleInstName())).append(",\n");
			s.append(ind).append(STEP).append("status=").append(dumpStats(ss.getStatus(), ind+STEP)).append(",\n");
			s.append(ind).append(STEP).append("scheduleItemStatuses=").append(dumpSchItemStats(ss.getScheduleItemStatuses(), ind+STEP)).append("\n");
			s.append(ind).append("}");
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	private static String dumpSchStats(ScheduleStatusDetailsList ssl, String ind) {
		StringBuilder s = new StringBuilder();
		if (null != ssl) {
			openList(s, ssl);
			for (int i = 0; i < ssl.size(); ++i) {
				s.append(ind).append(STEP).append(i).append(": ").append(dumpSchStat(ssl.get(i), ind+STEP)).append(",\n");
			}
			closeList(s, ssl, ind);
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	/**
	 * Outputs ScheduleStatuses list.
	 * @param ssl
	 * @return
	 */
	public static String schStats(ScheduleStatusDetailsList ssl) {
		return dumpSchStats(ssl, STEP);
	}
	
	private static String dumpPatchOp(PatchOperation po) {
		StringBuilder s = new StringBuilder();
		if (null != po) {
			s.append("{ enum=").append(po);
			s.append(", ordinal=").append(po.getOrdinal());
			s.append(", numbericValue=").append(po.getNumericValue());
			s.append(" }");
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	private static String dumpPatchOps(PatchOperationList pol, String ind) {
		StringBuilder s = new StringBuilder();
		if (null != pol) {
			openList(s, pol);
			for (int i = 0; i < pol.size(); ++i) {
				s.append(ind).append(STEP).append(i).append(": ").append(dumpPatchOp(pol.get(i))).append(",\n");
			}
			closeList(s, pol, ind);
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	private static String dumpSchItemPatchOp(ScheduleItemPatchOperations sipo, String ind) {
		StringBuilder s = new StringBuilder();
		if (null != sipo) {
			s.append("{\n");
			s.append(ind).append(STEP).append("scheduleItemName=").append(dumpId(sipo.getScheduleItemInstName())).append(",\n");
			s.append(ind).append(STEP).append("delegateItem=").append(dumpPatchOp(sipo.getDelegateItem())).append(",\n");
			s.append(ind).append(STEP).append("argTypes=").append(dumpPatchOps(sipo.getArgumentTypes(), ind+STEP)).append(",\n");
			s.append(ind).append(STEP).append("argValues=").append(dumpPatchOps(sipo.getArgumentValues(), ind+STEP)).append(",\n");
			s.append(ind).append(STEP).append("timingConstraints=").append(dumpPatchOps(sipo.getTimingConstraints(), ind+STEP)).append("\n");
			s.append(ind).append("}");
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	private static String dumpSchItemPatchOps(ScheduleItemPatchOperationsList sipol, String ind) {
		StringBuilder s = new StringBuilder();
		if (null != sipol) {
			openList(s, sipol);
			for (int i = 0; i < sipol.size(); ++i) {
				s.append(ind).append(STEP).append(i).append(": ").append(dumpSchItemPatchOp(sipol.get(i), ind+STEP)).append(",\n");
			}
			closeList(s, sipol, ind);
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	private static String dumpSchPatchOp(SchedulePatchOperations spo, String ind) {
		StringBuilder s = new StringBuilder();
		if (null != spo) {
			s.append("{\n");
			s.append(ind).append(STEP).append("scheduleInstName=").append(dumpId(spo.getScheduleInstName())).append(",\n");
			s.append(ind).append(STEP).append("argValues=").append(dumpPatchOps(spo.getArgumentValues(), ind+STEP)).append(",\n");
			s.append(ind).append(STEP).append("timingConstraints=").append(dumpPatchOps(spo.getTimingConstraints(), ind+STEP)).append(",\n");
			s.append(ind).append(STEP).append("scheduleItems=").append(dumpSchItemPatchOps(spo.getScheduleItems(), ind+STEP)).append("\n");
			s.append(ind).append("}");
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	/**
	 * Outputs Schedule Patch Operations.
	 * @param spo
	 * @return
	 */
	public static String schPatchOp(SchedulePatchOperations spo) {
		return dumpSchPatchOp(spo, STEP);
	}
	
	private static String dumpArcDet(ArchiveDetails ad, String ind) {
		StringBuilder s = new StringBuilder();
		if (null != ad) {
			s.append("{\n");
			s.append(ind).append(STEP).append("instId=").append(ad.getInstId()).append(",\n");
			s.append(ind).append(STEP).append("objDetails=").append(ad.getDetails()).append(",\n");
			s.append(ind).append(STEP).append("network=").append(ad.getNetwork()).append(",\n");
			s.append(ind).append(STEP).append("timeStamp=").append(ad.getTimestamp()).append(",\n");
			s.append(ind).append(STEP).append("provider=").append(ad.getProvider()).append(",\n");
			s.append(ind).append("}");
		}
		return s.toString();
	}
	
	private static String dumpArcDets(ArchiveDetailsList adl, String ind) {
		StringBuilder s = new StringBuilder();
		if (null != adl) {
			openList(s, adl);
			for (int i = 0; i < adl.size(); ++i) {
				s.append(ind).append(STEP).append(i).append(": ").append(dumpArcDet(adl.get(i), ind+STEP)).append(",\n");
			}
			closeList(s, adl, ind);
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	/**
	 * Outputs Archive Details list.
	 * @param adl
	 * @return
	 */
	public static String arcDets(ArchiveDetailsList adl) {
		return dumpArcDets(adl, STEP);
	}
	
	private static String dumpEl(Element e, String ind) {
		String s;
		if (e instanceof TaskInstanceDetails) {
			s = dumpTaskInst((TaskInstanceDetails)e, ind);
		} else if (e instanceof PlanningRequestInstanceDetails) {
			s = dumpPrInst((PlanningRequestInstanceDetails)e, ind);
		} else if (e instanceof ScheduleInstanceDetails) {
			s = dumpSchInst((ScheduleInstanceDetails)e, ind);
		} else {
			s = (null != e) ? e.toString() : NULL;
		}
		return s;
	}
	
	@SuppressWarnings("rawtypes")
	private static String dumpEls(ElementList el, String ind) {
		StringBuilder s = new StringBuilder();
		if (null != el) {
			openList(s, el);
			for (int i = 0; i < el.size(); ++i) {
				s.append(ind).append(STEP).append(i).append(": ").append(dumpEl((Element)el.get(i), ind+STEP)).append(",\n");
			}
			closeList(s, el, ind);
		} else {
			s.append(NULL);
		}
		return s.toString();
	}
	
	/**
	 * Outputs ElementList - List of TaskInstances, PlanningRequestInstances or ScheduleInstances.
	 * @param el
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String els(ElementList el) {
		return dumpEls(el, STEP);
	}
}
