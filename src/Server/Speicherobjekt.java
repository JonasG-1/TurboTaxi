package Server;

import linearestrukturen.List;

import java.io.Serializable;

public class Speicherobjekt implements Serializable {
    private final List<Rangeinheit> zInhalt;

    public Speicherobjekt(List<Rangeinheit> pInhalt) {
        zInhalt = pInhalt;
    }

    public List<Rangeinheit> gibInhalt() {
        return zInhalt;
    }
}
