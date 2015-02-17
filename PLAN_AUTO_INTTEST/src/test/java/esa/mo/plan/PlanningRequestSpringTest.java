package esa.mo.plan;

import static org.junit.Assert.*;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import esa.mo.plan.provider.PlanningRequestProvider;

// http://www.programcreek.com/2014/01/spring-helloworld-example-using-eclipse-and-maven/
public class PlanningRequestSpringTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws MALException, MALInteractionException {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("testProvContext.xml");
		Object bean = ctx.getBean("PlanningRequestProvider");
		assertTrue(bean instanceof PlanningRequestProvider);
		PlanningRequestProvider svc = (PlanningRequestProvider)bean;
		
		IdentifierList idList = new IdentifierList();
		idList.add(new Identifier("*"));
		LongList taskIdList = svc.listTaskDefinition(idList, null);
		assertNotNull(taskIdList);
	}

}
