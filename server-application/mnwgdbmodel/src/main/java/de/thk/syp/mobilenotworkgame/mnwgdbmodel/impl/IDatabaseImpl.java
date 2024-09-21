package de.thk.syp.mobilenotworkgame.mnwgdbmodel.impl;

import de.thk.syp.mobilenotworkgame.mnwgdbmodel.services.EntityManagerSingleton;
import de.thk.syp.mobilenotworkgame.mnwgdbmodel.services.IDatabase;

import jakarta.persistence.EntityManager;

public class IDatabaseImpl implements IDatabase {
    public EntityManager getEntityManager() {
        return EntityManagerSingleton.getInstance().getEntityManager();
    }

}
