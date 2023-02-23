package Server;

import java.io.Serializable;

public class Rangeinheit implements Serializable {

    private int zPunkte;
    private int zZeitInSekunden;
    private final Verbindung zVerbindung;

    public Rangeinheit(Verbindung pVerbindung) {
        this.zVerbindung = pVerbindung;
        this.zPunkte = 0;
        this.zZeitInSekunden = -1;
    }

    public int gibPunkte() {
        return zPunkte;
    }

    public int gibZeit() {
        return zZeitInSekunden;
    }

    public Verbindung gibVerbindung() {
        return zVerbindung;
    }

    public void setzePunkte(int pPunkte) {
        zPunkte = pPunkte;
    }

    public void setzeZeit(int pZeit) {
        zZeitInSekunden = pZeit;
    }
}
