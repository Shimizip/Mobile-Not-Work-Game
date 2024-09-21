package de.thk.syp.mobilenotworkgame.datenhaltung;

import de.thk.syp.mobilenotworkgame.datenhaltungapi.*;
import de.thk.syp.mobilenotworkgame.mnwgdbmodel.entities.*;
import de.thk.syp.mobilenotworkgame.mnwgdbmodel.exceptions.NoEntityManagerException;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ICRUDKartensegmentImpl implements ICRUDKartensegment {
    private EntityManager em;
    private static final Logger LOGGER = Logger.getLogger(ICRUDKartensegmentImpl.class.getName());

    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    @Override
    public boolean insertKartensegment(Kartensegment kartensegment) {
        if (em == null)         //wenn kein EntityManager gesetzt ist abbrechen
            throw new NoEntityManagerException();

        if(kartensegment == null)
            throw new EntityNotFoundException();
        try{
            em.persist(kartensegment);
            return true;
        }catch (PersistenceException pe)         //schlaegt das einfuegen fehl, abbrechen und Loggen
        {
            LOGGER.log(Level.ALL, "insertKartensegment", pe);
            pe.printStackTrace();
            return false;
        }
    }

    @Override
    public Kartensegment getKartensegmentById(int ksid) {
        if (em == null)         //wenn kein EntityManager gesetzt ist abbrechen
            throw new NoEntityManagerException();

        Kartensegment kartensegment = em.find(Kartensegment.class, ksid);
        return kartensegment;
    }

    @Override
    public boolean editKartensegment(Kartensegment kartensegment) {
        if (em == null)         //wenn kein EntityManager gesetzt ist abbrechen
            throw new NoEntityManagerException();

        if (kartensegment == null)       //wenn uebergebenes Objekt nicht korrekt initialisiert wurde abbrechen
            throw new EntityNotFoundException();

        Kartensegment edit = em.find(Kartensegment.class, kartensegment.getKsid());
        if(edit == null) return false;

        em.merge(kartensegment);
        return true;
    }

    @Override
    public boolean deleteKartensegment(int ksid) {
        if (em == null)         //wenn kein EntityManager gesetzt ist abbrechen
            throw new NoEntityManagerException();

        Kartensegment kartensegment = em.find(Kartensegment.class, ksid);
        if(kartensegment == null) return false;
        em.remove(kartensegment);
        return true;
    }

    @Override
    public List<Kartensegment> getKartensegmentInRange(double vonLat, double bisLat, double vonLon, double bisLon) {
        if (em == null)         //wenn kein EntityManager gesetzt ist abbrechen
            throw new NoEntityManagerException();

        List<Kartensegment> resultList;
        String queryName = "Kartensegment.findInRange";
        Query query = em.createNamedQuery(queryName);
        query.setParameter("vonLat", vonLat);
        query.setParameter("bisLat", bisLat);
        query.setParameter("vonLon", vonLon);
        query.setParameter("bisLon", bisLon);
        try {
            resultList = (List<Kartensegment>) query.getResultList();
        } catch (PersistenceException pe) {
            throw new RuntimeException("Ein Datenbankfehler ist aufgetreten.");
        }

        return resultList;
    }

    @Override
    public Kartensegment getKartensegmentByMittelpunkt(double lat, double lon) {
        if (em == null)         //wenn kein EntityManager gesetzt ist abbrechen
            throw new NoEntityManagerException();

        Kartensegment ks;
        String queryName = "Kartensegment.findByCenterpointWithTolerance";
        Query query = em.createNamedQuery(queryName);
        query.setParameter("lat", lat);
        query.setParameter("lon", lon);

        try {
            ks = (Kartensegment) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (PersistenceException pe) {
            throw new RuntimeException("Ein Datenbankfehler ist aufgetreten.");
        }

        return ks;
    }

}
