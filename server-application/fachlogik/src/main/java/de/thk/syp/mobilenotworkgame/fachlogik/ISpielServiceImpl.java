package de.thk.syp.mobilenotworkgame.fachlogik;

import de.thk.syp.mobilenotworkgame.datenhaltungapi.ICRUDPunktestandSpielperiode;
import de.thk.syp.mobilenotworkgame.datenhaltungapi.ICRUDSpielperiode;
import de.thk.syp.mobilenotworkgame.fachlogikapi.ISpielService;
import de.thk.syp.mobilenotworkgame.fachlogikapi.grenz.PunktestandspielperiodeGrenz;
import de.thk.syp.mobilenotworkgame.fachlogikapi.grenz.SpielerGrenz;
import de.thk.syp.mobilenotworkgame.fachlogikapi.grenz.SpielperiodeGrenz;
import de.thk.syp.mobilenotworkgame.mnwgdbmodel.entities.Spieler;
import de.thk.syp.mobilenotworkgame.mnwgdbmodel.entities.Spielperiode;
import de.thk.syp.mobilenotworkgame.mnwgdbmodel.entities.Punktestandspielperiode;
import de.thk.syp.mobilenotworkgame.mnwgdbmodel.impl.IDatabaseImpl;
import de.thk.syp.mobilenotworkgame.mnwgdbmodel.services.IDatabase;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.module.FindException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

public class ISpielServiceImpl implements ISpielService {
    Logger logger= LoggerFactory.getLogger("de.thk.mobilenotworkgame.fachlogik.IMessungServiceImpl");
    ICRUDSpielperiode icrudSpielperiode;
    ICRUDPunktestandSpielperiode icrudPunktestandSpielperiode;
    private EntityManager em;
    public ISpielServiceImpl(){
        logger.info("Starte ISpielServiceImpl");
        IDatabase db = new IDatabaseImpl();
        em = db.getEntityManager();
        if (em == null) {
            logger.info("EntityManager holen fehlgeschlagen");
            throw new EntityNotFoundException();
        }
        logger.info("IDatabase und em geholt.");


        // ICRUDSpielperiode Implementierung ermitteln
        Iterator<ICRUDSpielperiode> spit = ServiceLoader.load(ICRUDSpielperiode.class).iterator();
        if (spit.hasNext()) {
            icrudSpielperiode = spit.next();
            icrudSpielperiode.setEntityManager(em);
            logger.info("Implementierung fuer ICRUDSpielperiode erfolgreich ermittelt.");
        } else {
            logger.error("Keine Implementierung fuer ICRUDSpielperiode gefunden.");
            throw new FindException("Keine Implementierung fuer ICRUDSpielperiode gefunden.");
        }

        // ICRUDPunktestandSpielperiode Implementierung ermitteln
        Iterator<ICRUDPunktestandSpielperiode> pspit = ServiceLoader.load(ICRUDPunktestandSpielperiode.class).iterator();
        if (pspit.hasNext()) {
            icrudPunktestandSpielperiode = pspit.next();
            icrudPunktestandSpielperiode.setEntityManager(em);
            logger.info("Implementierung fuer ICRUDPunktestandSpielperiode erfolgreich ermittelt.");
        } else {
            logger.error("Keine Implementierung fuer ICRUDPunktestandSpielperiode gefunden.");
            throw new FindException("Keine Implementierung fuer ICRUDPunktestandSpielperiode gefunden.");
        }
    }
    /*
     * Die Methode ermittelt die aktuelle Spielperiode und gibt diese zurück
     */
    public SpielperiodeGrenz getCurrentSpielperiode() {
        Spielperiode aktSP = icrudSpielperiode.getSpielperiodeByDatum(new java.sql.Date(System.currentTimeMillis()));
        if(aktSP == null)
            return null;
        return convertSpielperiodeToGrenz(aktSP);
    }

