package org.ccsds.moims.mo.mal.scenarios.goce.test;

import java.util.HashMap;
import java.util.Map;

import org.ccsds.moims.mo.automation.scheduleexecution.structures.ScheduleFilter;
import org.ccsds.moims.mo.mal.automation.consumer.ScheduleExecutionServiceConsumer;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.jbpm.runtime.manager.impl.RuntimeEnvironmentBuilder;
import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:**/testContext.xml")
public class GoceScheduleSubmitInTcsTest extends JbpmJUnitBaseTestCase {
	
	private static final Logger logger = LoggerFactory
			.getLogger(GoceScheduleSubmitInTcsTest.class);
	private static RuntimeManager runtimeManager;
	private KieSession ksession;
	private RuntimeEngine runtimeEngine;
	
	@Autowired
	private ScheduleExecutionServiceConsumer scheduleExecutionServiceConsumer;
	
	@Before
	public void init() throws Exception {
		logger.info("Loading goce_pr_definition.bpmn");
		runtimeManager = getRuntimeManager(
				"org/ccsds/moims/mo/mal/scenarios/goce/goce_schedule_service_tcs.bpmn");
		runtimeEngine = runtimeManager.getRuntimeEngine(EmptyContext.get());
		ksession = runtimeEngine.getKieSession();
		TestWorkItemHandler testHandler = getTestWorkItemHandler();
		ksession.getWorkItemManager().registerWorkItemHandler("Log",
				testHandler);
	}

	@After
	public void destroy() {
		ksession.destroy();
		runtimeManager.disposeRuntimeEngine(runtimeEngine);
		runtimeManager.close();
	}

	private RuntimeManager getRuntimeManager(String process) {
		RuntimeEnvironment environment = RuntimeEnvironmentBuilder
				.getEmpty()
				.addAsset(ResourceFactory.newClassPathResource(process),
						ResourceType.BPMN2)
				.get();
		return RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(
				environment);
	}
	
	@Test
	public void testProcess() {
		ScheduleFilter scheduleFilter = new ScheduleFilter();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("i", 0);
        params.put("scheduleFilter", scheduleFilter);
        params.put("scheduleDefinitionList", null);
        params.put("currentScheduleDefinition", null);
        params.put("scheduleExecutionServiceConsumer", scheduleExecutionServiceConsumer);
        IdentifierList identifierList = new IdentifierList();
        params.put("identifierList", identifierList);
		ProcessInstance processInstance = ksession.startProcess(
				"goce_schedule_service", params);
		logger.info("Process completed");
	}

}
