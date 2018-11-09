package at.fhj.swd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProjectTest {

	private static EntityManagerFactory factory;
	private static EntityManager manager;
	private static EntityTransaction transaction;

	private static Person person;
	private static Person person2;
	private static Address address;
	private static Address address2;
	private static Department department;
	private static Project project;
	private static Project project2;

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
		person2 = new Person(2, "Frank", "Tuttle");
		address = new Address("Kasernstrasse 12", "Graz", "8010");
		address2 = new Address("Lendgasse 1", "Graz", "8020");
		department = new Department(1, "Marketing");
		project = new Project(1, "Project 1");
		project2 = new Project(2, "Project 2");

		person.setAddress(address);
		person2.setAddress(address2);
		person.setDepartment(department);
		person2.setDepartment(department);

		person.add(project);
		person.add(project2);

		person2.add(project);
	}

	@Test
	public void create() {
		assertNotNull(person);
		assertNotNull(address);
		assertNotNull(person2);
		assertNotNull(address2);
		assertNotNull(department);

		transaction.begin();
		manager.persist(department);
		manager.persist(project);
		manager.persist(project2);

		manager.persist(person);
		manager.persist(person2);
		transaction.commit();

		Person p1 = manager.find(Person.class, person.getId());
		Person p2 = manager.find(Person.class, person2.getId());

		assertNotNull(p1);
		assertNotNull(p2);

		assertNotNull(p1.getAddress());
		assertNotNull(p2.getAddress());

		assertNotNull(p1.getDepartment());
		assertNotNull(p2.getDepartment());

		assertEquals("Marketing", p1.getDepartment().getName());
		assertEquals("Marketing", p2.getDepartment().getName());

		assertTrue(p1.getProjects().contains(project));
		assertTrue(p1.getProjects().contains(project2));
		assertTrue(p2.getProjects().contains(project));
	}

	@Test
	public void remove() {
		Person p = manager.find(Person.class, person.getId());
		Person p2 = manager.find(Person.class, person2.getId());
		Department d = manager.find(Department.class, department.getId());
		Project pr = manager.find(Project.class, project.getId());
		Project pr2 = manager.find(Project.class, project2.getId());

		transaction.begin();

		// remove person from database, cascades to address as well
		manager.remove(p);
		manager.remove(p2);

		// removal of department is only possible when it is empty
		manager.remove(d);

		manager.remove(pr);
		manager.remove(pr2);

		transaction.commit();

		assertNull(manager.find(Person.class, person.getId()));
		assertNull(manager.find(Person.class, person2.getId()));

		assertNull(manager.find(Address.class, person.getId()));
		assertNull(manager.find(Address.class, person2.getId()));

		assertNull(manager.find(Department.class, department.getId()));

		assertNull(manager.find(Project.class, project.getId()));
		assertNull(manager.find(Project.class, project2.getId()));
	}

}
