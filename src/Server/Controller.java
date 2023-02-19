package Server;

import Constants.Protokoll;

public class Controller {

    private final Server hatServer;
    private final Benutzerverwaltung hatBenutzerverwaltung;
    private final Debugger hatDebugger;


    public static void main(String[] args) {
        new Controller();
    }

    public Controller(){
        hatServer = new Server(1234, this);
        hatBenutzerverwaltung = new Benutzerverwaltung();
        hatDebugger = new Debugger(false, false, true);
    }

    public void neueVerbindung(String pIP, int pPort) {
        Verbindung verbindung = new Verbindung(pIP, pPort);
        hatBenutzerverwaltung.fuegeVerbindungHinzu(verbindung);
        hatDebugger.sendeInfo("Neue Verbindung.", pIP, pPort, Protokoll.LEER);
    }

    public void leseProtokollAus(String pNachricht, String pIP, int pPort) {
        String lNachricht = verarbeiteNachricht(pNachricht, pIP, pPort);
        hatDebugger.sendeInfo("Nachricht empfangen: " + pNachricht, pIP, pPort, Protokoll.LEER);
        if (lNachricht != null && !lNachricht.equals(Protokoll.LEER)) {
            hatServer.send(pIP, pPort, lNachricht);
        }
    }

    private String verarbeiteNachricht(String pNachricht, String pIP, int pPort) {
        String lAntwort = "";
        String[] lNachrichtTeile = pNachricht.split(" ");
        switch (lNachrichtTeile[0]) {
            case Protokoll.Client.NAME:
                lAntwort = verarbeiteAnmeldung(baueArray(lNachrichtTeile, 1), pIP, pPort);
                break;
            case Protokoll.Client.BEREIT:
                lAntwort = verarbeiteBereit(pIP, pPort);
                break;
            case Protokoll.Client.FEHLER:
                lAntwort = verarbeiteFehler(lNachrichtTeile[1], pIP, pPort);
                break;
            case Protokoll.Client.FERTIG:
                lAntwort = verarbeiteFertig(lNachrichtTeile[1], pIP, pPort);
                break;
            case Protokoll.Client.TRENNEN:
                lAntwort = verarbeiteTrennen(lNachrichtTeile[1], pIP, pPort);
                break;
        }
        return lAntwort;
    }

    private String verarbeiteAnmeldung(String pNachricht, String pIP, int pPort) {
        String lAntwort;
        if (pNachricht.equals(Protokoll.LEER)) {
            pNachricht = "LEER";
        }
        Verbindung lVerbindung = hatBenutzerverwaltung.gibBenutzer(pIP, pPort);
        Verbindung lBenutzer = hatBenutzerverwaltung.gibBenutzer(pNachricht);
        if (lVerbindung != null && lBenutzer == null) {
            lVerbindung.setzeName(pNachricht);
            lAntwort = gibAntwort(true, Protokoll.Client.NAME, pNachricht,
                    "Erfolgreich angemeldet.");
        } else {
            if (lVerbindung == null) {
                lAntwort = gibAntwortKeineVerbindung(Protokoll.Client.NAME, pIP + ":" + pPort);
            } else {
                lAntwort = gibAntwort(false, Protokoll.Client.NAME, pNachricht,
                     "Der Benutzername ist bereits vergeben.");
            }
        }
        return lAntwort;
    }

    private String verarbeiteBereit(String pIP, int pPort) {
        String lAntwort;
        Verbindung lVerbindung = hatBenutzerverwaltung.gibBenutzer(pIP, pPort);
        if (lVerbindung != null && lVerbindung.istAngemeldet()) {
            lVerbindung.setzeBereit(true);
            lAntwort = gibAntwort(true, Protokoll.Client.BEREIT, lVerbindung.gibName(),
                    "Der Benutzer ist bereit.");
        } else {
            if (lVerbindung == null) {
                lAntwort = gibAntwortKeineVerbindung(Protokoll.Client.BEREIT, pIP + ":" + pPort);
            } else {
                lAntwort = gibAntwortNichtAngemeldet(Protokoll.Client.BEREIT, pIP + ":" + pPort);
            }
        }
        return lAntwort;
    }

    private String verarbeiteFehler(String pNachricht, String pIP, int pPort) {
        Verbindung lVerbindung = hatBenutzerverwaltung.gibBenutzer(pIP, pPort);
        hatDebugger.sendeWarnung("Fehlermeldung erhalten: " + pNachricht,
                pIP, pPort, lVerbindung.istAngemeldet() ? lVerbindung.gibName() : Protokoll.LEER);
        return Protokoll.LEER;
    }

    private String gibAntwortNichtAngemeldet(String pBefehl, String pInhalt) {
        return gibAntwort(false, pBefehl, pInhalt, "Der Benutzer ist nicht angemeldet.");
    }

    private String gibAntwortKeineVerbindung(String pBefehl, String pInhalt) {
        return gibAntwort(false, pBefehl, pInhalt, "Bei Der Anmeldung ist etwas schiefgelaufen. " +
                "Der Benutzer scheint nicht zu existieren.");
    }

    private String gibAntwort(boolean pErfolg, String pBefehl, String pInhalt, String pAntwort) {
        return String.format("%s %s (%s) - %s", pErfolg ? Protokoll.OK : Protokoll.ERR, pBefehl, pInhalt, pAntwort);
    }

    private String baueArray(String[] pArray, int pStartIndex, String pTrenner) {
        StringBuilder lString = new StringBuilder();
        for (int i = pStartIndex; i < pArray.length; i++) {
            lString.append(pArray[i]).append(pTrenner);
        }
        return lString.toString();
    }

    private String baueArray(String[] pArray, int pStartIndex) {
        return baueArray(pArray, pStartIndex, " ");
    }

    private String baueArray(String[] pArray) {
        return baueArray(pArray, 0);
    }
}
