package de.thk.syp.mobilenotworkgame.datenhaltungapi;

import de.thk.syp.mobilenotworkgame.mnwgdbmodel.entities.Spielperiode;

import jakarta.persistence.EntityManager;

import java.util.Date;
import java.util.List;

public interface ICRUDSpielperiode {
    public void setEntityManager(EntityManager em);
    public boolean insertSpielperiode(Spielperiode spielperiode);
    public List<Spielperiode> getAllSpielperiode();
    public Spielperiode getSpielperiodeByDatum(java.sql.Date date);
    public boolean editSpielperiode(Spielperiode spielperiode);
    public boolean deleteSpielperiode(int spid);
    public Spielperiode getSpielperiodeById(int spid);
}
