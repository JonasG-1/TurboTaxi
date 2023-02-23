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
    private final DAO hatDAO;
    private SpielVerwaltung hatSpielVerwaltung;
    private boolean zLaeuft;
    private int zCountdown;
    private final int zCountdownZuvor;
    private final int zSpielMaximaleNr;


    public static void main(String[] args) {
        new Controller();
    }

    public Controller(){
        hatServer = new Server(1234, this, false);
        hatBenutzerverwaltung = new Benutzerverwaltung();
        hatDebugger = new Debugger(true, true, true);
        hatSpielVerwaltung = new SpielVerwaltung(this);
        hatDAO = new DAO("matchrangliste.ser");
        zLaeuft = true;
        zCountdownZuvor = 40;
        zCountdown = zCountdownZuvor;
        zSpielMaximaleNr = 4;
        ScheduledExecutorService lExecutor = Executors.newSingleThreadScheduledExecutor();
        lExecutor.scheduleAtFixedRate(() -> {
            pruefeWartende();
            pruefeSpiel();
            pruefeCountdown();
            if (!zLaeuft) {
                lExecutor.shutdown();
            }
        }, 0, 1, TimeUnit.SECONDS);
        new Console(this);
    }

    public void fuegeVerbindungHinzu(String pIP, int pPort) {
        Verbindung verbindung = new Verbindung(pIP, pPort);
        hatBenutzerverwaltung.fuegeVerbindungHinzu(verbindung);
        hatServer.send(pIP, pPort,
                gibAntwort(true, Protokoll.Server.VERBINDUNG, pIP + ":" + pPort, Protokoll.LEER));
        hatDebugger.sendeInfo("Neue Verbindung.", pIP, pPort, Protokoll.LEER);
    }

    private String verarbeiteTrennen(String pIP, int pPort) {
        String lAntwort = gibAntwort(true, Protokoll.Client.TRENNEN, pIP + ":" + pPort, "Getrennt.");
        Verbindung lVerbindung = hatBenutzerverwaltung.gibBenutzer(pIP, pPort);
        if (lVerbindung != null) {
            hatBenutzerverwaltung.entferneVerbindung(lVerbindung);
            hatDebugger.sendeInfo("Verbindung getrennt.", pIP, pPort,
                    lVerbindung.gibName() == null ? Protokoll.LEER : lVerbindung.gibName());
        } else {
            lAntwort = gibAntwort(false, Protokoll.Client.TRENNEN, pIP + ":" + pPort,
                    "Die Verbindung wurde bereits entfernt.");
        }
        return lAntwort;
    }

    public void erzwingeVerbindungEntfernen(String pIP, int pPort) {
        try {
            hatServer.send(pIP, pPort, verarbeiteTrennen(pIP, pPort));
        } catch (Exception e) {
            hatDebugger.sendeInfo("Verbindung wurde ohne zu trennen abgebrochen. Stacktrace folgt.", pIP, pPort,
                    Protokoll.LEER);
            hatDebugger.sendeInfo(e.getMessage(), pIP, pPort, Protokoll.LEER);
        }
    }

    public void leseProtokollAus(String pIP, int pPort, String pNachricht) {
        hatDebugger.sendeInfo("Nachricht empfangen: " + pNachricht, pIP, pPort, Protokoll.LEER);
        String lAntwort = verarbeiteNachricht(pNachricht, pIP, pPort);
        //Verbindung lVerbindung = hatBenutzerverwaltung.gibBenutzer(pIP, pPort);
        if (lAntwort != null && !lAntwort.equals(Protokoll.LEER)) {
            hatServer.send(pIP, pPort, lAntwort);
            hatDebugger.sendeInfo("Antwort gesendet: " + lAntwort, pIP, pPort, Protokoll.LEER);
        }
    }

    private String verarbeiteNachricht(String pNachricht, String pIP, int pPort) {
        String lAntwort;
        String[] lNachrichtTeile = pNachricht.split(" ");
        switch (lNachrichtTeile[0]) {
            case Protokoll.Client.NAME:
                lAntwort = verarbeiteAnmeldung(baueArray(lNachrichtTeile, 1), pIP, pPort);
                break;
            case Protokoll.Client.BEREIT:
                lAntwort = verarbeiteBereit(pIP, pPort);
                break;
            case Protokoll.Client.FEHLER:
                lAntwort = verarbeiteFehler(baueArray(lNachrichtTeile, 1), pIP, pPort);
                break;
            case Protokoll.Client.FERTIG:
                lAntwort = verarbeiteFertig(baueArray(lNachrichtTeile, 1), pIP, pPort);
                break;
            case Protokoll.Client.TRENNEN:
                lAntwort = verarbeiteTrennen(pIP, pPort);
                break;
            default:
                lAntwort = gibAntwort(false, Protokoll.Client.FEHLER, pIP + ":" + pPort,
                     "Befehl nicht gefunden.");
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
            } else if (!lVerbindung.istAngemeldet()) {
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
                pIP, pPort, lVerbindung != null && lVerbindung.istAngemeldet() ? lVerbindung.gibName() : Protokoll.LEER);
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
        int lZeit = hatSpielVerwaltung.gibZeitInSekunden();
        int lRundenZeit = hatDAO.gibRundenZeit(pVerbindung);
        int lGesamtZeit = hatDAO.gibGesamtZeit(pVerbindung);
        int lRundenPunkte = hatDAO.gibRundenPunkte(pVerbindung);
        int lGesamtPunkte = hatDAO.gibGesamtPunkte(pVerbindung);
        int lRundenPunkteNeu = lRundenPunkte + 1;
        int lGesamtPunkteNeu = lGesamtPunkte + 1;
        if (lRundenZeit == -1 || lZeit < lRundenZeit) {
            hatDAO.speichereRundenZeit(pVerbindung, lZeit);
        }
        if (lGesamtZeit == -1 || lZeit < lGesamtZeit) {
           hatDAO.speichereGesamtZeit(pVerbindung, lZeit);
        }
        hatDAO.speichereRundenPunkte(pVerbindung, lRundenPunkteNeu);
        hatDAO.speichereGesamtPunkte(pVerbindung, lGesamtPunkteNeu);
    }

    private String gibAntwortNichtAngemeldet(String pBefehl, String pInhalt) {
        return gibAntwort(false, pBefehl, pInhalt, "Der Benutzer ist nicht angemeldet.");
    }

    private String gibAntwortKeineVerbindung(String pBefehl, String pInhalt) {
        return gibAntwort(false, pBefehl, pInhalt, "Der Benutzer scheint nicht (mehr) zu existieren. " +
                "Ein neuer Verbindungsaufbau könnte das Problem beheben.");
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
        while (lWartende.hasAccess()) {
            Verbindung lVerbindung = lWartende.getContent();
            if (zCountdown == zCountdownZuvor) {
                hatServer.send(lVerbindung.gibIP(), lVerbindung.gibPort(), Protokoll.Server.WARTEN +
                        " Du kannst dem Spiel gerade nicht beitreten.");
            } else {
                hatServer.send(lVerbindung.gibIP(), lVerbindung.gibPort(), Protokoll.Server.WARTEN);
            }
            lWartende.next();
        }
    }

    private void pruefeSpiel() {
        int lSpielende = hatBenutzerverwaltung.gibSpielende().length();
        if (hatSpielVerwaltung.istBeendet() || (hatSpielVerwaltung.istGestartet() && lSpielende < 2)) {
            sendeListen();
            if (lSpielende >= 2 && zSpielMaximaleNr >= hatSpielVerwaltung.gibRundenZahl()) {
                hatDebugger.sendeInfo("Die nächste Runde beginnt.", "Konsole", 0, Protokoll.LEER);
                hatSpielVerwaltung.starte();
            } else {
                hatDebugger.sendeInfo("Das Spiel wurde beendet.", "Konsole", 0, Protokoll.LEER);
                hatSpielVerwaltung = new SpielVerwaltung(this);
                hatBenutzerverwaltung.setzeAlleBereit(false);
                hatBenutzerverwaltung.setzeAlleImSpiel(false);
                hatServer.sendToAll(
                        "ANMERKUNG Alle Spieler, die mitspielen wollen, müssen sich jetzt auf BEREIT stellen."
                );
                zCountdown = zCountdownZuvor;
                hatDAO.loescheRundenrangliste();
            }
        }
    }

    private void pruefeCountdown() {
        int lWartende = hatBenutzerverwaltung.gibWartende().length();
        if (!hatSpielVerwaltung.istGestartet() && lWartende >= 2) {
            if (zCountdown > 0) {
                hatServer.sendToAll(Protokoll.Server.COUNTDOWN + " " + zCountdown);
                if (zCountdown % 10 == 0 || zCountdown <= 5) {
                    hatDebugger.sendeInfo("Countdown: " + zCountdown, "Konsole", 0, Protokoll.LEER);
                }
                zCountdown--;
            } else {
                hatSpielVerwaltung.starte();
                hatBenutzerverwaltung.setzeAlleImSpielWennBereit(true);
                hatDebugger.sendeInfo("Das Spiel beginnt.", "Konsole", 0, Protokoll.LEER);
            }
        } else {
            zCountdown = zCountdownZuvor;
        }
    }

    private void sendeListen() {
        sendeRundenliste();
        sendeGesamtliste();
    }

    private void sendeRundenliste() {
        List<Verbindung> lBenutzer = hatBenutzerverwaltung.gibSpielende();
        lBenutzer.toFirst();
        StringBuilder lRundenliste = new StringBuilder();
        while (lBenutzer.hasAccess()) {
            Verbindung lVerbindung = lBenutzer.getContent();
            if (lVerbindung != null && lVerbindung.istAngemeldet() && lVerbindung.istImSpiel()) {
                lRundenliste.append(String.format(
                        "(%s,%s,%s;)", lVerbindung.gibName(),
                        hatDAO.gibRundenZeit(lVerbindung),
                        hatDAO.gibRundenPunkte(lVerbindung)
                        ));
            }
            lBenutzer.next();
        }
        hatServer.sendToAll(Protokoll.Server.RUNDENLISTE + " " + lRundenliste);
    }

    private void sendeGesamtliste() {
        List<Verbindung> lBenutzer = hatBenutzerverwaltung.gibSpielende();
        lBenutzer.toFirst();
        StringBuilder lGesamtliste = new StringBuilder();
        while (lBenutzer.hasAccess()) {
            Verbindung lVerbindung = lBenutzer.getContent();
            if (lVerbindung != null && lVerbindung.istAngemeldet() && lVerbindung.istImSpiel()) {
                lGesamtliste.append(String.format(
                        "(%s,%s,%s;)", lVerbindung.gibName(),
                        hatDAO.gibGesamtZeit(lVerbindung),
                        hatDAO.gibGesamtPunkte(lVerbindung)
                        ));
            }
            lBenutzer.next();
        }
        hatServer.sendToAll(Protokoll.Server.MATCHLISTE + " " + lGesamtliste);
    }

    public String gibStatistikAus() {
        StringBuilder lStatistik = new StringBuilder();
        List<Verbindung> lBenutzer = hatBenutzerverwaltung.gibSpielende();
        List<Verbindung> lBenutzer2 = hatBenutzerverwaltung.gibWartende();
        int lVerbindungen = hatBenutzerverwaltung.gibAnzahlVerbindungen();
        boolean lSpiel = hatSpielVerwaltung.istGestartet();
        boolean lSpielBeendet = hatSpielVerwaltung.istBeendet();
        int lRunde = hatSpielVerwaltung.gibRundenZahl();
        int lZeit = hatSpielVerwaltung.gibZeitInSekunden();
        lStatistik.append("Spielende: ").append(lBenutzer.length()).append(" Wartende: ").append(lBenutzer2.length())
                .append("\nVerbindungen: ").append(lVerbindungen).append(" Spiel: ").append(lSpiel)
                .append("\nSpielBeendet: ").append(lSpielBeendet).append(" Runde: ").append(lRunde)
                .append("\nZeit: ").append(lZeit).append(" Countdown: ").append(zCountdown)
                .append("\nLäuft: ").append(zLaeuft);
        return lStatistik.toString();
    }
}
