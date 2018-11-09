package at.fhj.swd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BidirectionalTest {

	private static EntityManagerFactory factory;
	private static EntityManager manager;
	private static EntityTransaction transaction;

	private static Person person;
	private static Address address;
	private static Department department;

	@BeforeClass
	public static void init() {
		factory = Persistence.createEntityManagerFactory("JPAtest");
		manager = factory.createEntityManager();
		transaction = manager.getTransaction();
	}

	@AfterClass
	public static void destroy() {
		manager.close();
		factory.close();
	}

	@Before
	public void setup() {
		person = new Person(1, "John", "Doe");
		address = new Address("Kasernstrasse 12", "Graz", "8010");
		department = new Department(1, "Marketing");

		person.setAddress(address);
		person.setDepartment(department);

		transaction.begin();
		manager.persist(department);
		manager.persist(person);
		transaction.commit();
	}

	@After
	public void tearDown() {
		Person p = manager.find(Person.class, person.getId());
		Department d = manager.find(Department.class, department.getId());

		transaction.begin();
		manager.remove(p);
		manager.remove(d);
		transaction.commit();
	}

	@Test
	public void testBidirectionals() {
		assertEquals(address, person.getAddress());
		assertEquals(person, address.getPerson());

		assertEquals(department, person.getDepartment());
		assertTrue(department.getPersons().contains(person));
	}

	@Test
	public void testPersistedBidirectionals() {
		Person p = manager.find(Person.class, person.getId());
		Department d = manager.find(Department.class, department.getId());
		Address a = p.getAddress();

		assertEquals(p, a.getPerson());
		assertEquals(d, p.getDepartment());
		assertTrue(d.getPersons().contains(p));
	}
}
