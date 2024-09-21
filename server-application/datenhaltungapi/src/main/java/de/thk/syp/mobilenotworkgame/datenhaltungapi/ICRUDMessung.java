package de.thk.syp.mobilenotworkgame.datenhaltungapi;

import de.thk.syp.mobilenotworkgame.mnwgdbmodel.entities.Messung;

import jakarta.persistence.EntityManager;
import java.util.List;

public interface ICRUDMessung {
    public void setEntityManager(EntityManager em);
    public boolean insertMessung(Messung messung);
    public Messung getMessungById(int mid);
    public boolean editMessung(Messung messung);
    public boolean deleteMessung(int mid);
    int getSumPunkteForSpielerInSpielperiode(int sid, int spid);
    public List<Messung> getMessungenByKartensegment(int ksid);
    public List<Messung> getMessungenByKartensegmentForSpielperiode(int ksid, int spid);
    public List<Messung> getMessungenByKartensegmentAndSpielerForSpielperiode(int ksid, int sid, int spid);
}
