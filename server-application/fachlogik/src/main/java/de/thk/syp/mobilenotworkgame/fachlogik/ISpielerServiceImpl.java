package de.thk.syp.mobilenotworkgame.fachlogik;

import de.thk.syp.mobilenotworkgame.datenhaltungapi.ICRUDSpieler;
import de.thk.syp.mobilenotworkgame.fachlogikapi.ISpielerService;
import de.thk.syp.mobilenotworkgame.fachlogikapi.grenz.SpielerGrenz;
import de.thk.syp.mobilenotworkgame.mnwgdbmodel.impl.IDatabaseImpl;
import de.thk.syp.mobilenotworkgame.mnwgdbmodel.services.IDatabase;
import de.thk.syp.mobilenotworkgame.mnwgdbmodel.entities.Spieler;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.EntityManager;
import java.lang.module.FindException;
import java.util.*;

public class ISpielerServiceImpl implements ISpielerService {

    Logger logger= LoggerFactory.getLogger("de.thk.mobilenotworkgame.fachlogik.ISpielerServiceImpl");

    ICRUDSpieler icrudSpieler;

    private static EntityManager em;

    public ISpielerServiceImpl() {
        logger.info("Starte ISpielerServiceImpl");
        IDatabase db = new IDatabaseImpl();
        em = db.getEntityManager();
        if (em == null) {
            logger.info("EntityManager holen fehlgeschlagen");
            throw new EntityNotFoundException();
        }
        logger.info("IDatabase und em geholt.");

        // ICRUDSpieler Implementierung ermitteln
        Iterator<ICRUDSpieler> it = ServiceLoader.load(ICRUDSpieler.class).iterator();
        System.out.println("ermittle Impl");
        if (it.hasNext()) {
            icrudSpieler = it.next();
            icrudSpieler.setEntityManager(em);
            logger.info("Implementierung fuer ICRUDSpieler erfolgreichn ermittelt.");
        } else {
            logger.error("Keine Implementierung fuer ICRUDSpieler gefunden.");
            throw new FindException("Keine Implementierung fuer ICRUDSpieler gefunden.");
        }
    }

    /*
     * Die Methode ermittelt alle Spieler und gibt diese als Objekt zurück
     */
    public List<SpielerGrenz> getSpieler() {
        List<Spieler> spielerList = icrudSpieler.getAlleSpieler();
        if(spielerList == null)
            return new ArrayList<SpielerGrenz>();
        // Objekt in Grenzklassen Objekt kopieren
        List<SpielerGrenz> spielerGrenzList = new ArrayList<>();
        Spieler tmpSpieler;
        Iterator<Spieler> it = spielerList.iterator();
        while (it.hasNext()) {
            tmpSpieler = it.next();
            SpielerGrenz spGrenz = new SpielerGrenz();
            spGrenz.setSid(tmpSpieler.getSid());
            spGrenz.setBenutzername(tmpSpieler.getBenutzername());
            spGrenz.setEinwilligungagb(tmpSpieler.getEinwilligungagb());
            spGrenz.setBlockiert(tmpSpieler.isBlockiert());
            spGrenz.setGeraeteid(tmpSpieler.getGeraeteid());

            spielerGrenzList.add(spGrenz);
        }

        return spielerGrenzList;
    }

    /*
     * Die Methode ermittelt einen Spieler zu einer gegebenen Spieler-ID (sid)
     */
    public SpielerGrenz getSpielerById(int sid) {
        Spieler sp = icrudSpieler.getSpielerByID(sid);
        if(sp == null)
            return null;

        // Objekt in Grenzklassen Objekt kopieren
        SpielerGrenz spGrenz = new SpielerGrenz();
        spGrenz.setSid(sp.getSid());
        spGrenz.setBenutzername(sp.getBenutzername());
        spGrenz.setEinwilligungagb(sp.getEinwilligungagb());
        spGrenz.setBlockiert(sp.isBlockiert());
        spGrenz.setGeraeteid(sp.getGeraeteid());

        return spGrenz;
    }

    /*
     * Die Methode prüft, ob ein Spieler mit entsprechender GeraeteID existiert und gibt die sid zurück
     */
    public int spielerLogin(String geraeteId) {
        Spieler spieler = icrudSpieler.getSpielerByGeraeteId(geraeteId);

        // wenn kein matchender Spieler gefunden wurde
        if (spieler == null) return -2;
        // wenn der Spieler blockiert ist
        if (spieler.isBlockiert()) return -1;

        return spieler.getSid();
    }

    /*
     * Die Methode gibt für eine gegebene Spieler-ID (sid) den Benutzernamen zurück
     */
    public String getSpielerBenutzernameById(int sid) {
        Spieler sp;

        sp = icrudSpieler.getSpielerByID(sid);
        if (sp != null) {
            return sp.getBenutzername();
        } else {
            return "";
        }
    }


