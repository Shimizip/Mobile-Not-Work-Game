package de.thk.syp.mobilenotworkgame.datenhaltung;

import de.thk.syp.mobilenotworkgame.datenhaltungapi.ICRUDMobilfunkanbieter;
import de.thk.syp.mobilenotworkgame.mnwgdbmodel.entities.Messung;
import de.thk.syp.mobilenotworkgame.mnwgdbmodel.entities.Mobilfunkanbieter;
import de.thk.syp.mobilenotworkgame.mnwgdbmodel.exceptions.NoEntityManagerException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ICRUDMobilfunkanbieterImpl implements ICRUDMobilfunkanbieter {
    private EntityManager em;
    private static final Logger LOGGER = Logger.getLogger(ICRUDMobilfunkanbieterImpl.class.getName());

    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    @Override
    public boolean insertMobilfunkanbieter(Mobilfunkanbieter mobilfunkanbieter) {
        if (em == null)         //wenn kein EntityManager gesetzt ist abbrechen
            throw new NoEntityManagerException();
        if(mobilfunkanbieter == null)
            return false;

        try{
            em.persist(mobilfunkanbieter);
            return true;
        } catch (PersistenceException pe)         //schlaegt das einfuegen fehl, abbrechen und Loggen
        {
            LOGGER.log(Level.ALL, "insertMobilfunkanbieter", pe);
            pe.printStackTrace();
            return false;
        }
    }

    @Override
    public Mobilfunkanbieter getMobilfunkanbieterById(int mfid) {
        if (em == null)         //wenn kein EntityManager gesetzt ist abbrechen
            throw new NoEntityManagerException();
        return em.find(Mobilfunkanbieter.class,mfid);
    }

    @Override
    public boolean editMobilfunkanbieter(Mobilfunkanbieter mobilfunkanbieter) {
        if (em == null)         //wenn kein EntityManager gesetzt ist abbrechen
            throw new NoEntityManagerException();
        if(mobilfunkanbieter == null)
            throw new EntityNotFoundException();

        Mobilfunkanbieter edit = em.find(Mobilfunkanbieter.class, mobilfunkanbieter.getMfid());
        if(edit == null) return false;
        em.persist(mobilfunkanbieter);
        return true;
    }

    @Override
    public boolean deleteMobilfunkanbieter(int mfid) {
        if (em == null)         //wenn kein EntityManager gesetzt ist abbrechen
            throw new NoEntityManagerException();
        Mobilfunkanbieter buf = em.find(Mobilfunkanbieter.class, mfid);
        if(buf == null) return false;
        em.remove(buf);
        return true;
    }

    @Override
    public List<Mobilfunkanbieter> getAllMobilfunkanbieter() {
        if (em == null)         //wenn kein EntityManager gesetzt ist abbrechen
            throw new NoEntityManagerException();

        String queryName = "Mobilfunkanbieter.findAll";
        Query query = em.createNamedQuery(queryName);
        try {
            return (List<Mobilfunkanbieter>) query.getResultList();
        } catch (PersistenceException pe) {
            throw new RuntimeException("Ein Datenbankfehler ist aufgetreten.");
        }
    }
}
