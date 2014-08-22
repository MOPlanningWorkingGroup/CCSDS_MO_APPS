package org.ccsds.moims.mo.mal.automation.test;

import java.util.HashMap;
import java.util.Map;

import org.ccsds.moims.mo.automation.procedureexecution.structures.ProcedureDefinition;
import org.ccsds.moims.mo.mal.automation.consumer.ProcedureExecutionServiceConsumer;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.jbpm.runtime.manager.impl.RuntimeEnvironmentBuilder;
import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.io.ResourceFactory;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:**/testContext.xml")
public class ProcessTest extends JbpmJUnitBaseTestCase {
	
	private static final Logger logger = LoggerFactory.getLogger(ProcessTest.class);
	private static RuntimeManager runtimeManager;
    private KieSession ksession;
    private RuntimeEngine runtimeEngine;
	
	@Autowired
	private ProcedureExecutionServiceConsumer procedureExecutionServiceConsumer;
	
    @Before
	public void init() throws Exception {
        logger.info("Loading EvaluationProcess.bpmn2");
        runtimeManager = getRuntimeManager("org/ccsds/moims/mo/mal/automation/automation.bpmn");
        runtimeEngine = runtimeManager.getRuntimeEngine(EmptyContext.get());
        ksession = runtimeEngine.getKieSession();
    }

    @After
    public void destroy() {
        ksession.destroy();
        runtimeManager.disposeRuntimeEngine(runtimeEngine);
        runtimeManager.close();
    }
	
    private RuntimeManager getRuntimeManager(String process) {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.getEmpty()
                .addAsset(ResourceFactory.newClassPathResource(process), ResourceType.BPMN2)
                .get();
        return RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
    }
	
	@Test
	public void testProcess() {
		logger.info("Register tasks");
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler("RegisterRequest", new SystemOutWorkItemHandler());
        Map<String, Object> params = new HashMap<String, Object>();
        //params.put("procedureExecutionServiceProvider", procedureExecutionServiceProvider);
        params.put("procedureExecutionServiceConsumer", procedureExecutionServiceConsumer);
        ProcedureDefinition procedureDefinition = new ProcedureDefinition();
		procedureDefinition.setName("procedureDefinition1");
		procedureDefinition.setDescription("description");
		params.put("procedureDefinition", procedureDefinition);
		params.put("defId", null);
        logger.info("Start process EvaluationProcess.bpmn2");
        ProcessInstance processInstance = ksession.startProcess("org.ccsds.moims.mo.mal.automation", params);
        logger.info("Stated completed");
	}

}
