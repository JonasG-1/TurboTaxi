package Server;

public class Karte {

    private int zID;
    private int zRotation;

    public Karte(int pID) {
        this.zID = pID;
        this.zRotation = 0;
    }

    public Karte(int pID, int pRotation) {
        this.zID = pID;
        this.zRotation = pRotation;
    }

    public int gibID() {
        return zID;
    }

    public int gibRotation() {
        return zRotation;
    }
}
