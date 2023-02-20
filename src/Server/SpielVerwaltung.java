package Server;

public class SpielVerwaltung {

    Controller kenntController;


    public SpielVerwaltung(Controller pController) {
        this.kenntController = pController;
    }

    public boolean ueberpruefeWeg(String pProtokoll) {
        return true;
    }

    public int gibZeitInSekunden() {
        return 0;
    }

    public int gibRundenZahl() {
        return 0;
    }

    public boolean istGestartet() {
        return false;
    }

    public boolean istBeendet() {
        return false;
    }

    public void starte() {

    }
}
