package org.ccsds.moims.mo.mal.planning.dao.impl;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.ccsds.moims.mo.mal.planning.dao.TaskDao;
import org.springframework.stereotype.Repository;

@Repository
public class TaskDaoImpl implements TaskDao {

	@PersistenceUnit(unitName="planning-persistence")
    private EntityManagerFactory factory;
	
}
