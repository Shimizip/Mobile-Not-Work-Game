package de.thk.syp.mobilenotworkgame.mnwgdbmodel.services;

import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import java.sql.SQLException;

public class EntityManagerSingleton {
    private static EntityManagerSingleton instance;
    private EntityManager emProd;

    private String pu = "syp-db";

    Logger logger= LoggerFactory.getLogger("de.thk.mobilenotworkgame.mnwgdbmodel.services.EntityManagerSingleton");


    private EntityManagerSingleton() {
        emProd = Persistence.createEntityManagerFactory("MNWGPRODPU").createEntityManager();
    }

    public static EntityManagerSingleton getInstance() {
        if (instance == null) {
            instance = new EntityManagerSingleton();
        }
        try {
            Server.createTcpServer().start();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return instance;
    }


    public EntityManager getEntityManager() {
        return  emProd;
    }

}
