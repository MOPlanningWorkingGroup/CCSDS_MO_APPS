package org.ccsds.moims.mo.mal.planning.test;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.logging.Logger;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.planning.consumer.TaskServiceConsumer;
import org.ccsds.moims.mo.mal.planning.dao.impl.TaskDefinitionDaoImpl;
import org.ccsds.moims.mo.mal.planning.provider.TaskServiceProvider;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.planning.task.structures.TaskArgumentDefinitionList;
import org.ccsds.moims.mo.planning.task.structures.TaskDefinition;
import org.ccsds.moims.mo.planning.task.structures.TaskDefinitionList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:**/applicationPlanningContext.xml")
public class TaskServiceTest {
	
	public static final Logger LOGGER = Logger
			.getLogger(TaskServiceTest.class.getName());
	
	@Autowired
	private TaskDefinitionDaoImpl taskDefinitionDaoImpl;
	
	@Autowired
	private TaskServiceProvider taskServiceProvider;
	
	@Autowired
	private TaskServiceConsumer taskServiceConsumer;
	
	@Test
	public void testListDefinition() throws MALInteractionException, MALException {
		TaskDefinitionList defList = new TaskDefinitionList();
		TaskDefinition def = new TaskDefinition();
		def.setName("test");
		def.setDescription("description");
		TaskArgumentDefinitionList arguments = new TaskArgumentDefinitionList();
		def.setArguments(arguments);
		defList.add(def);
		taskServiceConsumer.getTaskService().addDefinition(defList);
		IdentifierList identifierList = new IdentifierList();
		identifierList.add(new Identifier("test"));
		identifierList.add(new Identifier("test2"));
		LongList list = taskServiceConsumer.getTaskService().listDefinition(identifierList);
		assertTrue(list.size() > 0);
	}
	
	@Test
	public void testAddDefinition() throws MALInteractionException, MALException {
		TaskDefinitionList defList = new TaskDefinitionList();
		TaskDefinition def = new TaskDefinition();
		def.setName("test2");
		def.setDescription("description2");
		defList.add(def);
		taskServiceConsumer.getTaskService().addDefinition(defList);
		TaskArgumentDefinitionList arguments = new TaskArgumentDefinitionList();
		def.setArguments(arguments);
		defList.add(def);
		LongList longList = taskServiceConsumer.getTaskService().addDefinition(defList);
		IdentifierList identifierList = new IdentifierList();
		identifierList.add(new Identifier("test"));
		identifierList.add(new Identifier("test2"));
		LongList list = taskServiceConsumer.getTaskService().listDefinition(identifierList);
		assertTrue(longList.size() == 2);
	}
	
	@Test
	public void testUpdateDefinition() throws MALInteractionException, MALException {
		TaskDefinitionList defList = new TaskDefinitionList();
		TaskDefinition def = new TaskDefinition();
		def.setName("test");
		def.setDescription("description");
		TaskArgumentDefinitionList arguments = new TaskArgumentDefinitionList();
		def.setArguments(arguments);
		defList.add(def);
		LongList longList = taskServiceConsumer.getTaskService().addDefinition(defList);
		defList = new TaskDefinitionList();
		def = new TaskDefinition();
		def.setName("test2update");
		def.setDescription("description2update");
		defList.add(def);
		taskServiceConsumer.getTaskService().updateDefinition(longList, defList);
		IdentifierList identifierList = new IdentifierList();
		identifierList.add(new Identifier("test2update"));
		LongList list = taskServiceConsumer.getTaskService().listDefinition(identifierList);
		assertTrue(list.size() > 0);
	}
	
	@Test
	public void testRemoveDefinition() throws MALInteractionException, MALException {
		TaskDefinitionList defList = new TaskDefinitionList();
		TaskDefinition def = new TaskDefinition();
		def.setName("test");
		def.setDescription("description");
		TaskArgumentDefinitionList arguments = new TaskArgumentDefinitionList();
		def.setArguments(arguments);
		defList.add(def);
		LongList longList = taskServiceConsumer.getTaskService().addDefinition(defList);
		taskServiceConsumer.getTaskService().removeDefinition(longList);
		IdentifierList identifierList = new IdentifierList();
		identifierList.add(new Identifier("test2update"));
		LongList list = taskServiceConsumer.getTaskService().listDefinition(identifierList);
		boolean exists = false;
		Iterator<Long> it = list.iterator();
		while (it.hasNext()) {
			Long id = it.next();
			if (longList.get(0).equals(id)) {
				exists = true;
			}
		}
		assertTrue(!exists);
	}

}
