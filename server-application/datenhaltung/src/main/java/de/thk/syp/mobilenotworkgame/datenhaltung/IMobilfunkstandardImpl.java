package de.thk.syp.mobilenotworkgame.datenhaltung;

import de.thk.syp.mobilenotworkgame.datenhaltungapi.IMobilfunkstandard;
import de.thk.syp.mobilenotworkgame.mnwgdbmodel.entities.Messung;
import de.thk.syp.mobilenotworkgame.mnwgdbmodel.entities.Mobilfunkstandard;
import de.thk.syp.mobilenotworkgame.mnwgdbmodel.exceptions.NoEntityManagerException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import java.util.List;
import java.util.logging.Logger;

public class IMobilfunkstandardImpl implements IMobilfunkstandard {
    private EntityManager em;
    private static final Logger LOGGER = Logger.getLogger(IMobilfunkstandardImpl.class.getName());

    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    @Override
    public Mobilfunkstandard getMobilfunkstandardById(int msid) {
        if (em == null)         //wenn kein EntityManager gesetzt ist abbrechen
            throw new NoEntityManagerException();
        return em.find(Mobilfunkstandard.class, msid);
    }

    @Override
    public List<Mobilfunkstandard> getAllMobilfunkstandard() {
        if (em == null)         //wenn kein EntityManager gesetzt ist abbrechen
            throw new NoEntityManagerException();

        String queryName = "Mobilfunkstandard.findAll";
        Query query = em.createNamedQuery(queryName);
        try {
            return (List<Mobilfunkstandard>) query.getResultList();
        } catch (PersistenceException pe) {
            throw new RuntimeException("Ein Datenbankfehler ist aufgetreten.");
        }
    }
}
