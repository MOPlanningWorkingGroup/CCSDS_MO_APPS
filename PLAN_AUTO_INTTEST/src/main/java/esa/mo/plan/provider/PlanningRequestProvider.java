package esa.mo.plan.provider;

import java.util.HashMap;
import java.util.Map;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.planning.planningrequest.provider.PlanningRequestInheritanceSkeleton;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestInstanceDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.PlanningRequestStatusDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskDefinitionDetailsList;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskStatusDetails;
import org.ccsds.moims.mo.planning.planningrequest.structures.TaskStatusDetailsList;

public class PlanningRequestProvider extends PlanningRequestInheritanceSkeleton {

	public void submitPlanningRequest(Long defInstId, Long prInstId, PlanningRequestInstanceDetails prDetails,
			MALInteraction interaction) throws MALInteractionException,
			MALException {
		if (defInstId != null) {
			if (prDetails != null) {
				// TODO
			} else {
				throw new MALException("pr instance not given");
			}
		} else {
			throw new MALException("definition instance id not given");
		}
	}

	public void updatePlanningRequest(Long prInstId, PlanningRequestInstanceDetails prDetails,
			MALInteraction interaction) throws MALException, MALInteractionException {
		if (prInstId != null) {
			if (prDetails != null) {
				// TODO
			} else {
				throw new MALException("pr instance not given");
			}
		} else {
			throw new MALException("pr instance id not given");
		}
	}

	public void removePlanningRequest(Long prInstId, MALInteraction interaction)
			throws MALException, MALInteractionException {
		if (prInstId != null) {
			// TODO
		} else {
			throw new MALException("no pr instance id given");
		}
	}

	private PlanningRequestStatusDetails getPrStatus(Long id) {
		return null; // TODO
	}

	public PlanningRequestStatusDetailsList getPlanningRequestStatus(LongList prIds, MALInteraction interaction)
			throws MALInteractionException, MALException {
		PlanningRequestStatusDetailsList list = null;
		if (prIds != null) {
			list = new PlanningRequestStatusDetailsList(); 
			if (prIds.isEmpty()) {
				throw new MALException("pr instance id list is empty");
			}
			for (int i = 0; i < prIds.size(); ++i) {
				Long id = prIds.get(i);
				if (id == null) {
					throw new MALException("pr instance id[" + i + "] is null");
				}
				PlanningRequestStatusDetails stat = getPrStatus(id);
				list.add(stat);
			}
		} else {
			throw new MALException("no pr instance id list given");
		}
		return list;
	}

	private long lastPrId = 0L;
	private Map<Long, PlanningRequestDefinitionDetails> prDefs = new HashMap<Long, PlanningRequestDefinitionDetails>();

	private LongList getPrDefs(Identifier id) {
		LongList list = new LongList();
		list.addAll(prDefs.keySet()); // TODO
		return list;
	}

	public LongList listDefinition(IdentifierList prNames, MALInteraction interaction) throws MALInteractionException,
			MALException {
		LongList list = null;
		if (prNames != null) {
			if (prNames.isEmpty()) {
				throw new MALException("no ids in list");
			}
			list = new LongList();
			for (int i = 0; i < prNames.size(); ++i) {
				Identifier id = prNames.get(i);
				if (id == null) {
					throw new MALException("identifier[" + i + "] is null");
				}
				LongList idList = getPrDefs(id);
				list.addAll(idList);
			}
		} else {
			throw new MALException("no id list given");
		}
		return list;
	}

	private Long addPrDef(PlanningRequestDefinitionDetails prDef) {
		Long id = new Long(++lastPrId);
		prDefs.put(id, prDef);
		return id;
	}

