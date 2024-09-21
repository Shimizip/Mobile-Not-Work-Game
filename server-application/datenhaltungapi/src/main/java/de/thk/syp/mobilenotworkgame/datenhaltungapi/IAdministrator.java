package de.thk.syp.mobilenotworkgame.datenhaltungapi;

import de.thk.syp.mobilenotworkgame.mnwgdbmodel.entities.Administrator;
import de.thk.syp.mobilenotworkgame.mnwgdbmodel.entities.Messung;

import jakarta.persistence.EntityManager;
import java.util.List;

public interface IAdministrator {
    public void setEntityManager(EntityManager em);
    public Administrator getAdministratorById(int aid);
    public Administrator getAdministratorByBenutzername(String benutzername);
    public List<Administrator> getAllAdministrator();
    public boolean blockSpieler(int sid, boolean block);
    public List<Messung> getMessungenBySpieler(int sid);
    public List<Messung> getMessungenByMobilfunkanbieter(int mfid);
    public List<Messung> getMessungenByMobilfunkstandard(int msid);
    public List<Messung> getMessungenBySignalstaerke(double von, double bis);
}
