package org.hibernate.bugs;

import java.sql.Connection;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.LockModeType;
import jakarta.persistence.Table;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.mysql.MySQLContainer;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Reproducer for a repeated pessimistic refresh restoring stale state on MySQL under REPEATABLE READ.
 */
class ORMStandaloneTestCase {

	private static final MySQLContainer mysql = new MySQLContainer( "mysql:9.7" )
			.withDatabaseName( "hibernate_orm_test" )
			.withUsername( "hibernate_orm_test" )
			.withPassword( "hibernate_orm_test" );

	private SessionFactory sf;

	@BeforeAll
	static void startMySQL() {
		mysql.start();
	}

	@AfterAll
	static void stopMySQL() {
		mysql.stop();
	}

	@BeforeEach
	void setup() {
		StandardServiceRegistryBuilder srb = new StandardServiceRegistryBuilder()
				.clearSettings()
				.applySetting( AvailableSettings.DIALECT, "org.hibernate.dialect.MySQLDialect" )
				.applySetting( AvailableSettings.JAKARTA_JDBC_DRIVER, mysql.getDriverClassName() )
				.applySetting( AvailableSettings.JAKARTA_JDBC_URL, mysql.getJdbcUrl() )
				.applySetting( AvailableSettings.JAKARTA_JDBC_USER, mysql.getUsername() )
				.applySetting( AvailableSettings.JAKARTA_JDBC_PASSWORD, mysql.getPassword() )
				.applySetting( AvailableSettings.ISOLATION, Connection.TRANSACTION_REPEATABLE_READ )
				.applySetting( "hibernate.show_sql", "true" )
				.applySetting( "hibernate.format_sql", "true" )
				.applySetting( "hibernate.hbm2ddl.auto", "create-drop" );

		Metadata metadata = new MetadataSources( srb.build() )
				.addAnnotatedClass( Item.class )
				.buildMetadata();

		sf = metadata.buildSessionFactory();
	}

	@AfterEach
	void tearDown() {
		if ( sf != null ) {
			sf.close();
		}
	}

	@Test
	void repeatedPessimisticRefreshMustNotRestoreStaleState() {
		inTransaction( session -> session.persist( new Item( 1L, "initial" ) ) );

		try ( Session transactionASession = sf.openSession() ) {
			Transaction transactionA = transactionASession.beginTransaction();
			try {
				// This ordinary read establishes Transaction A's consistent-read snapshot.
				Item item = transactionASession.find( Item.class, 1L );
				assertEquals( "initial", item.value );

				// Transaction B commits a value that is newer than Transaction A's snapshot.
				inTransaction( session -> session.find( Item.class, 1L ).value = "updated" );

				// A locking read is a current read on MySQL and sees Transaction B's update.
				transactionASession.refresh( item, LockModeType.PESSIMISTIC_WRITE );
				assertEquals( "updated", item.value );

				// Hibernate omits FOR UPDATE because the persistence context already records the lock.
				// The resulting consistent read consults A's old snapshot and restores "initial".
				transactionASession.refresh( item, LockModeType.PESSIMISTIC_WRITE );
				assertEquals( "updated", item.value,
						"An explicit pessimistic refresh must not replace current state with stale snapshot state" );
			}
			finally {
				transactionA.rollback();
			}
		}
	}

	private void inTransaction(java.util.function.Consumer<Session> work) {
		try ( Session session = sf.openSession() ) {
			Transaction transaction = session.beginTransaction();
			try {
				work.accept( session );
				transaction.commit();
			}
			catch (RuntimeException | Error e) {
				transaction.rollback();
				throw e;
			}
		}
	}

	@Entity(name = "Item")
	@Table(name = "test_item")
	static class Item {
		@Id
		Long id;

		@Column(name = "item_value")
		String value;

		Item() {
		}

		Item(Long id, String value) {
			this.id = id;
			this.value = value;
		}
	}
}
