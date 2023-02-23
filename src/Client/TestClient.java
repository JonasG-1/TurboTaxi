package Client;

import java.util.Scanner;

public class TestClient {

    public static void main(String[] args) {
        Client client = new Client("localhost", 1234);
        String lNachricht = "";
        Scanner lScanner = new Scanner(System.in);
        while (!lNachricht.equals("exit")) {
            client.send(lNachricht);
            lNachricht = lScanner.nextLine();
        }
    }
}
