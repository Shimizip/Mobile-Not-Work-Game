package de.thk.syp.mobilenotworkgame.datenhaltung;

import de.thk.syp.mobilenotworkgame.datenhaltungapi.ISpielerData;
import de.thk.syp.mobilenotworkgame.mnwgdbmodel.entities.Messung;
import de.thk.syp.mobilenotworkgame.mnwgdbmodel.entities.Punktestandspielperiode;
import de.thk.syp.mobilenotworkgame.mnwgdbmodel.entities.Spielperiode;
import de.thk.syp.mobilenotworkgame.mnwgdbmodel.exceptions.NoEntityManagerException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import java.util.List;
import java.util.logging.Logger;

public class ISpielerDataImpl implements ISpielerData {
    private EntityManager em;
    private static final Logger LOGGER = Logger.getLogger(ISpielerDataImpl.class.getName());

    @Override
    public void setEntityManager(EntityManager em) {
        if (em == null)         //wenn kein EntityManager gesetzt ist abbrechen
            throw new NoEntityManagerException();
        this.em = em;
    }

    @Override
    public List<Messung> getMessungBySid(int sid) {
        if (em == null)         //wenn kein EntityManager gesetzt ist abbrechen
            throw new NoEntityManagerException();

        String queryName = "Messung.findBySid";
        Query query = em.createNamedQuery(queryName);
        query.setParameter("sid", sid);
        try {
            return query.getResultList();
        } catch (PersistenceException pe) {
            throw new RuntimeException("Ein Datenbankfehler ist aufgetreten.");
        }
    }

    @Override
    public Punktestandspielperiode getScorebyID(int sid) {
        if (em == null)         //wenn kein EntityManager gesetzt ist abbrechen
            throw new NoEntityManagerException();
        return em.find(Punktestandspielperiode.class, sid);
    }

    @Override
    public Spielperiode getSpielPeriode(int spid) {
        if (em == null)         //wenn kein EntityManager gesetzt ist abbrechen
            throw new NoEntityManagerException();
        return em.find(Spielperiode.class, spid);
    }

    @Override
    public List<Messung> getMessungenBySignalstaerke(double von, double bis, int sid) {
        if (em == null)         //wenn kein EntityManager gesetzt ist abbrechen
            throw new NoEntityManagerException();

        String queryName = "Messung.findBySignalstaerkeUndSid";
        Query query = em.createNamedQuery(queryName);
        query.setParameter("von", von);
        query.setParameter("bis", bis);
        query.setParameter("sid", sid);
        try {
            return (List<Messung>) query.getResultList();
        } catch (PersistenceException pe) {
            throw new RuntimeException("Ein Datenbankfehler ist aufgetreten.");
        }
    }

    @Override
    public List<Messung> getMessungenByMobilfunkstandard(int msid, int sid) {
        if (em == null)         //wenn kein EntityManager gesetzt ist abbrechen
            throw new NoEntityManagerException();

        String queryName = "Messung.findByMsidUndSid";
        Query query = em.createNamedQuery(queryName);
        query.setParameter("msid", msid);
        query.setParameter("sid", sid);
        try {
            return (List<Messung>) query.getResultList();
        } catch (PersistenceException pe) {
            throw new RuntimeException("Ein Datenbankfehler ist aufgetreten.");
        }
    }
}
