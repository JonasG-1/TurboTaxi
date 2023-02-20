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

    public List<Verbindung> gibWartende() {
        hatVerbindungen.toFirst();
        List<Verbindung> lWartende = new List<>();
        while (hatVerbindungen.hasAccess()) {
           if (!hatVerbindungen.getContent().istImSpiel()) {
               lWartende.append(hatVerbindungen.getContent());
           }
           hatVerbindungen.next();
        }
        return lWartende;
    }

    public List<Verbindung> gibSpielende() {
        hatVerbindungen.toFirst();
        List<Verbindung> lSpielende = new List<>();
        while (hatVerbindungen.hasAccess()) {
           if (hatVerbindungen.getContent().istImSpiel()) {
               lSpielende.append(hatVerbindungen.getContent());
           }
           hatVerbindungen.next();
        }
        return lSpielende;
    }

    public void setzeAlleBereit(boolean pBereit) {
        hatVerbindungen.toFirst();
        while (hatVerbindungen.hasAccess()) {
            hatVerbindungen.getContent().setzeBereit(pBereit);
            hatVerbindungen.next();
        }
    }

    public void setzeAlleImSpiel(boolean pImSpiel) {
        hatVerbindungen.toFirst();
        while (hatVerbindungen.hasAccess()) {
            hatVerbindungen.getContent().setzeImSpiel(pImSpiel);
            hatVerbindungen.next();
        }
    }
}