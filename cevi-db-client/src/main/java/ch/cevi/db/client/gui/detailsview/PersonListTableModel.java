package ch.cevi.db.client.gui.detailsview;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cevi.db.client.business.entities.Person;
import ch.cevi.db.client.business.entities.YGroup;
import ch.cevi.db.client.configuration.Texts;

public class PersonListTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -8938603953164722545L;

	private static Logger LOGGER = LoggerFactory.getLogger(PersonListTableModel.class.getName());

	public static final int COLUMN_ID = 0;

	public static final int COLUMN_FIRSTNAME = 1;

	public static final int COLUMN_LASTNAME = 2;

	public static final int COLUMN_NICKNAME = 3;

	public static final int COLUMN_COMPANYNAME = 4;

	public static final int COLUMN_IS_COMPANY = 5;

	public static final int COLUMN_GENDER = 6;

	public static final int COLUMN_EMAIL = 7;

	public static final int COLUMN_ADDRESS = 8;

	public static final int COLUMN_ZIPCODE = 9;

	public static final int COLUMN_TOWN = 10;

	public static final int COLUMN_COUNTRY = 11;

	public static final int COLUMN_LOCAL_DEPARTMENTS = 12;

	private static final String[] COLUMNS = { Texts.getTranslatedText(Texts.PERSON_LIST_COLUMN_ID), Texts.getTranslatedText(Texts.PERSON_LIST_COLUMN_FIRSTNAME),
			Texts.getTranslatedText(Texts.PERSON_LIST_COLUMN_LASTNAME), Texts.getTranslatedText(Texts.PERSON_LIST_COLUMN_NICKNAME), Texts.getTranslatedText(Texts.PERSON_LIST_COLUMN_COMPANYNAME),
			Texts.getTranslatedText(Texts.PERSON_LIST_COLUMN_IS_COMPANY), Texts.getTranslatedText(Texts.PERSON_LIST_COLUMN_GENDER), Texts.getTranslatedText(Texts.PERSON_LIST_COLUMN_EMAIL),
			Texts.getTranslatedText(Texts.PERSON_LIST_COLUMN_ADDRESS), Texts.getTranslatedText(Texts.PERSON_LIST_COLUMN_ZIPCODE), Texts.getTranslatedText(Texts.PERSON_LIST_COLUMN_TOWN),
			Texts.getTranslatedText(Texts.PERSON_LIST_COLUMN_COUNTRY), Texts.getTranslatedText(Texts.PERSON_LIST_COLUMN_LOCALDEPARTMENTS) };

	private static final Class<?>[] COLUMN_CLASSES = { String.class, String.class, String.class, String.class, String.class, Boolean.class, String.class, String.class, String.class, String.class,
			String.class, String.class, String.class };

	private YGroup group;

	private List<Person> personList;

	public PersonListTableModel() {
		this.personList = new ArrayList<Person>();
	}

	@Override
	public String getColumnName(int column) {
		return COLUMNS[column];
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return COLUMN_CLASSES[columnIndex];
	}

	@Override
	public int getRowCount() {
		return personList.size();
	}

	@Override
	public int getColumnCount() {
		return COLUMNS.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object value = null;
		Person person = personList.get(rowIndex);
		switch (columnIndex) {
		case COLUMN_ID:
			value = person.getId();
			break;
		case COLUMN_FIRSTNAME:
			value = person.getFirstName();
			break;
		case COLUMN_LASTNAME:
			value = person.getLastName();
			break;
		case COLUMN_NICKNAME:
			value = person.getNickname();
			break;
		case COLUMN_COMPANYNAME:
			value = person.getCompanyName();
			break;
		case COLUMN_IS_COMPANY:
			value = person.isCompany();
			break;
		case COLUMN_GENDER:
			value = person.getGender();
			break;
		case COLUMN_EMAIL:
			value = person.getEmail();
			break;
		case COLUMN_ADDRESS:
			value = person.getAddress();
			break;
		case COLUMN_ZIPCODE:
			value = person.getZipCode();
			break;
		case COLUMN_TOWN:
			value = person.getTown();
			break;
		case COLUMN_COUNTRY:
			value = person.getCountry();
			break;
		case COLUMN_LOCAL_DEPARTMENTS:
			// not implemented
			value = null;
			break;
		default:
			LOGGER.error("Column out of range in PersonListTableModel");
			break;
		}

		return value;
	}

	public YGroup getGroup() {
		return group;
	}

	public void setGroup(YGroup group) {
		this.group = group;
		personList.clear();
		if (group != null) {
			personList.addAll(group.getGroupMembers());
		}
		fireTableDataChanged();
	}

}
