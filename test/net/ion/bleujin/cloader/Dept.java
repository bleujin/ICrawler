package net.ion.bleujin.cloader;

import java.util.List;

import net.ion.framework.util.ListUtil;

public class Dept {

	private List<Person> persons = ListUtil.newList();

	public Dept addPerson(String name) {
		persons.add(new Person(name));
		return this;
	}

	public void printPersons() {
		for (Person p : persons) {
			System.out.println(p.name());
		}
	}

	public static void main(String[] args) {
		Dept d = new Dept();

		d.addPerson("bleujin");
		d.addPerson("hero");
		d.printPersons();
	}
}


class Person {

	private String name;

	public Person(String name) {
		this.name = name;
	}

	public String name() {
		return this.name;
	}
}
