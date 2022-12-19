/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hibernate.bugs;

import jakarta.persistence.LockModeType;
import org.assertj.core.api.Assertions;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.bugs.cl.PolicyEntity;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.testing.bytecode.enhancement.BytecodeEnhancerRunner;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using its built-in unit test framework.
 * Although ORMStandaloneTestCase is perfectly acceptable as a reproducer, usage of this class is much preferred.
 * Since we nearly always include a regression test with bug fixes, providing your reproducer using this method
 * simplifies the process.
 *
 * What's even better?  Fork hibernate-orm itself, add your test case directly to a module's unit tests, then
 * submit it as a PR!
 */

// @RunWith(BytecodeEnhancerRunner.class)
public class ORMUnitTestCase extends BaseCoreFunctionalTestCase {

	// Add your entities here.
	@Override
	protected Class[] getAnnotatedClasses() {
		return new Class[] {
				PolicyEntity.class,
//				Foo.class,
//				Bar.class
		};
	}

	// If you use *.hbm.xml mappings, instead of annotations, add the mappings here.
	@Override
	protected String[] getMappings() {
		return new String[] {
//				"Foo.hbm.xml",
//				"Bar.hbm.xml"
		};
	}
	// If those mappings reside somewhere other than resources/org/hibernate/test, change this.
	@Override
	protected String getBaseForMappings() {
		return "org/hibernate/test/";
	}

	// Add in any settings that are specific to your test.  See resources/hibernate.properties for the defaults.
	@Override
	protected void configure(Configuration configuration) {
		super.configure( configuration );

		configuration.setProperty( AvailableSettings.SHOW_SQL, Boolean.TRUE.toString() );
		configuration.setProperty( AvailableSettings.FORMAT_SQL, Boolean.TRUE.toString() );
		configuration.setProperty( AvailableSettings.ORDER_UPDATES, Boolean.TRUE.toString() );
		//configuration.setProperty( AvailableSettings.GENERATE_STATISTICS, "true" );
	}

	// Add your tests, using standard JUnit.
	@Test
	public void hhh123Test() throws Exception {
		// BaseCoreFunctionalTestCase automatically creates the SessionFactory and provides the Session.

		Long id1, id2;

		{
			Session s = openSession();
			Transaction tx = s.beginTransaction();

			PolicyEntity p1 = new PolicyEntity();
			s.persist(p1);
			id1 = p1.getId();

			PolicyEntity p2 = new PolicyEntity();
			s.persist(p2);
			id2 = p2.getId();

			p1.addAssociatedPolicy(p2);

			tx.commit();
			s.close();

		}

		{
			Session s = openSession();
			Transaction tx = s.beginTransaction();

			PolicyEntity p1 = s.find(PolicyEntity.class, id1, LockModeType.PESSIMISTIC_WRITE);
			PolicyEntity p2 = s.find(PolicyEntity.class, id2, LockModeType.PESSIMISTIC_WRITE);

			Assertions.assertThat(p1.getAssociatedPolicies()).hasSize(1);
			Assertions.assertThat(p2.getRootPolicies()).hasSize(1);

			tx.commit();
			s.close();
		}

		{
			Session s = openSession();
			Transaction tx = s.beginTransaction();

			PolicyEntity p1 = s.find(PolicyEntity.class, id1, LockModeType.PESSIMISTIC_WRITE);
			PolicyEntity p2 = s.find(PolicyEntity.class, id2, LockModeType.PESSIMISTIC_WRITE);

			p1.removeAssociatedPolicy(p2);

			s.flush();

			// a flush will update the non-owning side even if removeAssociatedPolicy() doesn't do it
			Assertions.assertThat(p1.getAssociatedPolicies()).hasSize(0);
			Assertions.assertThat(p2.getRootPolicies()).hasSize(0);

			tx.commit();
			s.close();
		}

		{
			Session s = openSession();
			Transaction tx = s.beginTransaction();

			PolicyEntity p1 = s.find(PolicyEntity.class, id1, LockModeType.PESSIMISTIC_WRITE);
			PolicyEntity p2 = s.find(PolicyEntity.class, id2, LockModeType.PESSIMISTIC_WRITE);

			Assertions.assertThat(p1.getAssociatedPolicies()).hasSize(0);
			Assertions.assertThat(p2.getRootPolicies()).hasSize(0);

			tx.commit();
			s.close();
		}

	}
}
