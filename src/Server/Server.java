package Server;

public class Server extends netzwerk.Server {

    private Controller zController;

     public Server(int pPort, Controller pController) {
          super(pPort);
          this.zController = pController;
     }
    @Override
    public void processNewConnection(String pIP, int pPort) {

    }

    @Override
    public void processMessage(String pIP, int pPort, String pNachricht) {

    }

    @Override
    public void processClosingConnection(String pIP, int pPort) {

    }
}
