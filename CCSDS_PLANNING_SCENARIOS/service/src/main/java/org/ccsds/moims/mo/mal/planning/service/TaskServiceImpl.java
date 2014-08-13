package org.ccsds.moims.mo.mal.planning.service;

import java.util.ArrayList;
import java.util.List;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.planning.dao.impl.TaskDefinitionDaoImpl;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.planning.task.provider.TaskInheritanceSkeleton;
import org.ccsds.moims.mo.planning.task.structures.TaskArgumentDefinition;
import org.ccsds.moims.mo.planning.task.structures.TaskDefinition;
import org.ccsds.moims.mo.planning.task.structures.TaskDefinitionList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Task Definition provider implementation.
 * @author krikse
 *
 */
@Controller
public class TaskServiceImpl extends TaskInheritanceSkeleton {
	
	@Autowired
	private TaskDefinitionDaoImpl taskDefinitionDaoImpl;

	public LongList listDefinition(IdentifierList _IdentifierList0,
			MALInteraction interaction) throws MALInteractionException,
			MALException {
		LongList list = new LongList();
		List<org.ccsds.moims.mo.mal.planning.datamodel.TaskDefinition> defList = taskDefinitionDaoImpl.getList();
		if (defList != null) {
			for (org.ccsds.moims.mo.mal.planning.datamodel.TaskDefinition taskDef : defList) {
				list.add(taskDef.getId());
			}
		}
		return list;
	}

	public LongList addDefinition(TaskDefinitionList _TaskDefinitionList0,
			MALInteraction interaction) throws MALInteractionException,
			MALException {
		LongList list = new LongList();
		if (_TaskDefinitionList0 != null) {
			for (TaskDefinition def : _TaskDefinitionList0) {
				org.ccsds.moims.mo.mal.planning.datamodel.TaskDefinition taskDef =
						new org.ccsds.moims.mo.mal.planning.datamodel.TaskDefinition();
				taskDef.setName(def.getName());
				taskDef.setDescription(def.getDescription());
				if (def.getArguments() != null) {
					taskDef.setTaskArgumentDefinitions(new ArrayList<org.ccsds.moims.mo.mal.planning.datamodel.TaskArgumentDefinition>());
					for (TaskArgumentDefinition argDef : def.getArguments()) {
						org.ccsds.moims.mo.mal.planning.datamodel.TaskArgumentDefinition dbArg =
								new org.ccsds.moims.mo.mal.planning.datamodel.TaskArgumentDefinition();
						dbArg.setName(argDef.getName());
						// TODO dbArg.setValueType(org.ccsds.moims.mo.mal.planning.datamodel.ValueType.valueOf(argDef.getValueType().toString()));
						dbArg.setTaskDefinition(taskDef);
						taskDef.getTaskArgumentDefinitions().add(dbArg);
					}
				}
				taskDefinitionDaoImpl.insertUpdate(taskDef);
				list.add(taskDef.getId());
			}
		}
		return list;
	}

	public void updateDefinition(LongList _LongList0,
			TaskDefinitionList _TaskDefinitionList1, MALInteraction interaction)
			throws MALInteractionException, MALException {
		if (_LongList0 != null &&_TaskDefinitionList1 != null) {
			if (_LongList0.size() != _TaskDefinitionList1.size()) {
				MALStandardError error = new MALStandardError(null, "ID list size and task definition list size is different!");
				throw new MALInteractionException(error);
			}
			for (int i = 0; i < _LongList0.size(); i++) {
				Long id = _LongList0.get(i);
				org.ccsds.moims.mo.mal.planning.datamodel.TaskDefinition taskDef = taskDefinitionDaoImpl.get(id);
				if (taskDef == null) {
					MALStandardError error = new MALStandardError(null, "One of the supplied Task Definition object instance identifiers is unknown!");
					throw new MALInteractionException(error);
				}
				TaskDefinition def =_TaskDefinitionList1.get(i);
				taskDef.setName(def.getName());
				taskDef.setDescription(def.getDescription());
				if (def.getArguments() != null) {
					for (TaskArgumentDefinition argDef : def.getArguments()) {
						// TODO
						org.ccsds.moims.mo.mal.planning.datamodel.TaskArgumentDefinition dbArg =
								new org.ccsds.moims.mo.mal.planning.datamodel.TaskArgumentDefinition();
						dbArg.setName(argDef.getName());
						// TODO dbArg.setValueType(org.ccsds.moims.mo.mal.planning.datamodel.ValueType.valueOf(argDef.getValueType().toString()));
						dbArg.setTaskDefinition(taskDef);
						taskDef.getTaskArgumentDefinitions().add(dbArg);
					}
				}
				taskDefinitionDaoImpl.insertUpdate(taskDef);
			}
		}
		
	}

	public void removeDefinition(LongList _LongList0, MALInteraction interaction)
			throws MALInteractionException, MALException {
		if (_LongList0 != null) {
			for (int i = 0; i < _LongList0.size(); i++) {
				Long id = _LongList0.get(i);
				org.ccsds.moims.mo.mal.planning.datamodel.TaskDefinition taskDef = taskDefinitionDaoImpl.get(id);
				if (taskDef == null) {
					MALStandardError error = new MALStandardError(null, "One of the supplied Task Definition object instance identifiers is unknown!");
					throw new MALInteractionException(error);
				}
				taskDefinitionDaoImpl.remove(id);
			}
		}
	}
	
	

}
