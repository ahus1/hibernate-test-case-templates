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

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.bugs.cl.Child;
import org.hibernate.bugs.cl.Parent;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using its built-in unit test framework.
 * Although ORMStandaloneTestCase is perfectly acceptable as a reproducer, usage of this class is much preferred.
 * Since we nearly always include a regression test with bug fixes, providing your reproducer using this method
 * simplifies the process.
 *
 * What's even better?  Fork hibernate-orm itself, add your test case directly to a module's unit tests, then
 * submit it as a PR!
 */
public class ORMUnitTestCase extends BaseCoreFunctionalTestCase {

	// Add your entities here.
	@Override
	protected Class[] getAnnotatedClasses() {
		return new Class[] {
				Parent.class,
				Child.class
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
		//configuration.setProperty( AvailableSettings.GENERATE_STATISTICS, "true" );
	}

	// Add your tests, using standard JUnit.
	@Test
	public void hhh15230Test() throws Exception {
		// BaseCoreFunctionalTestCase automatically creates the SessionFactory and provides the S.
		Session s = openSession();
		Transaction tx = s.beginTransaction();

		// given...
		s.persist(new Parent());
		s.flush();

		List<Parent> results = s.createQuery("from parent", Parent.class).list();
		assertEquals(1, results.size());
		results = s.createQuery("from parent", Parent.class).list();
		assertEquals(1, results.size());
		Parent parent = (Parent) results.get(0);
		assertEquals(0, parent.getChildren().size());

		// when...
		// ... "Child 1" added
		Child c1 = new Child();
		c1.setParent(parent);
		c1.setName("Child 1");
		s.persist(c1);
		parent.getChildren().add(c1);

		// ... executing a query queues the entry into s.actionQueue.inserts,
		// but as auto-flush sees that parent is queried and not children, it doesn't flush them
		s.createQuery("from parent", Parent.class).list();

		// ... test will only be green with an explicit flush here
		// s.flush();

		// ... "Child 1" removed, expecting it to be orphan-removed from the database
		parent.getChildren().remove(c1);

		// ... "Child 2" added
		Child c2 = new Child();
		c2.setName("Child 2");
		c2.setParent(parent);
		s.persist(c2);
		parent.getChildren().add(c2);

		s.flush();
		s.clear();

		Parent parent2 = s.get(Parent.class, parent.getId());
		assertTrue(parent2.getChildren().stream().anyMatch(child -> child.getName().equals("Child 2")));
		// if there are 2 elements, the orphan removal didn't work as expected
		assertEquals(1, parent2.getChildren().size());


		tx.commit();
		s.close();
	}
}
