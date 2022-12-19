package org.hibernate.bugs;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.bugs.cl.Parent;
import org.hibernate.query.spi.QueryImplementor;
import org.junit.After;
import org.junit.Assert;
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

	// Entities are auto-discovered, so just add them anywhere on class-path
	// Add your tests, using standard JUnit.
	@Test
	public void hhh123Test() throws Exception {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Parent> cq = cb.createQuery(Parent.class);
		Root<Parent> root = cq.from(Parent.class);
		cq.select(root).where(cb.equal(root.get("name"), "main"));

		TypedQuery<Parent> typedQuery = entityManager.createQuery(cq);

		QueryImplementor<?> query = typedQuery.unwrap(QueryImplementor.class);

		Assert.assertEquals("select generatedAlias0 from parent as generatedAlias0 where generatedAlias0.name=:param0", query.getQueryString());

		query.getParameters().forEach(parameter -> {
			// succeed on H5, fails on H6 as the parameter name is null
			Assert.assertNotNull(parameter.getName());
		});

		// THEN: our code uses queryString + parameters as a caching key to cache the results within a session

		entityManager.getTransaction().commit();
		entityManager.close();
	}
}
