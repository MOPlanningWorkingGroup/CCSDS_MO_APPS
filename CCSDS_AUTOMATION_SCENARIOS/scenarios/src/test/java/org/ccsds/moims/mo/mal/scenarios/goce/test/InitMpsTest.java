package org.ccsds.moims.mo.mal.scenarios.goce.test;

import java.util.HashMap;
import java.util.Map;

import org.ccsds.moims.mo.mal.planning.consumer.PlanningRequestServiceConsumer;
import org.ccsds.moims.mo.mal.planning.consumer.TaskServiceConsumer;
import org.ccsds.moims.mo.planning.task.structures.TaskDefinitionList;
import org.jbpm.runtime.manager.impl.RuntimeEnvironmentBuilder;
import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
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
@ContextConfiguration("classpath*:**/testPlanningContext.xml")
@Ignore
public class InitMpsTest extends JbpmJUnitBaseTestCase {
	
	private static final Logger logger = LoggerFactory.getLogger(InitMpsTest.class);
	private static RuntimeManager runtimeManager;
    private KieSession ksession;
    private RuntimeEngine runtimeEngine;
    
    @Autowired
	private TaskServiceConsumer taskServiceConsumer;
    
    @Autowired
	private PlanningRequestServiceConsumer planningRequestConsumer;
    
    @Before
	public void init() throws Exception {
        logger.info("Loading init_mps.bpmn2");
        runtimeManager = getRuntimeManager("org/ccsds/moims/mo/mal/scenarios/goce/init_mps.bpmn");
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
    	Map<String, Object> params = new HashMap<String, Object>();
        params.put("taskServiceConsumer", taskServiceConsumer);
        params.put("planningRequestConsumer", planningRequestConsumer);
        params.put("taskDefList", new TaskDefinitionList());
        params.put("taskDefLongList", null);
        params.put("prDef1", null);
        params.put("prDef2", null);
        params.put("prDefId1", null);
        params.put("prDefId2", null);
        ProcessInstance processInstance = ksession.startProcess("org.ccsds.moims.mo.mal.scenarios.goce", params);
        logger.info("Stated completed");
    }

}
