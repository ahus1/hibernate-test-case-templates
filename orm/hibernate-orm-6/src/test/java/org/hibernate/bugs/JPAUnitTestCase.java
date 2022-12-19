package org.hibernate.bugs;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import jakarta.persistence.Query;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.Assertions;
import org.hibernate.bugs.cl.Parent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using the Java Persistence API.
 */
public class JPAUnitTestCase {

	private final static Logger LOGGER = LogManager.getLogger(JPAUnitTestCase.class);

	private EntityManagerFactory entityManagerFactory;

	@Before
	public void init() {
		entityManagerFactory = Persistence.createEntityManagerFactory( "templatePU" );
	}

	@After
	public void destroy() {
		entityManagerFactory.close();
	}

	// Entities are auto-discovered, so just add them anywhere on class-path
	// Add your tests, using standard JUnit.
	@Test
	public void hhh123Test() throws Exception {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		entityManager.persist(new Parent());

		// logs a warning in H6:
		// SqmDynamicInstantiation:174 - Argument [org.hibernate.bugs.cl.Parent as p] for dynamic Map instantiation did not declare an 'injection alias' [parent] but such aliases are needed for dynamic Map instantiations; will likely cause problems later translating sqm
		Query cb = entityManager.createQuery("select new map(p as parent) from parent p");
		List<Parent> resultList = cb.getResultList();

		Assertions.assertThat(resultList).hasSize(1);

		entityManager.getTransaction().commit();
		entityManager.close();
	}
}
