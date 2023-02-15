package Server;

public class Controller {

    private Server hatServer;

    public static void main(String[] args) {
        new Controller();
    }

    public Controller(){
        hatServer = new Server(1234, this);
    }

    public void leseProtokollAus() {

    }
}
