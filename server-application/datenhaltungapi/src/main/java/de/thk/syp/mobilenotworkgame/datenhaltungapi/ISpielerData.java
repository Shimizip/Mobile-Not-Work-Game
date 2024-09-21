package de.thk.syp.mobilenotworkgame.datenhaltungapi;

import de.thk.syp.mobilenotworkgame.mnwgdbmodel.entities.Messung;
import de.thk.syp.mobilenotworkgame.mnwgdbmodel.entities.Punktestandspielperiode;
import de.thk.syp.mobilenotworkgame.mnwgdbmodel.entities.Spielperiode;

import jakarta.persistence.EntityManager;
import java.util.List;

public interface ISpielerData {
    public void setEntityManager(EntityManager em);
    public List<Messung> getMessungBySid(int sid);
    public Punktestandspielperiode getScorebyID (int sid);
    public Spielperiode getSpielPeriode (int spid);
    public List<Messung> getMessungenBySignalstaerke (double von, double bis, int sid);
    public List<Messung> getMessungenByMobilfunkstandard(int msid, int sid);
}
