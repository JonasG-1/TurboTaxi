package Server;

public class Verbindung {

    private String zIP;
    private int zPort;
    private String zName;
    private Farbe zFarbe;

    public Verbindung(String zIP, int zPort) {
        this.zIP = zIP;
        this.zPort = zPort;
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

    public void setzeName(String zName) {
        this.zName = zName;
    }

    public Farbe gibFarbe() {
        return zFarbe;
    }

    public void setzeFarbe(Farbe zFarbe) {
        this.zFarbe = zFarbe;
    }
}
