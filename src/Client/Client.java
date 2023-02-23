package Client;

public class Client extends netzwerk.Client {

    public Client(String pServerIP, int pServerPort) {
        super(pServerIP, pServerPort, false);
    }

    @Override
    public void processMessage(String s) {
        System.out.println(s);
    }
}