    @Override
    public SpielperiodeGrenz getSpielperiodeByDatum(java.sql.Date date) {
        Spielperiode aktSP = icrudSpielperiode.getSpielperiodeByDatum(date);
        if(aktSP == null)
            return null;
        return convertSpielperiodeToGrenz(aktSP);
    }

    /*
     * Ermittlung aller Spielperioden
     */
    public ArrayList<SpielperiodeGrenz> getAllSpielperiode() {
        List<Spielperiode> allSP = icrudSpielperiode.getAllSpielperiode();
        if(allSP == null)
            return new ArrayList<SpielperiodeGrenz>();
        ArrayList<SpielperiodeGrenz> allSpGrenz = new ArrayList<>();
        Iterator it = allSP.iterator();
        while (it.hasNext()){
            allSpGrenz.add(convertSpielperiodeToGrenz((Spielperiode) it.next()));
        }
        return allSpGrenz;
    }

    /*
     * Die Methode gibt für eine gegebene Spielperiode den Punktestand aller Spieler zurück
     */
    public ArrayList<PunktestandspielperiodeGrenz> getPunktestandSpielperiodeBySpid(int spid) {
        ArrayList<PunktestandspielperiodeGrenz> spsGrenz = new ArrayList<>();
        List<Punktestandspielperiode> sps = icrudPunktestandSpielperiode.getPunktestandSpielperiodeBySpid(spid);
        if (sps == null)
            return new ArrayList<>();

        Punktestandspielperiode buf;
        Iterator<Punktestandspielperiode> it = sps.iterator();
        while (it.hasNext()) {
            buf = it.next();
            spsGrenz.add(convertPunktestandspielperiodeToGrenz(buf));
        }
        return spsGrenz;
    }


    private SpielperiodeGrenz convertSpielperiodeToGrenz(Spielperiode aktSP){
        SpielperiodeGrenz spielperiodeGrenz = new SpielperiodeGrenz();
        SpielerGrenz spielerGrenz = new SpielerGrenz();
        Spieler spieler = aktSP.getSpieler();

        // Gewinner setzen falls vorhanden
        if (spieler != null) {
            spielerGrenz.setBenutzername(spieler.getBenutzername());
            spielerGrenz.setBlockiert(spieler.isBlockiert());
            spielerGrenz.setEinwilligungagb(spieler.getEinwilligungagb());
            spielerGrenz.setGeraeteid(spieler.getGeraeteid());
            spielerGrenz.setSid(spieler.getSid());
            spielperiodeGrenz.setSpieler(spielerGrenz);
        }

        spielperiodeGrenz.setSpid(aktSP.getSpid());
        spielperiodeGrenz.setBisdatum(aktSP.getBisdatum());
        spielperiodeGrenz.setVondatum(aktSP.getVondatum());

        return spielperiodeGrenz;
    }

    private SpielerGrenz convertSpielerToGrenz(Spieler spieler){
        SpielerGrenz spielerGrenz = new SpielerGrenz();

        spielerGrenz.setSid(spieler.getSid());
        spielerGrenz.setGeraeteid(spieler.getGeraeteid());
        spielerGrenz.setBenutzername(spieler.getBenutzername());
        spielerGrenz.setEinwilligungagb(spieler.getEinwilligungagb());
        spielerGrenz.setBlockiert(spieler.isBlockiert());

        return spielerGrenz;
    }

    private PunktestandspielperiodeGrenz convertPunktestandspielperiodeToGrenz(Punktestandspielperiode psp){
        PunktestandspielperiodeGrenz pspGrenz = new PunktestandspielperiodeGrenz();
        pspGrenz.setScid(psp.getScid());
        pspGrenz.setSummepunkte(psp.getSummepunkte());
        pspGrenz.setSpielperiode(convertSpielperiodeToGrenz(psp.getSpielperiode()));
        pspGrenz.setSpielerGrenz(convertSpielerToGrenz(psp.getSpieler()));

        return  pspGrenz;
    }



}