    /*
     * Die Methode registriert einen Spieler
     */
    public boolean spielerRegistrieren(String geraeteId, boolean einwilligungAGB) {
        // Parameter für die Generierung des Benutzernamens
        final String[] ADJECTIVES = {"Red", "Blue", "Green", "Yellow", "Crazy", "Lazy", "Funny", "Smart", "Sleeping"};
        final String[] NOUNS = {"Cat", "Meatball", "Hairdryer", "Moon", "Sun", "Window", "Frog", "Tree", "Turtle", "Train"};
        final Set<Integer> BLACKLISTED_NUMBERS = Set.of(18, 88, 420, 1312);

        // Pruefen, ob es bereits einen Spieler mit der GeraeteID gibt
        boolean alreadyExists = false;
        if (icrudSpieler.getSpielerByGeraeteId(geraeteId) != null) alreadyExists = true;

        // GeraeteID bereits registriert oder AGB nicht zugestimmt => kein Erfolg
        if (alreadyExists || !einwilligungAGB) return false;

        // neues Spieler Objekt erstellen
        Spieler sp = new Spieler();
        String benutzername;

        // Daten des neuen Spieler setzen
        sp.setGeraeteid(geraeteId);
        sp.setEinwilligungagb(einwilligungAGB);

        // Zufälligen Benutzernamen generieren, der noch nicht existiert
        do {
            Random random = new Random();
            String adjective = ADJECTIVES[random.nextInt(ADJECTIVES.length)];
            String noun = NOUNS[random.nextInt(NOUNS.length)];
            int number;
            do {
                number = random.nextInt(10000); // von 0 bis 9999
            } while (BLACKLISTED_NUMBERS.contains(number));

            benutzername = adjective + noun + number;

            // Pruefen, ob der Name bereits exisitiert
            alreadyExists = false;
            Spieler tmpSpieler;
            List <Spieler> spielerList = icrudSpieler.getAlleSpieler();
            if (spielerList.isEmpty()) return false;

            Iterator<Spieler> spielerIterator = spielerList.iterator();
            while (spielerIterator.hasNext()) {
                tmpSpieler = spielerIterator.next();

                if (tmpSpieler.getBenutzername() == benutzername) {
                    alreadyExists = true;
                }
            }
        } while (alreadyExists);

        sp.setBenutzername(benutzername);

        em.getTransaction().begin();
        boolean success = icrudSpieler.insertSpieler(sp);
        if (success) {
            em.getTransaction().commit();
        } else {
            em.getTransaction().rollback();
        }

        return success;
    }

    /*
     * Die Methode setzt einen Spieler auf blockiert
     */
    public boolean spielerBlockieren(int sid) {
        Spieler toEdit = icrudSpieler.getSpielerByID(sid);
        if(toEdit == null)
            return false;

        toEdit.setBlockiert(true);

        em.getTransaction().begin();
        if(icrudSpieler.editSpieler(toEdit)){
            em.getTransaction().commit();
            return true;
        } else {
            em.getTransaction().rollback();
            return false;
        }

    }

    /*
     * Die Methode schaltet einen blockierten Spieler wieder frei
     */
    public boolean spielerFreischalten(int sid) {
        Spieler toEdit = icrudSpieler.getSpielerByID(sid);
        if(toEdit == null)
            return false;
        toEdit.setBlockiert(false);

        em.getTransaction().begin();
        if(icrudSpieler.editSpieler(toEdit)){
            em.getTransaction().commit();
            return true;
        } else {
            em.getTransaction().rollback();
            return false;
        }
    }

    /*
     * Die Methode ändert den Benutzernamen eines Spielers
     */
    public boolean spielerBenutzernameAendern(int sid, String benutzername) {
        Spieler toEdit = icrudSpieler.getSpielerByID(sid);
        if(toEdit == null){
            logger.info("spielerBenutzernameAendern: Spieler nach ID nicht gefunden");
            return false;
        }

        List<Spieler> allSpieler = icrudSpieler.getAlleSpieler();
        if(allSpieler.isEmpty()) {
            logger.info("spielerBenutzernameAendern: alle Spieler holen fehlgeschlagen");
            return false;
        }
        Iterator<Spieler> it = allSpieler.iterator();
        while (it.hasNext()){
            if(it.next().getBenutzername().equals(benutzername)){
                logger.info("spielerBenutzernameAendern: Nutzername bereits belegt");
                return false;
            }
        }
        toEdit.setBenutzername(benutzername);
        em.getTransaction().begin();
        if(icrudSpieler.editSpieler(toEdit)){
            em.getTransaction().commit();
            logger.info("spielerBenutzernameAendern: commit erfolgreich");
            return true;
        } else {
            em.getTransaction().rollback();
            logger.info("spielerBenutzernameAendern: commit fehlgeschlagen");
            return false;
        }
    }
}