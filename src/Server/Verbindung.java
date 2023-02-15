package Server;

public class Verbindung {

    private String zIP;
    private int zPort;
    private String zName;
    private boolean zIstAngemeldet;
    private int zHighscore;

    public Verbindung(String pIP, int pPort) {
        this.zIP = pIP;
        this.zPort = pPort;
        this.zName = null;

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
