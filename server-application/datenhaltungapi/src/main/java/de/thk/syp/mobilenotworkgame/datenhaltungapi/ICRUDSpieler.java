package de.thk.syp.mobilenotworkgame.datenhaltungapi;

import de.thk.syp.mobilenotworkgame.mnwgdbmodel.entities.Spieler;

import jakarta.persistence.EntityManager;
import java.util.List;

public interface ICRUDSpieler {
    public void setEntityManager(EntityManager em);
    public boolean insertSpieler(Spieler spieler);
    public List<Spieler> getAlleSpieler();
    public boolean editSpieler(Spieler spieler);
    public Spieler getSpielerByID(int sid);
    public Spieler getSpielerByGeraeteId(String geraeteId);
}
