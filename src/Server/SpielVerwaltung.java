package Server;

public class SpielVerwaltung {

    Controller kenntController;
    private int zRundenZahl;
    private int zZeitInSekunden;
    private boolean zGestartet;
    private boolean zBeendet;

    public SpielVerwaltung(Controller pController) {
        this.kenntController = pController;
        zRundenZahl = 0;
        zZeitInSekunden = 0;
        zGestartet = false;
        zBeendet = false;
    }

    public boolean ueberpruefeWeg(String pProtokoll) {
        return true;
    }

    public int gibZeitInSekunden() {
        return zZeitInSekunden;
    }

    public int gibRundenZahl() {
        return zRundenZahl;
    }

    public boolean istGestartet() {
        return zGestartet;
    }

    public boolean istBeendet() {
        return zBeendet;
    }

    public void starte() {
        zRundenZahl++;
        if (!zBeendet) {
            zGestartet = true;
        }
        kenntController.sendeSpielerliste();
    }

    public void beende() {
        zBeendet = true;
        zGestartet = false;
    }
}
