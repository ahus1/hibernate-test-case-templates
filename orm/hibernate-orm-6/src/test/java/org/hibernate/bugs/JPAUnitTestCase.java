package org.hibernate.bugs;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.bugs.cl.Parent;
import org.hibernate.query.spi.QueryImplementor;
import org.hibernate.query.sqm.internal.QuerySqmImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Parent> cq = cb.createQuery(Parent.class);
		Root<Parent> root = cq.from(Parent.class);
		root.alias("generatedAlias0");
		cq.select(root).where(cb.equal(root.get("name"), "main"));

		TypedQuery<Parent> typedQuery = entityManager.createQuery(cq);

		QueryImplementor<?> query = typedQuery.unwrap(QueryImplementor.class);

		// Fails on H6. Instead, it's only "<criteria>"
		// Assert.assertEquals("select generatedAlias0 from parent as generatedAlias0 where generatedAlias0.name=:param0", query.getQueryString());

		// will contain a dynamic alias unless set with an alias above
		Assert.assertEquals("select generatedAlias0 as generatedAlias0 from org.hibernate.bugs.cl.Parent generatedAlias0 where generatedAlias0.name = main",
				((QuerySqmImpl<?>) query).getSqmStatement().toHqlString());

		// THEREFORE: SQL is not usable as a key for caching

		query.getParameters().forEach(parameter -> {
			// Succeed on H5, fails on H6 as the parameter name is null - how's that?
			// The parameter is already replaced in the HSQL, so no harm done here to ignore those without a name?
			Assert.assertNotNull(parameter.getName());
		});

		entityManager.getTransaction().commit();
		entityManager.close();
	}
}
