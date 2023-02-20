package Constants;

public interface Protokoll {

    String OK = "+OK";
    String ERR = "-ERR";
    String LEER = "";

    interface Client {
        String NAME = "NAME";
        String BEREIT = "BEREIT";
        String FEHLER = "FEHLER";
        String FERTIG = "FERTIG";
        String TRENNEN = "TRENNEN";
    }

    interface Server {
        String WARTEN = "WARTEN";
        String SPIELERLISTE = "SPIELERLISTE";
        String COUNTDOWN = "COUNTDOWN";
        String START = "START";
        String ENDE = "ENDE";
        String RUNDENENDE = "RUNDENENDE";
        String RUNDENLISTE = "RUNDENRANGLISTE";
        String MATCHLISTE = "MATCHRANGLISTE";
        String VERBINDUNG = "VERBINDUNG";
    }

    interface Debug {
        String DEBUG = "[DEBUG]";
        String INFO = "[INFO]";
        String WARNUNG = "[WARN]";
        String FEHLER = "[ERROR]";
        String STRUKTUR = "%s %s:%s - %s%s";

    }

}
