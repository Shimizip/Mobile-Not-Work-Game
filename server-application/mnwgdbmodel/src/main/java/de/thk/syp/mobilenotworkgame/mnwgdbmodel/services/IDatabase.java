package de.thk.syp.mobilenotworkgame.mnwgdbmodel.services;

import jakarta.persistence.EntityManager;

public interface IDatabase {
    public EntityManager getEntityManager();
}
