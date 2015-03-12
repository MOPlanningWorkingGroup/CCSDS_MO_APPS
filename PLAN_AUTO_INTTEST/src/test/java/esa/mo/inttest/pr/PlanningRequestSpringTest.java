package esa.mo.inttest.pr;

import static org.junit.Assert.*;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.planning.planningrequest.structures.DefinitionType;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import esa.mo.inttest.pr.provider.PlanningRequestProvider;

/**
 * Testing how it works with spring.
 * Guide from http://www.programcreek.com/2014/01/spring-helloworld-example-using-eclipse-and-maven/
 */
public class PlanningRequestSpringTest {

	@Test
	public void test() throws MALException, MALInteractionException {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("testProvContext.xml");
		Object bean = ctx.getBean("PlanningRequestProvider");
		assertTrue(bean instanceof PlanningRequestProvider);
		PlanningRequestProvider svc = (PlanningRequestProvider)bean;
		
		IdentifierList idList = new IdentifierList();
		idList.add(new Identifier("*"));
		LongList taskIdList = svc.listDefinition(DefinitionType.TASK_DEF, idList, null);
		assertNotNull(taskIdList);
	}

}
