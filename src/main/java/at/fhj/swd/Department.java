package at.fhj.swd;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Department {

	@OneToMany(mappedBy = "department")
	private Collection<Person> persons = new ArrayList<Person>();

	public Collection<Person> getPersons() {
		return persons;
	}

	public void addPerson(Person person) {
		persons.add(person);
	}

	@Id
	private int id;

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	protected Department() {
	};

	public Department(int id, String name) {
		setId(id);
		setName(name);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
