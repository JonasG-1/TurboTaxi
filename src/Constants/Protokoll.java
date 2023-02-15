package Constants;

public interface Protokoll {
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
    }



}
