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
import org.hibernate.bugs.cl.MyEntity;
import org.hibernate.bugs.cl.MyOtherEntity;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.testing.bytecode.enhancement.BytecodeEnhancerRunner;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;
import java.util.Set;

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
				MyEntity.class,
				MyOtherEntity.class
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
	public void hhh123Test() {
		// BaseCoreFunctionalTestCase automatically creates the SessionFactory and provides the Session.
		Long id;

		{
			Session s = openSession();
			s.getTransaction().begin();

			MyEntity myEntity = new MyEntity();
			myEntity.getRedirectUris().add("1");
			myEntity = s.merge(myEntity);
			id = myEntity.getId();
			s.getTransaction().commit();
			s.close();
		}
		{
			Session s = openSession();
			s.getTransaction().begin();

			MyEntity myEntity = s.find(MyEntity.class, id);

			Set<String> set = new HashSet<>();
			set.add("2");
			myEntity.setRedirectUris(set);

			log.info("running select for other entity, causing a auto-flush check, but no flush");
			s.createQuery("from MyOtherEntity ", MyOtherEntity.class).getResultList();

			log.info("Now the CollectionEntry was initialized with ignore=true, which will prevent ");

			s.getTransaction().commit();
			s.close();
		}

		{
			Session s = openSession();
			s.getTransaction().begin();

			MyEntity myEntity = s.find(MyEntity.class, id);

			// as the delete wasn't triggered, this will fail with 2 entries
			// but only for BytecodeEnhancerRunner
			Assert.assertEquals(1, myEntity.getRedirectUris().size());

			s.getTransaction().commit();
			s.close();
		}

	}
}
