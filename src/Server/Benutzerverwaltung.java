package Server;

import linearestrukturen.List;

public class Benutzerverwaltung {

    private List<Verbindung> hatVerbindungen;
    private int hatAnzahlVerbindungen;

    public Benutzerverwaltung() {
        hatVerbindungen = new List<Verbindung>();
        hatAnzahlVerbindungen = 0;
    }

    public void fuegeVerbindungHinzu(Verbindung pVerbindung) {
        hatVerbindungen.append(pVerbindung);
        hatAnzahlVerbindungen++;
    }

    public void entferneVerbindung(Verbindung pVerbindung) {
        hatVerbindungen.toFirst();
        boolean gefunden = false;
        while(hatVerbindungen.hasAccess() && !gefunden) {
            if (hatVerbindungen.getContent().equals(pVerbindung)) {
                hatVerbindungen.remove();
                hatAnzahlVerbindungen--;
                gefunden = true;
            }
            hatVerbindungen.next();
        }
    }

    public int gibAnzahlVerbindungen() {
        return hatAnzahlVerbindungen;
    }

    public Verbindung gibBenutzer(String pName) {
        hatVerbindungen.toFirst();
        Verbindung verbindung = null;
        while (hatVerbindungen.hasAccess()) {
            if (hatVerbindungen.getContent().gibName().equals(pName)) {
                verbindung = hatVerbindungen.getContent();
            }
            hatVerbindungen.next();
        }
        return verbindung;
    }

    public Verbindung gibBenutzer(String pIP, int pPort) {
        hatVerbindungen.toFirst();
        Verbindung verbindung = null;
        while (hatVerbindungen.hasAccess()) {
            if (hatVerbindungen.getContent().equals(new Verbindung(pIP, pPort))) {
                verbindung = hatVerbindungen.getContent();
            }
            hatVerbindungen.next();
        }
        return verbindung;
    }
}