	public LongList addDefinition(PlanningRequestDefinitionDetailsList prDefDetails, MALInteraction interaction)
			throws MALInteractionException, MALException {
		LongList list = null;
		if (prDefDetails != null) {
			list = new LongList();
			if (prDefDetails.isEmpty()) {
				throw new MALException("no pr definitions in list");
			}
			for (int i = 0; i < prDefDetails.size(); ++i) {
				PlanningRequestDefinitionDetails prDef = prDefDetails.get(i);
				if (prDef == null) {
					throw new MALException("pr definition[" + i + "] is null");
				}
				Long id = addPrDef(prDef);
				list.add(id);
			}
		} else {
			throw new MALException("no pr definition list given");
		}
		return list;
	}

	private void updatePrDef(Long id, PlanningRequestDefinitionDetails prDef) {
		prDefs.put(id, prDef);
	}

	public void updateDefinition(LongList prDefInstIds, PlanningRequestDefinitionDetailsList prDefDetails,
			MALInteraction interaction) throws MALInteractionException, MALException {
		if (prDefInstIds != null) {
			if (prDefDetails != null) {
				if (prDefInstIds.isEmpty()) {
					throw new MALException("no pr instance ids in list");
				}
				if (prDefInstIds.size() != prDefDetails.size()) {
					throw new MALException("pr instance id count does not match pr definition count");
				}
				for (int i = 0; i < prDefInstIds.size(); ++i) {
					Long id = prDefInstIds.get(i);
					if (id == null) {
						throw new MALException("pr instance id[" + i + "] is null");
					}
					PlanningRequestDefinitionDetails prDef = prDefDetails.get(i);
					if (prDef == null) {
						throw new MALException("pr definition[" + i + "] is null");
					}
					updatePrDef(id, prDef);
				}
			} else {
				throw new MALException("no pr definitions list given");
			}
		} else {
			throw new MALException("no pr instance list given");
		}
	}

	private void removePrDef(Long id) {
		prDefs.remove(id);
	}

	public void removeDefinition(LongList prDefInstIds, MALInteraction interaction) throws MALInteractionException,
			MALException {
		if (prDefInstIds != null) {
			if (prDefInstIds.isEmpty()) {
				throw new MALException("no instance ids in list");
			}
			for (int i = 0; i < prDefInstIds.size(); ++i) {
				Long id = prDefInstIds.get(i);
				if (id == null) {
					throw new MALException("instance id[" + i + "] is null");
				}
				removePrDef(id);
			}
		} else {
			throw new MALException("no instance id list given");
		}
	}

	private TaskStatusDetails getTaskStatus(Long id) {
//		TaskStatusDetails stat = new TaskStatusDetails();
//		return stat;
		return null; // no such task TODO
	}

	public TaskStatusDetailsList getTaskStatus(LongList taskIds, MALInteraction interaction)
			throws MALInteractionException, MALException {
		TaskStatusDetailsList list = null;
		if (taskIds != null) {
			list = new TaskStatusDetailsList();
			if (taskIds.isEmpty()) {
				throw new MALException("task id list is empty");
			}
			for (int i = 0; i < taskIds.size(); ++i) {
				Long id = taskIds.get(i);
				if (id == null) {
					throw new MALException("task id[" + i + "] is null");
				}
				TaskStatusDetails stat = getTaskStatus(id);
				list.add(stat);
			}
		} else {
			throw new MALException("no task id list given");
		}
		return list;
	}

	private void setTaskStatus(Long id, TaskStatusDetails stat) {
		// TODO
	}

	public void setTaskStatus(LongList taskIds, TaskStatusDetailsList taskStatus, MALInteraction interaction)
			throws MALInteractionException, MALException {
		if (taskIds != null) {
			if (taskStatus != null) {
				if (taskIds.isEmpty()) {
					throw new MALException("task id list is empty");
				}
				if (taskIds.size() != taskStatus.size()) {
					throw new MALException("task id count differs from task status count");
				}
				for (int i = 0; i < taskIds.size(); ++i) {
					Long id = taskIds.get(i);
					if (id == null) {
						throw new MALException("task id[" + i + "] is null");
					}
					TaskStatusDetails stat = taskStatus.get(i);
					if (stat == null) {
						throw new MALException("task status[" + i + "] is null");
					}
					setTaskStatus(id, stat);
				}
			} else {
				throw new MALException("no task status list givene");
			}
		} else {
			throw new MALException("no task id list given");
		}
	}

