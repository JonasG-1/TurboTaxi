package Server;

public class Server extends netzwerk.Server {

    private final Controller zController;

     public Server(int pPort, Controller pController, boolean pMitProtokoll) {
          super(pPort, pMitProtokoll);
          this.zController = pController;
     }
    @Override
    public void processNewConnection(String pIP, int pPort) {
         zController.fuegeVerbindungHinzu(pIP, pPort);
    }

    @Override
    public void processMessage(String pIP, int pPort, String pNachricht) {
        zController.leseProtokollAus(pIP, pPort, pNachricht);
    }

    @Override
    public void processClosingConnection(String pIP, int pPort) {
        zController.erzwingeVerbindungEntfernen(pIP, pPort);
    }
}
