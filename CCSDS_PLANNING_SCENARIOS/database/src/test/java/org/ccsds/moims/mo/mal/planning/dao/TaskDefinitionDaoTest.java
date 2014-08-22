package org.ccsds.moims.mo.mal.planning.dao;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.ccsds.moims.mo.mal.planning.dao.impl.TaskDefinitionDaoImpl;
import org.ccsds.moims.mo.mal.planning.datamodel.TaskArgumentDefinition;
import org.ccsds.moims.mo.mal.planning.datamodel.TaskDefinition;
import org.ccsds.moims.mo.mal.planning.datamodel.ValueType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:**/jpaPlanningContext.xml")
public class TaskDefinitionDaoTest {
	
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(TaskDefinitionDaoTest.class);
	
	@Autowired
	private TaskDefinitionDaoImpl taskDefinitionDaoImpl;
	
	@Test
	public void testTaskDefinitionDao() {
		TaskDefinition taskDefinition = new TaskDefinition();
		taskDefinition.setName("def1");
		taskDefinition.setDescription("def description");
		taskDefinition.setTaskArgumentDefinitions(new ArrayList<TaskArgumentDefinition>());
		TaskArgumentDefinition arg1 = new TaskArgumentDefinition();
		arg1.setTaskDefinition(taskDefinition);
		arg1.setName("arg1");
		arg1.setValueType(ValueType.LONG);
		taskDefinition.getTaskArgumentDefinitions().add(arg1);
		TaskArgumentDefinition arg2 = new TaskArgumentDefinition();
		arg2.setTaskDefinition(taskDefinition);
		arg2.setName("arg2");
		arg2.setValueType(ValueType.LONG);
		taskDefinition.getTaskArgumentDefinitions().add(arg2);
		taskDefinitionDaoImpl.insertUpdate(taskDefinition);
		List<TaskDefinition> list = taskDefinitionDaoImpl.getList();
		assertTrue(list.size() > 0);
	}

}
