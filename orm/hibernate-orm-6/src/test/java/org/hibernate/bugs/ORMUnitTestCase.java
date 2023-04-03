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
import java.util.List;
import java.util.Set;
import org.hibernate.Session;
import org.hibernate.Transaction;
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
@RunWith(BytecodeEnhancerRunner.class)
public class ORMUnitTestCase extends BaseCoreFunctionalTestCase {

	// Add your entities here.
	@Override
	protected Class[] getAnnotatedClasses() {
		return new Class[] {
                    RealmEntity.class,
                    ComponentEntity.class,
                    ComponentConfigEntity.class,
                    UserFederationMapperEntity.class,
                    UserFederationProviderEntity.class
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
		//configuration.setProperty( AvailableSettings.GENERATE_STATISTICS, "true" );
	}

	// Add your tests, using standard JUnit.
	@Test
	public void hhh123Test() throws Exception {
		// BaseCoreFunctionalTestCase automatically creates the SessionFactory and provides the Session.
		Session s = openSession();
		Transaction tx = s.beginTransaction();
		// Do stuff...

                RealmEntity realm = new RealmEntity();
                realm.setId("id");
                realm.setName("realm");

                ComponentConfigEntity config = new ComponentConfigEntity();
                ComponentEntity c1 = new ComponentEntity();
                c1.setId("c1");
                c1.setRealm(realm);

                config.setId("config1");
                config.setName("config-name");
                config.setValue("value");
                config.setComponent(c1);

                c1.setComponentConfigs(Set.of(config));

                ComponentEntity c2 = new ComponentEntity();
                c2.setId("c2");
                c2.setRealm(realm);

                realm.setComponents(Set.of(c1, c2));
//                realm.setEventsListeners(Set.of("l1"));
                realm.setSupportedLocales(Set.of("locale1"));
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
                s.persist(realm);

                tx.commit();
                tx.begin();

                RealmEntity find = s.find(RealmEntity.class, "id", LockModeType.PESSIMISTIC_WRITE);
                realm.getComponents();
                s.refresh(realm);

                realm.getSupportedLocales();
                realm.getUserFederationMappers();
                realm.getComponents();
//                s.find(ComponentEntity.class, "c1");

                s.remove(find);
		tx.commit();
		s.close();
	}
}
