package org.hibernate.bugs;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.LockModeType;
import jakarta.persistence.Persistence;
import java.util.Set;

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

	// Entities are auto-discovered, so just add them anywhere on class-path
	// Add your tests, using standard JUnit.
	@Test
	public void hhh123Test() throws Exception {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		RealmEntity realm = new RealmEntity();
                realm.setId("id");
                realm.setName("realm");

                ComponentEntity c1 = new ComponentEntity();
                c1.setId("c1");
                c1.setRealm(realm);

                ComponentEntity c2 = new ComponentEntity();
                c2.setId("c2");
                c2.setRealm(realm);

                realm.setComponents(Set.of(c1, c2));
                realm.setEventsListeners(Set.of("l1"));
//                UserFederationMapperEntity mapper = new UserFederationMapperEntity();
//                mapper.setId("mapper1");
//                mapper.setRealm(realm);
//
//                realm.setUserFederationMappers(Set.of(mapper));
//
//                UserFederationProviderEntity provider = new UserFederationProviderEntity();
//                provider.setId("provider1");
//                provider.setRealm(realm);
//
//                realm.setUserFederationProviders(List.of(provider));
                entityManager.persist(realm);

                entityManager.getTransaction().commit();
                entityManager.getTransaction().begin();

                RealmEntity find = entityManager.find(RealmEntity.class, "id", LockModeType.PESSIMISTIC_WRITE);
                entityManager.refresh(realm);

//                s.find(ComponentEntity.class, "c1");

                entityManager.remove(find);
		entityManager.getTransaction().commit();
		entityManager.close();
	}
}
