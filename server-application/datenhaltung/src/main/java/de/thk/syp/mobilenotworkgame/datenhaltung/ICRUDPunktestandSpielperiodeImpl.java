package de.thk.syp.mobilenotworkgame.datenhaltung;

import de.thk.syp.mobilenotworkgame.datenhaltungapi.ICRUDPunktestandSpielperiode;
import de.thk.syp.mobilenotworkgame.mnwgdbmodel.entities.Mobilfunkanbieter;
import de.thk.syp.mobilenotworkgame.mnwgdbmodel.entities.Punktestandspielperiode;
import de.thk.syp.mobilenotworkgame.mnwgdbmodel.exceptions.NoEntityManagerException;

import jakarta.persistence.*;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ICRUDPunktestandSpielperiodeImpl implements ICRUDPunktestandSpielperiode {
    private EntityManager em;
    private static final Logger LOGGER = Logger.getLogger(ICRUDPunktestandSpielperiodeImpl.class.getName());

    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<Punktestandspielperiode> getAllPunktestandSpielperiode() {
        if (em == null)         //wenn kein EntityManager gesetzt ist abbrechen
            throw new NoEntityManagerException();

        String queryName = "Punktestandspielperiode.findAll";
        Query query = em.createNamedQuery(queryName);
        try {
            return (List<Punktestandspielperiode>) query.getResultList();
        } catch (PersistenceException pe) {
            throw new RuntimeException("Ein Datenbankfehler ist aufgetreten.");
        }
    }

    @Override
    public List<Punktestandspielperiode> getPunktestandSpielperiodeBySpid(int spid) {
        if (em == null)         //wenn kein EntityManager gesetzt ist abbrechen
            throw new NoEntityManagerException();

        String queryName = "Punktestandspielperiode.findBySpidSorted";
        Query query = em.createNamedQuery(queryName);
        query.setParameter("spid", spid);
        try {
            return (List<Punktestandspielperiode>) query.getResultList();
        } catch (PersistenceException pe) {
            throw new RuntimeException("Ein Datenbankfehler ist aufgetreten.");
        }
    }

    @Override
    public Punktestandspielperiode getPunktestandSpielperiodeBySpidAndSid(int spid, int sid) {
        if (em == null)         //wenn kein EntityManager gesetzt ist abbrechen
            throw new NoEntityManagerException();

        String queryName = "Punktestandspielperiode.findBySpidAndSid";
        Query query = em.createNamedQuery(queryName);
        query.setParameter("spid", spid);
        query.setParameter("sid", sid);

        Punktestandspielperiode sps;
        try {
            sps = (Punktestandspielperiode) query.getSingleResult();
        } catch (NoResultException e) {
            sps = null;
        } catch (PersistenceException pe) {
            throw new RuntimeException("Ein Datenbankfehler ist aufgetreten.");
        }
        return sps;
    }

    @Override
    public boolean insertPunktestandSpielperiode(Punktestandspielperiode punktestandspielperiode) {
        if (em == null)         //wenn kein EntityManager gesetzt ist abbrechen
            throw new NoEntityManagerException();
        if(punktestandspielperiode == null)
            throw new EntityNotFoundException();

        try{
            em.persist(punktestandspielperiode);
            return true;
        }catch (PersistenceException pe)         //schlaegt das einfuegen fehl, abbrechen und Loggen
        {
            LOGGER.log(Level.ALL, "insertPunktestandSpielperiode", pe);
            pe.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean editPunktestandSpielperiode(Punktestandspielperiode punktestandspielperiode) {
        if (em == null)         //wenn kein EntityManager gesetzt ist abbrechen
            throw new NoEntityManagerException();
        if(punktestandspielperiode == null)
            throw new EntityNotFoundException();

        Punktestandspielperiode edit = em.find(Punktestandspielperiode.class, punktestandspielperiode.getScid());
        if(edit == null) return false;

        em.merge(punktestandspielperiode);
        return true;
    }

    @Override
    public boolean deletePunktestandSpielperiode(int scid) {
        if (em == null)         //wenn kein EntityManager gesetzt ist abbrechen
            throw new NoEntityManagerException();

        Punktestandspielperiode buf = em.find(Punktestandspielperiode.class, scid);
        if(buf == null) return false;
        em.remove(buf);
        return true;
    }
}
