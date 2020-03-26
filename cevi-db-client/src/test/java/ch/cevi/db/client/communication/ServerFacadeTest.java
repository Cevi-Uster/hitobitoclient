package ch.cevi.db.client.communication;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import ch.cevi.db.client.business.entities.AdditionalMail;
import ch.cevi.db.client.business.entities.Person;
import ch.cevi.db.client.business.entities.PhoneNumber;
import ch.cevi.db.client.business.entities.Role;
import ch.cevi.db.client.business.entities.SocialAccount;
import ch.cevi.db.client.business.entities.YGroup;

public class ServerFacadeTest {

	@Test
	public void testGetMainGroup() throws Exception {
		System.out.println("************** testGetMainGroup ****************");
		ISession session = new Session("https://cevi.puzzle.ch", false);
		try {
			session.login("simba.uster@cevi.ch", "cevi14cevi");
			ServerFacade serverFacade = new ServerFacade(session);
			YGroup group = serverFacade.loadMainGroupWithDetails();
			assertNotNull(group);
			for (YGroup childGroup : group.getChildren()) {
				assertNotEquals(group.getId(), childGroup.getId());
			}
			System.out.println(group.getName());

			assertNotNull(group.getGroupRoles());

		} finally {
			session.logout();
		}
	}

	@Test
	public void testGetChildGroups() throws Exception {
		System.out.println("************** testGetChildGroups ****************");
		ISession session = new Session("https://cevi.puzzle.ch", false);
		try {
			session.login("simba.uster@cevi.ch", "cevi14cevi");
			ServerFacade serverFacade = new ServerFacade(session);
			YGroup group = serverFacade.loadMainGroupWithDetails();
			assertNotNull(group);
			System.out.println(group.getName());
			for (YGroup childGroup : group.getChildren()) {
				assertNotEquals(group.getId(), childGroup.getId());
				serverFacade.completeNotFullyLoadedGroup(childGroup);
				assertNotEquals(group.getId(), childGroup.getId());
				System.out.println("   " + childGroup.getName());
				for (YGroup grandChildGroup : childGroup.getChildren()) {
					assertNotEquals(childGroup.getId(), grandChildGroup.getId());
					assertNotEquals(group.getId(), grandChildGroup.getId());
					System.out.println("      " + grandChildGroup.getName());
				}
			}
		} finally {
			session.logout();
		}
	}

	@Test
	public void testGetPersons() throws Exception {
		System.out.println("************** testGetPersons ****************");
		ISession session = new Session("https://cevi.puzzle.ch", false);
		try {
			session.login("simba.uster@cevi.ch", "cevi14cevi");
			ServerFacade serverFacade = new ServerFacade(session);
			List<Person> personList = serverFacade.loadPersonList("52");
			assertNotNull(personList);
			for (Person p : personList) {
				System.out.println("Id: " + p.getId() + " Firstname: " + p.getFirstName() + " Lastname: " + p.getLastName());
				assertNotNull(p.getPhoneNumbers());
				for (PhoneNumber pn : p.getPhoneNumbers()) {
					System.out.println("\t Phone number: " + pn.getNumber() + " Label: " + pn.getLabel());
				}
				assertNotNull(p.getRoles());
				assertNotEquals(0, p.getRoles().size());
				for (Role role : p.getRoles()) {
					System.out.println("\t Role: " + role.getRoleType() + " Label: " + role.getLabel());
				}

				assertNotNull(p.getAdditionalMails());
				for (AdditionalMail additionalMail : p.getAdditionalMails()) {
					System.out.println("\t Email: " + additionalMail.getEmail() + " Label: " + additionalMail.getLabel() + " visibleToPublic: " + additionalMail.isPublicVisible());
				}
				assertNotNull(p.getSocialAccounts());
				for (SocialAccount socialAccount : p.getSocialAccounts()) {
					System.out.println("\t Name: " + socialAccount.getName() + " Label: " + socialAccount.getLabel() + " visibleToPublic: " + socialAccount.isPublicVisible());
				}
			}

		} finally {
			session.logout();
		}
	}
}
