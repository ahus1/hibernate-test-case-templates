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

import java.util.HashMap;
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

		// create an entity in the database
		Parent entity = new Parent();
		entity.setData(new HashMap<>());
		entityManager.persist(entity);
		entityManager.flush();
		Long id = entity.getId();
		entityManager.clear();

		// re-read from the database, and make a change that should trigger a writing to the DB due to dirty checking
		entity = entityManager.find(Parent.class, id);

		// dirty checking in H6 requires all converted attributes to be immutable - THIS TEST WILL FAIL

		// therefore: cloning the attribute value
		// FIX START
		// entity.setData(new HashMap<>(entity.getData()));
		// FIX END

		entity.getData().put("key", "value");

		// re-read from the database, and see if the data has been written
		entityManager.flush();
		entityManager.clear();
		entity = entityManager.find(Parent.class, id);
		Assertions.assertThat(entity.getData()).hasSize(1);

		entityManager.getTransaction().commit();
		entityManager.close();
	}
}
