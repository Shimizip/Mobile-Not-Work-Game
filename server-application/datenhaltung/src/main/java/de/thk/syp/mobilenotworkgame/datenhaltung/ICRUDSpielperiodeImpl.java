package de.thk.syp.mobilenotworkgame.datenhaltung;

import de.thk.syp.mobilenotworkgame.datenhaltungapi.ICRUDSpielperiode;
import de.thk.syp.mobilenotworkgame.mnwgdbmodel.entities.Punktestandspielperiode;
import de.thk.syp.mobilenotworkgame.mnwgdbmodel.entities.Spieler;
import de.thk.syp.mobilenotworkgame.mnwgdbmodel.entities.Spielperiode;
import de.thk.syp.mobilenotworkgame.mnwgdbmodel.exceptions.NoEntityManagerException;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ICRUDSpielperiodeImpl implements ICRUDSpielperiode {
    private EntityManager em;
    private static final Logger LOGGER = Logger.getLogger(ICRUDSpielperiodeImpl.class.getName());

    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    @Override
    public boolean insertSpielperiode(Spielperiode spielperiode) {
        if (em == null)         //wenn kein EntityManager gesetzt ist abbrechen
            throw new NoEntityManagerException();
        if(spielperiode == null)
            throw new EntityNotFoundException();

        try{
            em.persist(spielperiode);
            return true;
        }catch (PersistenceException pe)         //schlaegt das einfuegen fehl, abbrechen und Loggen
        {
            LOGGER.log(Level.ALL, "insertSpielperiode", pe);
            pe.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Spielperiode> getAllSpielperiode() {
        if (em == null)         //wenn kein EntityManager gesetzt ist abbrechen
            throw new NoEntityManagerException();
        String queryName = "Spielperiode.findAll";
        Query query = em.createNamedQuery(queryName);
        try {
            return (List<Spielperiode>)query.getResultList();
        } catch (PersistenceException pe) {
            throw new RuntimeException("Ein Datenbankfehler ist aufgetreten.");
        }
    }

    @Override
    public Spielperiode getSpielperiodeByDatum(java.sql.Date date) {
        if (em == null)         //wenn kein EntityManager gesetzt ist abbrechen
            throw new NoEntityManagerException();
        if (date == null)
            return null;

        String queryName = "Spielperiode.findByDatum";
        Query query = em.createNamedQuery(queryName);
        query.setParameter("datum", date);
        try{
            return (Spielperiode)query.getSingleResult();
        }catch (NoResultException e){
            return null;
        } catch (PersistenceException pe) {
            throw new RuntimeException("Ein Datenbankfehler ist aufgetreten.");
        }
    }

    @Override
    public boolean editSpielperiode(Spielperiode spielperiode) {
        if (em == null)         //wenn kein EntityManager gesetzt ist abbrechen
            throw new NoEntityManagerException();
        if(spielperiode == null)
            throw new EntityNotFoundException();

        Spielperiode edit = em.find(Spielperiode.class, spielperiode.getSpid());
        if(edit == null) return false;
        em.merge(spielperiode);
        return true;
    }

    @Override
    public boolean deleteSpielperiode(int spid) {
        if (em == null)         //wenn kein EntityManager gesetzt ist abbrechen
            throw new NoEntityManagerException();
        Spielperiode spielperiode = em.find(Spielperiode.class, spid);
        if(spielperiode == null) return false;
        em.remove(spielperiode);
        return true;
    }

    @Override
    public Spielperiode getSpielperiodeById(int spid) {
        if (em == null)         //wenn kein EntityManager gesetzt ist abbrechen
            throw new NoEntityManagerException();
        return em.find(Spielperiode.class, spid);
    }
}
