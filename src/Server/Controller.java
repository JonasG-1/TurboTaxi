package Server;

import Constants.Protokoll;
import linearestrukturen.List;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Controller {

    private final Server hatServer;
    private final Benutzerverwaltung hatBenutzerverwaltung;
    private final Debugger hatDebugger;
    private SpielVerwaltung hatSpielVerwaltung;
    private boolean zLaeuft;
    private int zCountdown;
    private int zSpielMaximaleNr;


    public static void main(String[] args) {
        new Controller();
    }

    public Controller(){
        hatServer = new Server(1234, this);
        hatBenutzerverwaltung = new Benutzerverwaltung();
        hatDebugger = new Debugger(false, false, true);
        hatSpielVerwaltung = new SpielVerwaltung(this);
        zLaeuft = true;
        zCountdown = 40;
        zSpielMaximaleNr = 4;
        ScheduledExecutorService lExecutor = Executors.newSingleThreadScheduledExecutor();
        lExecutor.scheduleAtFixedRate(() -> {
            while (zLaeuft) {
                pruefeWartende();
                pruefeSpiel();
                pruefeCountdown();
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }

    public void neueVerbindung(String pIP, int pPort) {
        Verbindung verbindung = new Verbindung(pIP, pPort);
        hatBenutzerverwaltung.fuegeVerbindungHinzu(verbindung);
        hatServer.send(pIP, pPort,
                gibAntwort(true, Protokoll.Server.VERBINDUNG, pIP + ":" + pPort, Protokoll.LEER));
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
            pNachricht = Protokoll.LEER + "(LEER)";
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
        if (lVerbindung != null && lVerbindung.istAngemeldet() && !hatSpielVerwaltung.istGestartet()) {
            lVerbindung.setzeBereit(true);
            lAntwort = gibAntwort(true, Protokoll.Client.BEREIT, lVerbindung.gibName(),
                    "Der Benutzer ist bereit.");
        } else {
            if (lVerbindung == null) {
                lAntwort = gibAntwortKeineVerbindung(Protokoll.Client.BEREIT, pIP + ":" + pPort);
            } else if (lVerbindung.istAngemeldet()) {
                lAntwort = gibAntwortNichtAngemeldet(Protokoll.Client.BEREIT, pIP + ":" + pPort);
            } else {
                lAntwort = gibAntwort(false, Protokoll.Client.BEREIT, lVerbindung.gibName(),
                        "Das Spiel wurde bereits gestartet.");
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

    private String verarbeiteFertig(String pNachricht, String pIP, int pPort) {
        String lAntwort;
        Verbindung lVerbindung = hatBenutzerverwaltung.gibBenutzer(pIP, pPort);
        if (lVerbindung != null && lVerbindung.istAngemeldet() && lVerbindung.istImSpiel()) {
            boolean lRichtig = hatSpielVerwaltung.ueberpruefeWeg(pNachricht);
            lAntwort = gibAntwort(lRichtig, Protokoll.Client.FERTIG, pNachricht,
                    lRichtig ? "Der Weg ist richtig." : "Der Weg ist falsch.");
            if (lRichtig) {
                verarbeiteRichtigenWeg(lVerbindung);
            }
        } else {
            if (lVerbindung == null) {
                lAntwort = gibAntwortKeineVerbindung(Protokoll.Client.FERTIG, pIP + ":" + pPort);
            } else if (!lVerbindung.istAngemeldet()) {
                lAntwort = gibAntwortNichtAngemeldet(Protokoll.Client.FERTIG, pIP + ":" + pPort);
            } else {
                lAntwort = gibAntwort(false, Protokoll.Client.FERTIG, pIP + ":" + pPort,
                        "Der Benutzer nimmt nicht am Spiel teil.");
            }
        }
        return lAntwort;
    }

    private void verarbeiteRichtigenWeg(Verbindung pVerbindung) {
        int lRunde = hatSpielVerwaltung.gibRundenZahl();
        int lZeit = hatSpielVerwaltung.gibZeitInSekunden();

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

    private void pruefeWartende() {
        List<Verbindung> lWartende = hatBenutzerverwaltung.gibWartende();
        lWartende.toFirst();
        while (lWartende.hasAccess() && hatSpielVerwaltung.istGestartet()) {
            Verbindung lVerbindung = lWartende.getContent();
            hatServer.send(lVerbindung.gibIP(), lVerbindung.gibPort(), Protokoll.Server.WARTEN +
                    " Du kannst dem Spiel gerade nicht beitreten.");
            lWartende.next();
        }
    }

    private void pruefeSpiel() {
        if (hatSpielVerwaltung.istBeendet()) {
            if (zSpielMaximaleNr >= hatSpielVerwaltung.gibRundenZahl()) {
                hatSpielVerwaltung.starte();
            } else {
                hatSpielVerwaltung = new SpielVerwaltung(this);
                hatBenutzerverwaltung.setzeAlleBereit(false);
                hatBenutzerverwaltung.setzeAlleImSpiel(false);
                hatServer.sendToAll(
                        "ANMERKUNG Alle Spieler, die mitspielen wollen, müssen sich jetzt auf BEREIT stellen."
                );
                zCountdown = 40;
                hatDebugger.sendeInfo("Das Spiel ist beendet.", "Konsole", 0, Protokoll.LEER);
            }
        }
    }

    private void pruefeCountdown() {
        if (!hatSpielVerwaltung.istGestartet()) {
            if (zCountdown > 0) {
                hatServer.sendToAll(Protokoll.Server.COUNTDOWN + " " + zCountdown);
                zCountdown--;
            } else {
                hatSpielVerwaltung.starte();
                hatBenutzerverwaltung.setzeAlleImSpielWennBereit(true);
            }
        }
    }

    private void sendeRundenliste() {
        List<Verbindung> lBenutzer = hatBenutzerverwaltung.gibSpielende();
        lBenutzer.toFirst();
        List<String> lRundenliste = new List<>();
        while (lBenutzer.hasAccess()) {
            Verbindung lVerbindung = lBenutzer.getContent();
            if (lVerbindung != null && lVerbindung.istAngemeldet() && lVerbindung.istImSpiel()) {
                // TODO DAO
                lRundenliste.append(String.format("(%s,%s,%s;)", lVerbindung.gibName(), lVerbindung.gibAktuelleZeit(),
                        lVerbindung.gibAktuellePunkte()));
            }
            lBenutzer.next();
        }
        hatServer.sendToAll(Protokoll.Server.RUNDENLISTE + " " + lRundenliste);
    }
}
