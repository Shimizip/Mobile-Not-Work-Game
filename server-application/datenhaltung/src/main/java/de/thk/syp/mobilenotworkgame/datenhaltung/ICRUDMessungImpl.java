package de.thk.syp.mobilenotworkgame.datenhaltung;

import de.thk.syp.mobilenotworkgame.datenhaltungapi.ICRUDMessung;
import de.thk.syp.mobilenotworkgame.mnwgdbmodel.entities.*;
import de.thk.syp.mobilenotworkgame.mnwgdbmodel.exceptions.NoEntityManagerException;

import jakarta.persistence.*;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ICRUDMessungImpl implements ICRUDMessung {
    private EntityManager em;
    private static final Logger LOGGER = Logger.getLogger(ICRUDMessungImpl.class.getName());

    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    @Override
    public boolean insertMessung(Messung messung) {
        if (em == null)         //wenn kein EntityManager gesetzt ist abbrechen
            throw new NoEntityManagerException();

        if(messung == null)
            throw new EntityNotFoundException();

        try{
            em.persist(messung);
            return true;
        }catch (PersistenceException pe)         //schlaegt das einfuegen fehl, abbrechen und Loggen
        {
            LOGGER.log(Level.ALL, "insertMessung", pe);
            pe.printStackTrace();
            return false;
        }
    }

    @Override
    public Messung getMessungById(int mid) {
        if (em == null)         //wenn kein EntityManager gesetzt ist abbrechen
            throw new NoEntityManagerException();
        return em.find(Messung.class, mid);
    }

    @Override
    public boolean editMessung(Messung messung) {
        if (em == null)         //wenn kein EntityManager gesetzt ist abbrechen
            throw new NoEntityManagerException();
        if (messung == null)       //wenn uebergebenes Objekt nicht korrekt initialisiert wurde abbrechen
            throw new EntityNotFoundException();

        Messung edit = em.find(Messung.class, messung.getMid());
        if(edit == null) return false;

        em.merge(messung);
        return true;
    }

    @Override
    public boolean deleteMessung(int mid) {
        if (em == null)         //wenn kein EntityManager gesetzt ist abbrechen
            throw new NoEntityManagerException();

        Messung buf = em.find(Messung.class, mid);
        if( buf == null) return false;

        em.remove(buf);
        return true;
    }


    @Override
    public int getSumPunkteForSpielerInSpielperiode(int sid, int spid) {
        if (em == null)         //wenn kein EntityManager gesetzt ist abbrechen
            throw new NoEntityManagerException();

        Spieler spieler = em.find(Spieler.class, sid);
        Spielperiode spielperiode = em.find(Spielperiode.class, spid);
        if( (spieler == null) || (spielperiode == null) ) return -1;

        String queryName = "Messung.getSumPunkteForSpielerInSpielperiode";
        Query query = em.createNamedQuery(queryName);
        query.setParameter("sid", sid);
        query.setParameter("spid", spid);

        int summepunkte;
        try {
            // TODO unschön, im Datenschema Datentyp ändern
            summepunkte = ((Long) query.getSingleResult()).intValue();
        } catch (NoResultException e){
            return -1;
        } catch (PersistenceException pe) {
            throw new RuntimeException("Ein Datenbankfehler ist aufgetreten.");
        }
        return summepunkte;
    }

    @Override
    public List<Messung> getMessungenByKartensegment(int ksid) {
        if (em == null)         //wenn kein EntityManager gesetzt ist abbrechen
            throw new NoEntityManagerException();

        String queryName = "Messung.findByKsid";
        Query query = em.createNamedQuery(queryName);
        query.setParameter("ksid", ksid);

        try {
            return (List<Messung>) query.getResultList();
        } catch (PersistenceException pe) {
            throw new RuntimeException("Ein Datenbankfehler ist aufgetreten.");
        }
    }

    @Override
    public List<Messung> getMessungenByKartensegmentForSpielperiode(int ksid, int spid) {
        //TODO implement named query
        if (em == null)         //wenn kein EntityManager gesetzt ist abbrechen
            throw new NoEntityManagerException();
        String queryName = "Messung.findByKsidUndSpid";
        Query query = em.createNamedQuery(queryName);
        query.setParameter("ksid", ksid);
        query.setParameter("spid", spid);
        try {
            return (List<Messung>) query.getResultList();
        } catch (PersistenceException pe) {
            throw new RuntimeException("Ein Datenbankfehler ist aufgetreten.");
        }
    }

    @Override
    public List<Messung> getMessungenByKartensegmentAndSpielerForSpielperiode(int ksid, int sid, int spid) {
        if (em == null)         //wenn kein EntityManager gesetzt ist abbrechen
            throw new NoEntityManagerException();
        String queryName = "Messung.findByKsidUndSidUndSpid";
        Query query = em.createNamedQuery(queryName);
        query.setParameter("ksid", ksid);
        query.setParameter("sid", sid);
        query.setParameter("spid", spid);
        try {
            return (List<Messung>) query.getResultList();
        } catch (PersistenceException pe) {
            throw new RuntimeException("Ein Datenbankfehler ist aufgetreten.");
        }
    }
}
