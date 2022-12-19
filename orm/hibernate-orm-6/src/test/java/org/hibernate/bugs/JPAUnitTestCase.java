package org.hibernate.bugs;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.LockModeType;
import jakarta.persistence.Persistence;

import org.hibernate.bugs.cl.Child;
import org.hibernate.bugs.cl.Parent;
import org.hibernate.testing.bytecode.enhancement.BytecodeEnhancerRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using the Java Persistence API.
 */
@RunWith(BytecodeEnhancerRunner.class)
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

	// Entities are auto-discovered, so just add them anywhere on class-path
	// Add your tests, using standard JUnit.
	@Test
	public void hhh123Test() throws Exception {
		Long id;

		{
			EntityManager entityManager = entityManagerFactory.createEntityManager();
			entityManager.getTransaction().begin();

			Parent myParent = new Parent();
			myParent = entityManager.merge(myParent);
			Child child = entityManager.merge(new Child());
			myParent.addChild(child);
			id = myParent.getId();
			entityManager.flush();
			entityManager.clear();
			entityManager.getTransaction().commit();
			entityManager.close();
		}
		{

			EntityManager entityManager = entityManagerFactory.createEntityManager();
			entityManager.getTransaction().begin();

			// myParent = entityManager.find(Parent.class, myParent.getId());

			Parent myParent = entityManager.find(Parent.class, id, LockModeType.WRITE);
			entityManager.refresh(myParent);
			myParent.getChildren().size();

			myParent = entityManager.find(Parent.class, id, LockModeType.WRITE);
			entityManager.refresh(myParent);
			myParent.getChildren().size();

			entityManager.close();
		}
	}
}
