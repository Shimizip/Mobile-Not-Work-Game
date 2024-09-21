package de.thk.syp.mobilenotworkgame.datenhaltung;

import de.thk.syp.mobilenotworkgame.datenhaltungapi.IAdministrator;
import de.thk.syp.mobilenotworkgame.mnwgdbmodel.entities.*;
import de.thk.syp.mobilenotworkgame.mnwgdbmodel.exceptions.NoEntityManagerException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class IAdministratorImpl implements IAdministrator {
    private EntityManager em;
    private static final Logger LOGGER = Logger.getLogger(IAdministratorImpl.class.getName());

    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    @Override
    public Administrator getAdministratorById(int aid) {
        if (em == null)         //wenn kein EntityManager gesetzt ist abbrechen
            throw new NoEntityManagerException();

        Administrator administrator = em.find(Administrator.class, aid);
        return administrator;

    }

    @Override
    public Administrator getAdministratorByBenutzername(String benutzername) {
        if (em == null)         //wenn kein EntityManager gesetzt ist abbrechen
            throw new NoEntityManagerException();

        String queryName = "Administrator.findByBenutzername";
        Query query = em.createNamedQuery(queryName);
        query.setParameter("benutzername", benutzername);
        try {
            return (Administrator) query.getSingleResult();
        } catch (NoResultException e){
            return null;
        } catch (PersistenceException pe) {
            throw new RuntimeException("Ein Datenbankfehler ist aufgetreten.");
        }
    }

    @Override
    public List<Administrator> getAllAdministrator() {
        if (em == null)         //wenn kein EntityManager gesetzt ist abbrechen
            throw new NoEntityManagerException();


        String queryName = "Administrator.findAll";
        Query query = em.createNamedQuery(queryName);
        try {
            return (List<Administrator>) query.getResultList();
        } catch (PersistenceException pe) {
            throw new RuntimeException("Ein Datenbankfehler ist aufgetreten.");
        }
    }

    @Override
    public boolean blockSpieler(int sid, boolean blockiert) {
        if (em == null)         //wenn kein EntityManager gesetzt ist abbrechen
            throw new NoEntityManagerException();

        Spieler spieler =em.find(Spieler.class, sid);

        if(spieler == null) return false;

        spieler.setBlockiert(blockiert);
        em.merge(spieler);
        return true;

    }

    @Override
    public List<Messung> getMessungenBySpieler(int sid) {
        if (em == null)         //wenn kein EntityManager gesetzt ist abbrechen
            throw new NoEntityManagerException();

        String queryName = "Messung.findBySid";
        Query query = em.createNamedQuery(queryName);
        query.setParameter("sid", sid);
        try {
            return (List<Messung>) query.getResultList();
        } catch (PersistenceException pe) {
            throw new RuntimeException("Ein Datenbankfehler ist aufgetreten.");
        }
    }

    @Override
    public List<Messung> getMessungenByMobilfunkanbieter(int mfid) {
        if (em == null)         //wenn kein EntityManager gesetzt ist abbrechen
            throw new NoEntityManagerException();

        String queryName = "Messung.findByMfid";
        Query query = em.createNamedQuery(queryName);
        query.setParameter("mfid", mfid);
        try {
            return (List<Messung>) query.getResultList();
        } catch (PersistenceException pe) {
            throw new RuntimeException("Ein Datenbankfehler ist aufgetreten.");
        }
    }

    @Override
    public List<Messung> getMessungenByMobilfunkstandard(int msid) {
        if (em == null)         //wenn kein EntityManager gesetzt ist abbrechen
            throw new NoEntityManagerException();

        String queryName = "Messung.findByMsid";
        Query query = em.createNamedQuery(queryName);
        query.setParameter("msid", msid);
        try {
            return (List<Messung>) query.getResultList();
        } catch (PersistenceException pe) {
            throw new RuntimeException("Ein Datenbankfehler ist aufgetreten.");
        }
    }

    @Override
    public List<Messung> getMessungenBySignalstaerke(double von, double bis) {
        if (em == null)         //wenn kein EntityManager gesetzt ist abbrechen
            throw new NoEntityManagerException();

        String queryName = "Messung.findBySignalstaerke";
        Query query = em.createNamedQuery(queryName);
        query.setParameter("von", von);
        query.setParameter("bis", bis);
        try {
            return (List<Messung>) query.getResultList();
        } catch (PersistenceException pe) {
            throw new RuntimeException("Ein Datenbankfehler ist aufgetreten.");
        }
    }

}
