package Server;

import linearestrukturen.List;

public class Benutzerverwaltung {

    private final List<Verbindung> hatVerbindungen;
    private int hatAnzahlVerbindungen;

    public Benutzerverwaltung() {
        hatVerbindungen = new List<>();
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
            String lName = hatVerbindungen.getContent().gibName();
            if (lName != null && lName.equals(pName)) {
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
            Verbindung lVerbindung = hatVerbindungen.getContent();
           if (!lVerbindung.istImSpiel() && lVerbindung.istAngemeldet() && lVerbindung.istBereit()) {
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

    public void setzeAlleImSpielWennBereit(boolean pImSpiel) {
        hatVerbindungen.toFirst();
        while (hatVerbindungen.hasAccess()) {
            if (hatVerbindungen.getContent().istBereit()) {
                hatVerbindungen.getContent().setzeImSpiel(pImSpiel);
            }
            hatVerbindungen.next();
        }
    }
}