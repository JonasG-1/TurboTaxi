package Server;

import Constants.Protokoll;

public class Controller {

    private Server hatServer;
    private Benutzerverwaltung hatBenutzerverwaltung;


    public static void main(String[] args) {
        new Controller();
    }

    public Controller(){
        hatServer = new Server(1234, this);
        hatBenutzerverwaltung = new Benutzerverwaltung();
    }

    public void neueVerbindung(String pIP, int pPort) {
        Verbindung verbindung = new Verbindung(pIP, pPort);
        hatBenutzerverwaltung.fuegeVerbindungHinzu(verbindung);

    }

    public void leseProtokollAus(String pNachricht, String pIP, int pPort) {

    }

    private String verarbeiteNachricht(String pNachricht, String pIP, int pPort) {
        String lAntwort = Protokoll.ERR + " Fehler bei der Verarbeitung der Nachricht.";
        String[] lNachrichtTeile = pNachricht.split(" ");
        switch (lNachrichtTeile[0]) {
            case Protokoll.Client.NAME:
                lAntwort = verarbeiteAnmeldung(lNachrichtTeile[1], pIP, pPort);
                break;
            case Protokoll.Client.BEREIT:
                lAntwort = verarbeiteBereit(lNachrichtTeile[1], pIP, pPort);
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
        Verbindung lVerbindung = hatBenutzerverwaltung.gibBenutzer(pIP, pPort);
        Verbindung lBenutzer = hatBenutzerverwaltung.gibBenutzer(pNachricht);
        if (lVerbindung != null) {
            if (lBenutzer != null) {
                lAntwort = Protokoll.ERR + pNachricht + " Der Benutzername ist bereits vergeben.";
            } else {
                lVerbindung.setzeName(pNachricht);
                lAntwort = Protokoll.OK + pNachricht + " Anmeldung erfolgreich.";
            }
        } else {
            lAntwort = Protokoll.ERR + pNachricht + " Bei Der Anmeldung ist etwas schiefgelaufen.";
        }
        return lAntwort;
    }

    private String baueArrayZusammen(String[] pArray, int pStartIndex) {
        String lString = "";
        for (int i = pStartIndex; i < pArray.length; i++) {
            lString += pArray[i] + " ";
        }
        return lString;
    }

    private String baueArrayZusammen(String[] pArray) {
        return baueArrayZusammen(pArray, 0);
    }
}
