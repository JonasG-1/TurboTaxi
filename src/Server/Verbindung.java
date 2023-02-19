package Server;

public class Verbindung {

    private final String zIP;
    private final int zPort;
    private String zName;
    private boolean zIstAngemeldet;
    private boolean zBereit;
    private boolean zImSpiel;
    private int zHighscore;

    public Verbindung(String pIP, int pPort) {
        this.zIP = pIP;
        this.zPort = pPort;
        this.zName = null;
        this.zIstAngemeldet = false;
        this.zBereit = false;
        this.zImSpiel = false;
        this.zHighscore = 0;
    }

    public String gibIP() {
        return zIP;
    }

    public int gibPort() {
        return zPort;
    }

    public String gibName() {
        return zName;
    }

    public void setzeName(String pName) {
        this.zName = pName;
        zIstAngemeldet = true;
    }

    public boolean istAngemeldet() {
        return zIstAngemeldet;
    }

    public boolean istBereit() {
        return zBereit;
    }

    public void setzeBereit(boolean pBereit) {
        zBereit = pBereit;
    }

    public boolean istImSpiel() {
        return zImSpiel;
    }

    public void setzeImSpiel(boolean pImSpiel) {
        zImSpiel = pImSpiel;
    }

    public int gibHighscore() {
        return zHighscore;
    }

    public void setzeHighscore(int pHighscore) {
        zHighscore = pHighscore;
    }

    @Override
    public boolean equals(Object obj) {
        boolean gleich = false;
        if (obj instanceof Verbindung) {
           Verbindung verbindung = (Verbindung) obj;
           gleich = verbindung.gibIP().equals(zIP) && verbindung.gibPort() == zPort;
        }
        return gleich;
    }
}
