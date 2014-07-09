package org.ccsds.moims.mo.mal.planning.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.planning.task.provider.TaskInheritanceSkeleton;
import org.ccsds.moims.mo.planning.task.structures.TaskDefinition;
import org.ccsds.moims.mo.planning.task.structures.TaskDefinitionList;

/**
 * Task Definition provider implementation.
 * @author krikse
 *
 */
public class TaskServiceImpl extends TaskInheritanceSkeleton {
	
	private Map<Long, TaskDefinition> taskDefinitionMap = new HashMap<Long, TaskDefinition>();
	private Map<String, Long> identifierMap = new HashMap<String, Long>();
	private Long autoincrement = 0L;

	public LongList listDefinition(IdentifierList _IdentifierList0,
			MALInteraction interaction) throws MALInteractionException,
			MALException {
		LongList list = new LongList();
		if (taskDefinitionMap != null && taskDefinitionMap.size() > 0) {
			if (_IdentifierList0 != null) {
				Iterator<Identifier> it = _IdentifierList0.iterator();
				while (it.hasNext()) {
					Identifier identifier = it.next();
					Long id = identifierMap.get(identifier.getValue());
					if (id != null) {
						list.add(id);
					}
				}
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
				autoincrement++;
				list.add(autoincrement);
				taskDefinitionMap.put(autoincrement, def);
				identifierMap.put(def.getName(), autoincrement);
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
				if (!taskDefinitionMap.containsKey(id)) {
					MALStandardError error = new MALStandardError(null, "One of the supplied Task Definition object instance identifiers is unknown!");
					throw new MALInteractionException(error);
				}
				TaskDefinition def = _TaskDefinitionList1.get(i);
				taskDefinitionMap.put(id, def);
				identifierMap.put(def.getName(), id);
			}
		}
		
	}

	public void removeDefinition(LongList _LongList0, MALInteraction interaction)
			throws MALInteractionException, MALException {
		if (_LongList0 != null) {
			for (int i = 0; i < _LongList0.size(); i++) {
				Long id = _LongList0.get(i);
				if (!taskDefinitionMap.containsKey(id)) {
					MALStandardError error = new MALStandardError(null, "One of the supplied Task Definition object instance identifiers is unknown!");
					throw new MALInteractionException(error);
				}
				taskDefinitionMap.remove(id);
				identifierMap.remove(id);
			}
		}
	}
	
	

}
