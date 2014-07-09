package org.ccsds.moims.mo.mal.planning.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.logging.Logger;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.planning.consumer.TaskServiceConsumer;
import org.ccsds.moims.mo.mal.planning.provider.TaskServiceProvider;
import org.ccsds.moims.mo.mal.planning.service.TaskServiceImpl;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.planning.task.structures.TaskArgumentDefinitionList;
import org.ccsds.moims.mo.planning.task.structures.TaskDefinition;
import org.ccsds.moims.mo.planning.task.structures.TaskDefinitionList;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TaskServiceTest {
	
	public static final Logger LOGGER = Logger
			.getLogger(TaskServiceTest.class.getName());
	
	private static TaskServiceProvider provider = null;
	private static TaskServiceConsumer consumer = null;
	
	@BeforeClass
	public static void testSetup() throws IOException, MALInteractionException,
			MALException {
		// set up provider
		provider = new TaskServiceProvider(
				new TaskServiceImpl());
		provider.setPropertyFile("/demoProvider.properties");
		provider.start();

		// set up consumer
		consumer = new TaskServiceConsumer("TASK_SUB");
		consumer.setPropertyFile("/demoConsumer.properties");
		consumer.setBroker(provider.getBrokerUri());
		consumer.setUri(provider.getUri());
		consumer.start();
	}
	
	@AfterClass
	public static void testCleanup() throws MALException,
			MALInteractionException {
		if (consumer != null) {
			consumer.stop();
		}
		if (provider != null) {
			provider.stop();
		}
	}
	
	@Test
	public void testListDefinition() throws MALInteractionException, MALException {
		TaskDefinitionList defList = new TaskDefinitionList();
		TaskDefinition def = new TaskDefinition();
		def.setName("test");
		def.setDescription("description");
		TaskArgumentDefinitionList arguments = new TaskArgumentDefinitionList();
		def.setArguments(arguments);
		defList.add(def);
		consumer.getTaskService().addDefinition(defList);
		IdentifierList identifierList = new IdentifierList();
		identifierList.add(new Identifier("test"));
		identifierList.add(new Identifier("test2"));
		LongList list = consumer.getTaskService().listDefinition(identifierList);
		assertTrue(list.size() == 1);
	}
	
	@Test
	public void testAddDefinition() throws MALInteractionException, MALException {
		TaskDefinitionList defList = new TaskDefinitionList();
		TaskDefinition def = new TaskDefinition();
		def.setName("test2");
		def.setDescription("description2");
		defList.add(def);
		consumer.getTaskService().addDefinition(defList);
		TaskArgumentDefinitionList arguments = new TaskArgumentDefinitionList();
		def.setArguments(arguments);
		defList.add(def);
		LongList longList = consumer.getTaskService().addDefinition(defList);
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
		LongList longList = consumer.getTaskService().addDefinition(defList);
		defList = new TaskDefinitionList();
		def = new TaskDefinition();
		def.setName("test2update");
		def.setDescription("description2update");
		defList.add(def);
		consumer.getTaskService().updateDefinition(longList, defList);
		IdentifierList identifierList = new IdentifierList();
		identifierList.add(new Identifier("test2update"));
		LongList list = consumer.getTaskService().listDefinition(identifierList);
		assertTrue(list.size() == 1);
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
		LongList longList = consumer.getTaskService().addDefinition(defList);
		consumer.getTaskService().removeDefinition(longList);
		IdentifierList identifierList = new IdentifierList();
		identifierList.add(new Identifier("test2update"));
		LongList list = consumer.getTaskService().listDefinition(identifierList);
		assertTrue(list.size() == 0);
	}

}
