package ch.cevi.db.client.business;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import ch.cevi.db.client.business.entities.Role;

/**
 * Comparator class to compare two {@link Role} objects.
 * 
 * @author developer
 *
 */
public class RoleComparator implements Comparator<Role> {

	/* @formatter:off */
	private static String[] ROLE_ORDER = { "Dachverband.Administrator/-in", "Dachverband.Vorstand.Präsidium", "Dachverband.Vorstand.Finanzverantwortliche/-r", "Dachverband.Vorstand.Mitglied",

			"Dachverband.Geschäftsstelle.Geschäftsleiter/-in", "Dachverband.Geschäftsstelle.Angestellte/-r", "Dachverband.Geschäftsstelle.Finanzverantwortliche/-r",

			"Dachverband.Gremium.Leitung", "Dachverband.Gremium.Mitglied", "Dachverband.Gremium.Aktive/-r Kursleiter/-in",

			"Dachverband.Mitglieder.Adressverwalter/-in", "Dachverband.Mitglieder.Mitglied",

			"Dachverband.Externe.Adressverwalter/-in", "Dachverband.Externe.Externe/-r",

			"Dachverband.Spender.Spender/-in", "Dachverband.Spender.Spendenverwalter/-in",

			"Mitgliederorganisation.Administrator/-in",

			"Mitgliederorganisation.Vorstand.Präsidium", "Mitgliederorganisation.Vorstand.Finanzverantwortliche-/r", "Mitgliederorganisation.Vorstand.Mitglied",

			"Mitgliederorganisation.Geschäftsstelle.Geschäftsleiter/-in", "Mitgliederorganisation.Geschäftsstelle.Angestellte/-r:", "Mitgliederorganisation.Geschäftsstelle.Finanzverantwortliche/-r:",

			"Mitgliederorganisation.Gremium.Leitung", "Mitgliederorganisation.Gremium.Mitglied", "Mitgliederorganisation.Gremium.Aktive/-r Kursleiter/-in",

			"Mitgliederorganisation.Mitglieder.Adressverwalter/-in", "Mitgliederorganisation.Mitglieder.Mitglied",

			"Mitgliederorganisation.Externe,Adressverwalter/-in", "Mitgliederorganisation.Externe.Externe/-r",

			"Mitgliederorganisation.Spender.Spendenverwalter/-in", "Mitgliederorganisation.Spender.Spender/-in",

			"Ortsgruppe.Administrator/-in Cevi DB",

			"Jungschar.Abteilungsleiter/-in", "Jungschar.Coach", "Jungschar.Finanzverantwortliche/-r", "Jungschar.Adressverwalter/-in", "Jungschar.Aktuar/-in", "Jungschar.Busverwalter/-in:",
			"Jungschar.Freie/-r Mitarbeiter/-in", "Jungschar.Hausverantwortliche/-r", "Jungschar.Input", "Jungschar.Lädeliverantwortliche/-r", "Jungschar.Redaktor/-in",
			"Jungschar.Regionaltreffenvertreter/-in", "Jungschar.Webmaster/-in", "Jungschar.Werbung", "Jungschar.Materialverantwortliche/-r", "Jungschar.Kontakt Regionalzeitschrift",
			"Jungschar.Verantwortliche/-r PSA", "Jungschar.Jugendarbeiter/-in",

			"Jungschar.Externe.Verantwortliche/-r", "Jungschar.Externe.Jugendarbeiter/-in", "Jungschar.Externe.Externe/-r", "Jungschar.Externe.Gotte/Götti",

			"Jungschar.Fröschli.Fröschlihauptleiter/-in", "Jungschar.Fröschli.Fröschlileiter/-in", "Jungschar.Fröschli.Teilnehmer/-in",

			"Jungschar.Stufe.Stufenleiter/-in", "Jungschar.Stufe.Mini-Chef", "Jungschar.Stufe.Gruppenleiter/-in", "Jungschar.Stufe.Minigruppenleiter/-in", "Jungschar.Stufe.Helfer/-in",
			"Jungschar.Stufe.Teilnehmer/-in",

			"Jungschar.Team.Abteilungsleiter/-in", "Jungschar.Team.Coach", "Jungschar.Team.Finanzverantwortliche/-r", "Jungschar.Team.Adressverwalter/-in", "Jungschar.Team.Aktuar/-in",
			"Jungschar.Team.Busverwalter/-in", "Jungschar.Team.Freie/-r Mitarbeiter/-in", "Jungschar.Team.Hausverantwortliche/-r", "Jungschar.Team.Input", "Jungschar.Team.Lädeliverantwortliche/-r",
			"Jungschar.Team.Redaktor/-in", "Jungschar.Team.Regionaltreffenvertreter/-in", "Jungschar.Team.Webmaster/-in", "Jungschar.Team.Werbung", "Jungschar.Team.Materialverantwortliche/-r",
			"Jungschar.Team.Kontakt Regionalzeitschrift", "Jungschar.Team.Verantwortliche/-r PSA", "Jungschar.Team.Jugendarbeiter/-in", "Jungschar.Team.Leitung", "Jungschar.Team.Mitarbeiter/-in",

			"Jungschar.Spender.Spender/-in", "Jungschar.Spender.pendenverwalter/-in",

			"Verein.Adressverantwortlicher", "Verein.Mitglied", "Verein.Freie/-r Mitarbeiter/-in",

			"Verein.Vorstand.Präsident/-in", "Verein.Vorstand.Finanzverantwortliche/-r", "Verein.Vorstand.Aktuar/-in", "Verein.Vorstand.Mitglied",

			"Verein.Mitglieder.Leiter/-in", "Verein.Mitglieder.Mitglied",

			"Verein.Externe.Verantwortliche/-r", "Verein.Externe.Externe-/r",

			"Verein.Spender.Spender/-in", "Verein.Spender.pendenverwalter/-in",

			"Ten-Sing.Team/Gruppe.Adressverantwortliche/-r",

			"Ten-Sing.Externe.Verantwortliche/-r", "Ten-Sing.Externe.Externe/-r", "Ten-Sing.Externe.Jugendarbeiter/-in",

			"Ten-Sing.Spender.Spender/-in", "Ten-Sing.Spender.Spendenverwalter/-in",

			"Sport.Adressverantwortliche-/r", "Sport.Finanzverantwortliche/-r", "Sport.Hauptleitung", "Sport.Materialverantwortliche/-r", "Sport.Leiter/-in", "Sport.Mitglied",
			"Sport.Freie/-r Mitarbeiter/-in",

			"Sport.Team/Gruppe.Leiter", "Sport.Team/Gruppe.Mitglied",

			"Sport.ExterneVerantwortliche/-r", "Sport.ExterneExterne/-r",

			"Sport.Spender.Spender/-in", "Sport.Spender.Spendenverwalter/-in",

			"Arbeitsgebiet.Adressverantwortliche/-r", "Arbeitsgebiet.Finanzverantwortliche/-r", "Arbeitsgebiet.Hauptleitung", "Arbeitsgebiet.Materialverantwortliche/-r", "Arbeitsgebiet.Leiter/-in",
			"Arbeitsgebiet.Mitglied", "Arbeitsgebiet.Freie/-r Mitarbeiter/-in",

			"Arbeitsgebiet.Externe.Verantwortliche/-r", "Arbeitsgebiet.Externe.Externe/-r",

			"Arbeitsgebiet.Team/Gruppe.Leiter/-in", "Arbeitsgebiet.Team/Gruppe.Mitglied",

			"Arbeitsgebiet.Spender.Spender/-in", "Arbeitsgebiet.Spender.Spendenverwalter/-in",

			"Global.Hauptleiter/-in", "Global.Mitglied", "Global.Arrangeur", "Global.Adressverwalter/-in", "Global.Aktuar/-in", "Global.Finanzverantwortliche/-r", "Global.Freie/-r Mitarbeiter/-in",
			"Global.Input Leiter/-in", "Global.Redaktor/-in", "Global.Regionaltreffenvertreter/-in", "Global.Webmaster/-in", "Global.Werbeteam Leitende/-r", "Global.Dirigent/-in",
			"Global.Chorleiter/-in", "Global.Chorsänger/-in", "Global.Video Leiter/-in", "Global.Stagedesign Leiter/-in", "Global.Stagedesigner/-in", "Global.DJ",
			"Global.Verantwortliche/-r Lager Weekends", "Global.Jugendarbeiter/-in", "Global.Band Leiter/-in", "Global.Bandmitglied", "Global.Tanz Leiter/-in", "Global.Tänzer/-in",
			"Global.Technik Leiter/-in", "Global.Techniker/-in", "Global.Theater Leiter/-in", "Global.Schauspieler/-in" };
	/* @formatter:on */

	private List<String> roleOrder;

	public RoleComparator() {
		this.roleOrder = Arrays.asList(ROLE_ORDER);
	}

	@Override
	public int compare(Role o1, Role o2) {
		if (o1 == o2) {
			return 0;
		} else if (o1 == null && o2 != null) {
			return -1;
		} else if (o1 != null && o2 == null) {
			return 1;
		} else if (o1.getFullyQuallifiedRoleName() == null && o2.getFullyQuallifiedRoleName() == null) {
			return 0;
		} else if (o1.getFullyQuallifiedRoleName() == null && o2.getFullyQuallifiedRoleName() != null) {
			return -1;
		} else if (o1.getFullyQuallifiedRoleName() != null && o2.getFullyQuallifiedRoleName() == null) {
			return -1;
		} else {
			Integer roleIndex1 = roleOrder.indexOf(o1.getFullyQuallifiedRoleName());
			Integer roleIndex2 = roleOrder.indexOf(o2.getFullyQuallifiedRoleName());
			return roleIndex1.compareTo(roleIndex2);
		}
	}

}
