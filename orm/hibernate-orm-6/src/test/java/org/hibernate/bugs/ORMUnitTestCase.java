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
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.bugs.cl.Child;
import org.hibernate.bugs.cl.Parent;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.hibernate.testing.bytecode.enhancement.BytecodeEnhancerRunner;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using its built-in unit test framework.
 * Although ORMStandaloneTestCase is perfectly acceptable as a reproducer, usage of this class is much preferred.
 * Since we nearly always include a regression test with bug fixes, providing your reproducer using this method
 * simplifies the process.
 *
 * What's even better?  Fork hibernate-orm itself, add your test case directly to a module's unit tests, then
 * submit it as a PR!
 */

@RunWith(BytecodeEnhancerRunner.class)
public class ORMUnitTestCase extends BaseCoreFunctionalTestCase {

	// Add your entities here.
	@Override
	protected Class[] getAnnotatedClasses() {
		return new Class[] {
				Parent.class,
				Child.class,
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

		{
			Session s = openSession();
			Transaction tx = s.beginTransaction();

			Parent parent1 = new Parent();
			parent1 = s.merge(parent1);

			s.flush();

			// create two children with the same name, so that they differ only in their parent
			// otherwise the key doesn't trigger the exception
			Child child1 = new Child();
			child1.setName("name1");
			child1.setValue("value");
			parent1.addChild(child1);
			child1 = s.merge(child1);

			s.flush();
			s.clear();

			// The following fail with a NPE
			// List<Child> children = s.createQuery("select Child", Child.class).getResultList();
			// List<Child> children = s.createQuery("select Child from Child", Child.class).getResultList();
			// List<Child> children = s.createQuery("select Child from Child where 1=1", Child.class).getResultList();

			Query<Child> query = s.createQuery("select c from Child c where c.parent = :parent", Child.class);
			query.setParameter("parent", s.getReference(Parent.class, parent1.getId()));
			query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
			List<Child> children = query.getResultList();
			if (children.get(0).getParent() == null) {
				throw new NullPointerException();
			}

			tx.commit();
			s.close();

		}

	}
}
