package ch.cevi.db.client.business;

import java.util.Comparator;

import ch.cevi.db.client.business.entities.YGroup;

public class GroupByNameComparator implements Comparator<YGroup> {

	@Override
	public int compare(YGroup o1, YGroup o2) {
		if (o1 == o2) {
			return 0;
		} else if (o1 == null && o2 != null) {
			return -1;
		} else if (o1 != null && o2 == null) {
			return 1;
		} else if (o1.getName() == null && o2.getName() == null) {
			return 0;
		} else if (o1.getName() == null && o2.getName() != null) {
			return -1;
		} else if (o1.getName() != null && o2.getName() == null) {
			return -1;
		} else {
			return o1.getName().compareTo(o2.getName());
		}
	}

}
