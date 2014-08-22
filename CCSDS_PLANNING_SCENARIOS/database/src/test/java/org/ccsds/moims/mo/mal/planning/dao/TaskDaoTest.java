package org.ccsds.moims.mo.mal.planning.dao;

import org.apache.log4j.Logger;
import org.ccsds.moims.mo.mal.planning.dao.impl.TaskDaoImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:**/jpaPlanningContext.xml")
public class TaskDaoTest {
	
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(TaskDaoTest.class);
	
	@Autowired
	private TaskDaoImpl taskDao;
	
	@Test
	public void testTaskDao() {

	}

}