	private long lastTaskId = 0L;
	private Map<Long, TaskDefinitionDetails> taskDefs = new HashMap<Long, TaskDefinitionDetails>();

	private LongList getTaskDefs(Identifier id) {
		LongList list = new LongList();
		list.addAll(taskDefs.keySet()); // TODO
		return list;
	}

	public LongList listTaskDefinition(IdentifierList taskNames, MALInteraction interaction)
			throws MALInteractionException, MALException {
		LongList list = null;
		if (taskNames != null) {
			list = new LongList();
			if (taskNames.isEmpty()) {
				throw new MALException("no task names  in list");
			}
			int i = 0;
			for (Identifier id : taskNames) {
				if (id == null) {
					throw new MALException("identifier[" + i + "] is null");
				}
				LongList idList = getTaskDefs(id);
				list.addAll(idList);
				i++;
			}
		} else {
			throw new MALException("no task names list given");
		}
		return list;
	}

	private Long addTaskDef(TaskDefinitionDetails def) {
		Long id = new Long(++lastTaskId);
		taskDefs.put(id, def);
		return id;
	}

	public LongList addTaskDefinition(TaskDefinitionDetailsList taskDefDetails, MALInteraction interaction)
			throws MALInteractionException, MALException {
		LongList list = null;
		if (taskDefDetails != null) {
			list = new LongList();
			if (taskDefDetails.isEmpty()) {
				throw new MALException("no task definitions in list");
			}
			int i = 0;
			for (TaskDefinitionDetails def : taskDefDetails) {
				if (def == null) {
					throw new MALException("task definition["+i+"] is null");
				}
				Long id = addTaskDef(def);
				list.add(id);
				i++;
			}
		} else {
			throw new MALException("no task definition list given");
		}
		return list;
	}

	private void updateTaskDef(Long id, TaskDefinitionDetails def) {
		taskDefs.put(id, def);
	}

	public void updateTaskDefinition(LongList taskDefInstIds, TaskDefinitionDetailsList taskDefDetails,
			MALInteraction interaction) throws MALInteractionException, MALException {
		if (taskDefInstIds != null) {
			if (taskDefDetails != null) {
				if (taskDefInstIds.isEmpty()) {
					throw new MALException("no task instance ids in list");
				}
				if (taskDefInstIds.size() != taskDefDetails.size()) {
					throw new MALException("instance ids count does not match definitions count");
				}
				for (int i = 0; i < taskDefInstIds.size(); i++) {
					Long id = taskDefInstIds.get(i);
					if (id == null) {
						throw new MALException("task instance id[" + i + "] is null");
					}
					TaskDefinitionDetails def = taskDefDetails.get(i);
					if (def == null) {
						throw new MALException("task definition[" + i + "] is null");
					}
					updateTaskDef(id, def);
				}
			} else {
				throw new MALException("no task definition list given");
			}
		} else {
			throw new MALException("no task instance id list given");
		}
	}

	private void removeTaskDef(Long id) {
		taskDefs.remove(id);
	}

	public void removeTaskDefinition(LongList taskDefInstIds, MALInteraction interaction)
			throws MALInteractionException, MALException {
		if (taskDefInstIds != null) {
			if (taskDefInstIds.isEmpty()) {
				throw new MALException("no task instance ids in list");
			}
			for (int i = 0; i < taskDefInstIds.size(); ++i) {
				Long id = taskDefInstIds.get(i);
				if (id == null) {
					throw new MALException("task instance id[" + i + "] is null");
				}
				removeTaskDef(id);
			}
		} else {
			throw new MALException("no task instance id list given");
		}
	}

}
