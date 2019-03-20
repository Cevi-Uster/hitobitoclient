package ch.cevi.db.client.business;

import java.util.Comparator;

import ch.cevi.db.client.business.entities.Person;

/**
 * Class to compare {@link Person}s by last name and first name.
 * 
 * @author developer
 *
 */
public class PersonByNameComparator implements Comparator<Person> {

	@Override
	public int compare(Person o1, Person o2) {
		int result = 0;

		if (o1 == o2) {
			result = 0;
		} else if (o1 == null && o2 != null) {
			result = -1;
		} else if (o1 != null && o2 == null) {
			result = 1;
		} else if (o1.getLastName() == null && o2.getLastName() == null) {
			result = 0;
		} else if (o1.getLastName() == null && o2.getLastName() != null) {
			result = -1;
		} else if (o1.getLastName() != null && o2.getLastName() == null) {
			result = -1;
		} else {
			result = o1.getLastName().compareTo(o2.getLastName());
		}
		if (result == 0 && o1 != null && o2 != null) {
			if (o1.getFirstName() == null && o2.getFirstName() == null) {
				result = 0;
			} else if (o1.getFirstName() == null && o2.getFirstName() != null) {
				result = -1;
			} else if (o1.getFirstName() != null && o2.getFirstName() == null) {
				result = -1;
			} else {
				result = o1.getFirstName().compareTo(o2.getFirstName());
			}
		}
		return result;
	}

}
