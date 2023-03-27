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
import org.hibernate.bugs.cl.LazyChild;
import org.hibernate.bugs.cl.MyOtherEntity;
import org.hibernate.bugs.cl.Parent;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.collection.spi.PersistentBag;
import org.hibernate.testing.bytecode.enhancement.BytecodeEnhancerRunner;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.stream.Collectors;

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
				LazyChild.class,
				MyOtherEntity.class
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

		Long id;

		{
			Session s = openSession();
			Transaction tx = s.beginTransaction();

			Parent myParent = new Parent();
			s.persist(myParent);
			Child child = new Child();
			s.persist(child);
			child.setParent(myParent);
			myParent.getChildren().add(child);

			LazyChild lazyChild = new LazyChild();
			s.persist(lazyChild);
			lazyChild.setParent(myParent);
			myParent.getLazyChildren().add(lazyChild);

			id = myParent.getId();

			myParent.getEventsListeners().add("mylistener");

			tx.commit();
			s.close();

		}

		{
			Session s = openSession();
			Transaction tx = s.beginTransaction();

			Parent myParent = s.find(Parent.class, id, LockModeType.PESSIMISTIC_WRITE);

			s.refresh(myParent);

			s.remove(myParent);

			tx.commit();
			s.close();
		}

	}
}
