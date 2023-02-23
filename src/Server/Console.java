package Server;

import java.util.Scanner;

public class Console {


    public Console(Controller pController) {
        new Thread(() -> {
            Scanner lScanner = new Scanner(System.in);
            while (true) {
                String lEingabe = lScanner.nextLine();
                if (lEingabe.equals("stat")) {
                    System.out.println(pController.gibStatistikAus());
                }
            }
        }).start();
    }
}
