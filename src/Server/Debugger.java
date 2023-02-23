package Server;

import Constants.Protokoll;

public class Debugger {

    private boolean zDebug;
    private boolean zInfo;
    private boolean zWarnung;
    private boolean zFehler;

    public Debugger(boolean pDebug, boolean pInfo, boolean pFehler) {
        System.out.println("Debugger wurde gestartet");
        this.zDebug = pDebug;
        this.zInfo = pInfo;
        this.zWarnung = pFehler;
        this.zFehler = pFehler;
    }

    public void setzeStatus(boolean pDebug, boolean pInfo, boolean pWarnung, boolean pFehler) {
        this.zDebug = pDebug;
        this.zInfo = pInfo;
        this.zWarnung = pWarnung;
        this.zFehler = pFehler;
    }

    public void sende(String pNachricht) {
        if (this.zDebug) {
            System.out.println(Protokoll.Debug.DEBUG + " " + pNachricht);
        }
    }

    public void sendeInfo(String pDebug, String pIP, int pPort, String pName) {
        if (this.zInfo) {
            go(String.format(Protokoll.Debug.STRUKTUR, Protokoll.Debug.INFO, pIP, pPort, pDebug,
                    pName.equals(Protokoll.LEER) ? "" : "\n       --> Name: " + pName));
        }
    }

    public void sendeWarnung(String pDebug, String pIP, int pPort, String pName) {
        if (this.zWarnung) {
            go(String.format(Protokoll.Debug.STRUKTUR, Protokoll.Debug.WARNUNG, pIP, pPort, pDebug,
                    pName.equals(Protokoll.LEER) ? "" : "\n       --> Name: " + pName));
        }
    }

    public void sendeFehler(String pDebug, String pIP, int pPort, String pName) {
        if (this.zFehler) {
            go(String.format(Protokoll.Debug.STRUKTUR, Protokoll.Debug.FEHLER, pIP, pPort, pDebug,
                    pName.equals(Protokoll.LEER) ? "" : "\n       --> Name: " + pName));
        }
    }

    private void go(String pNachricht) {
        System.out.println(pNachricht);
    }

}
