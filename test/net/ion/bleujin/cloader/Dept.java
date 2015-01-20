package net.ion.bleujin.cloader;

import java.util.List;

import net.ion.framework.util.ListUtil;

public class Dept {

	private List<PersonSample> persons = ListUtil.newList();

	public Dept addPerson(String name) {
		persons.add(new PersonSample(name));
		return this;
	}

	public void printPersons() {
		for (PersonSample p : persons) {
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


class PersonSample {

	private String name;

	public PersonSample(String name) {
		this.name = name;
	}

	public String name() {
		return this.name;
	}
}
