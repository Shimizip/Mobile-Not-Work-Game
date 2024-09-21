package de.thk.syp.mobilenotworkgame.datenhaltung;

import de.thk.syp.mobilenotworkgame.datenhaltungapi.ICRUDSpieler;
import de.thk.syp.mobilenotworkgame.mnwgdbmodel.entities.Kartensegment;
import de.thk.syp.mobilenotworkgame.mnwgdbmodel.entities.Spieler;
import de.thk.syp.mobilenotworkgame.mnwgdbmodel.exceptions.NoEntityManagerException;

import jakarta.persistence.*;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ICRUDSpielerImpl implements ICRUDSpieler {
    private EntityManager em;
    private static final Logger LOGGER = Logger.getLogger(ICRUDSpielerImpl.class.getName());

    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    @Override
    public boolean insertSpieler(Spieler spieler) {
        if (em == null)         //wenn kein EntityManager gesetzt ist abbrechen
            throw new NoEntityManagerException();

        if(spieler == null)
            throw new EntityNotFoundException();

        try{
            em.persist(spieler);
            return true;
        }catch (PersistenceException pe)         //schlaegt das einfuegen fehl, abbrechen und Loggen
        {
            LOGGER.log(Level.ALL, "insertSpieler", pe);
            pe.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Spieler> getAlleSpieler() {
        if (em == null)         //wenn kein EntityManager gesetzt ist abbrechen
            throw new NoEntityManagerException();

        String queryName = "Spieler.findAll";
        Query query = em.createNamedQuery(queryName);
        try {
            return (List<Spieler>)query.getResultList();
        } catch (PersistenceException | IllegalStateException pe) {
            throw new RuntimeException("Ein Datenbankfehler ist aufgetreten.");
        }
    }

    @Override
    public boolean editSpieler(Spieler spieler) {
        if (em == null)         //wenn kein EntityManager gesetzt ist abbrechen
            throw new NoEntityManagerException();

        if(spieler == null)
            throw new EntityNotFoundException();

        Spieler edit = em.find(Spieler.class, spieler.getSid());
        if(edit == null) return false;
        em.merge(spieler);
        return true;
    }

    @Override
    public Spieler getSpielerByID(int sid) {
        if (em == null)         //wenn kein EntityManager gesetzt ist abbrechen
            throw new NoEntityManagerException();

        return em.find(Spieler.class, sid);
    }

    @Override
    public Spieler getSpielerByGeraeteId(String geraeteId) {
        if (em == null)         //wenn kein EntityManager gesetzt ist abbrechen
            throw new NoEntityManagerException();

        Spieler spieler;
        String queryName = "Spieler.findByGeraeteId";
        Query query = em.createNamedQuery(queryName);
        query.setParameter("geraeteId", geraeteId);

        try {
            spieler = (Spieler) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (PersistenceException pe) {
            throw new RuntimeException("Ein Datenbankfehler ist aufgetreten.");
        }

        return spieler;
    }
}
