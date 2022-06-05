package org.hibernate.bugs;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.assertj.core.api.Assertions;
import org.hibernate.Session;
import org.hibernate.bugs.cl.MyEntity;
import org.hibernate.bugs.cl.MyType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using the Java Persistence API.
 */
public class JPAUnitTestCase {

	private EntityManagerFactory entityManagerFactory;

	@Before
	public void init() {
		entityManagerFactory = Persistence.createEntityManagerFactory( "templatePU" );
	}

	@After
	public void destroy() {
		entityManagerFactory.close();
	}

	// Show that
	@Test
	public void hhh15317Test() throws Exception {

		Long id;

		{
			EntityManager entityManager = entityManagerFactory.createEntityManager();
			entityManager.getTransaction().begin();
			MyEntity myEntity = new MyEntity();
			MyType myType = new MyType();
			myType.setMutableState("A");
			myEntity.setMyType(myType);
			entityManager.persist(myEntity);
			entityManager.getTransaction().commit();
			id = myEntity.getId();
		}

		{
			EntityManager entityManager = entityManagerFactory.createEntityManager();
			entityManager.getTransaction().begin();
			entityManager.unwrap(Session.class).setDefaultReadOnly(true);
			MyEntity myEntity = entityManager.find(MyEntity.class, id);
			Assertions.assertThat(myEntity.getMyType().getMutableState()).isEqualTo("A");

			// BUG: when calling "setReadOnly(..., false)" this misses to clone the mutable type field
			entityManager.unwrap(Session.class).setReadOnly(myEntity, false);

			myEntity.getMyType().setMutableState("B");
			Assertions.assertThat(myEntity.getMyType().getMutableState()).isEqualTo("B");
			entityManager.getTransaction().commit();
		}

		{
			EntityManager entityManager = entityManagerFactory.createEntityManager();
			entityManager.getTransaction().begin();
			MyEntity myEntity = entityManager.find(MyEntity.class, id);

			// will fail, as mutation hasn't been detected in previous block
			Assertions.assertThat(myEntity.getMyType().getMutableState()).isEqualTo("B");

			entityManager.getTransaction().commit();
		}


	}
}
