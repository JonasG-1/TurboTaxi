package Server;

import linearestrukturen.List;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DAO {

    private final List<Rangeinheit> zRundenrangliste;
    private final List<Rangeinheit> zGesamtrangliste;
    private final String zDateiname;

    public DAO(String pDateiname) {
        zRundenrangliste = new List<>();
        zDateiname = pDateiname;
        File lDatei = new File(zDateiname);
        if (lDatei.exists() && !lDatei.isDirectory()) {
            zGesamtrangliste = ladeGesamtliste();
        } else {
            zGesamtrangliste = new List<>();
        }
    }

    public void loescheRundenrangliste() {
        zRundenrangliste.toFirst();
        while (zRundenrangliste.hasAccess()) {
           zRundenrangliste.remove();
        }
    }

    public int gibRundenZeit(Verbindung pVerbindung) {
        return sucheRangeinheit(pVerbindung).gibZeit();
    }

    public void speichereRundenZeit(Verbindung pVerbindung, int pZeit) {
        Rangeinheit lRangeinheit = sucheRangeinheit(pVerbindung);
        lRangeinheit.setzeZeit(pZeit);
    }

    public int gibGesamtZeit(Verbindung pVerbindung) {
        return sucheRangeinheitGesamt(pVerbindung).gibZeit();
    }

    public void speichereGesamtZeit(Verbindung pVerbindung, int pZeit) {
        Rangeinheit lRangeinheit = sucheRangeinheitGesamt(pVerbindung);
        lRangeinheit.setzeZeit(pZeit);
        speichereGesamtliste();
    }

    public int gibRundenPunkte(Verbindung pVerbindung) {
        return sucheRangeinheit(pVerbindung).gibPunkte();
    }

    public void speichereRundenPunkte(Verbindung pVerbindung, int pPunkte) {
        Rangeinheit lRangeinheit = sucheRangeinheit(pVerbindung);
        lRangeinheit.setzePunkte(pPunkte);
    }

    public int gibGesamtPunkte(Verbindung pVerbindung) {
        return sucheRangeinheitGesamt(pVerbindung).gibPunkte();
    }

    public void speichereGesamtPunkte(Verbindung pVerbindung, int pPunkte) {
        Rangeinheit lRangeinheit = sucheRangeinheitGesamt(pVerbindung);
        lRangeinheit.setzePunkte(pPunkte);
        speichereGesamtliste();
    }


    private Rangeinheit sucheRangeinheit(Verbindung pVerbindung) {
        return sucheRangeinheit(pVerbindung, zRundenrangliste);
    }

    private Rangeinheit sucheRangeinheitGesamt(Verbindung pVerbindung) {
        return sucheRangeinheit(pVerbindung, zGesamtrangliste);
    }

    private Rangeinheit sucheRangeinheit(Verbindung pVerbindung, List<Rangeinheit> pSuchliste) {
        pSuchliste.toFirst();
        Rangeinheit lRangeinheit = null;
        while (pSuchliste.hasAccess()) {
            if (pSuchliste.getContent().gibVerbindung().equals(pVerbindung)) {
                lRangeinheit = pSuchliste.getContent();
            }
            pSuchliste.next();
        }
        if (lRangeinheit == null) {
            lRangeinheit = new Rangeinheit(pVerbindung);
            pSuchliste.append(lRangeinheit);
        }
        return lRangeinheit;
    }

    public void speichereGesamtliste() {
        try {
            ObjectOutputStream lDatei = new ObjectOutputStream(Files.newOutputStream(Paths.get(zDateiname)));
            lDatei.writeObject(new Speicherobjekt(zGesamtrangliste));
            lDatei.close();
        } catch (IOException pFehler) {
            pFehler.printStackTrace();
        }
    }

    public List<Rangeinheit> ladeGesamtliste() {
        List<Rangeinheit> lInhalt = null;
        try {
            ObjectInputStream lDatei = new ObjectInputStream(Files.newInputStream(Paths.get(zDateiname)));
            lInhalt = ((Speicherobjekt) lDatei.readObject()).gibInhalt();
            lDatei.close();
        } catch (ClassNotFoundException | IOException pFehler) {
            pFehler.printStackTrace();
        }
        return lInhalt;
    }

}
