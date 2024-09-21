package de.thk.syp.mobilenotworkgame.datenhaltungapi;

import de.thk.syp.mobilenotworkgame.mnwgdbmodel.entities.Punktestandspielperiode;

import jakarta.persistence.EntityManager;
import java.util.List;

public interface ICRUDPunktestandSpielperiode {
    public void setEntityManager(EntityManager em);
    public List<Punktestandspielperiode> getAllPunktestandSpielperiode();
    public List<Punktestandspielperiode> getPunktestandSpielperiodeBySpid(int spid);
    public Punktestandspielperiode getPunktestandSpielperiodeBySpidAndSid(int spid, int sid);
    public boolean insertPunktestandSpielperiode(Punktestandspielperiode punktestandspielperiode);
    public boolean editPunktestandSpielperiode(Punktestandspielperiode punktestandspielperiode);
    public boolean deletePunktestandSpielperiode(int scid);
}
