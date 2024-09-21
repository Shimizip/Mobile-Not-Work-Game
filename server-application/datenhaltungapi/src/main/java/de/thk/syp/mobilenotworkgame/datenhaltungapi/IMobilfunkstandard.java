package de.thk.syp.mobilenotworkgame.datenhaltungapi;

import de.thk.syp.mobilenotworkgame.mnwgdbmodel.entities.Mobilfunkstandard;

import jakarta.persistence.EntityManager;
import java.util.List;

public interface IMobilfunkstandard {

    public void setEntityManager(EntityManager em);
    public Mobilfunkstandard getMobilfunkstandardById(int msid);
    public List<Mobilfunkstandard> getAllMobilfunkstandard();
}
