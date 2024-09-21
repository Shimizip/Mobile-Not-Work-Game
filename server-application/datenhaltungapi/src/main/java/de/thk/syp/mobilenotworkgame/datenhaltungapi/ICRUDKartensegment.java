package de.thk.syp.mobilenotworkgame.datenhaltungapi;

import de.thk.syp.mobilenotworkgame.mnwgdbmodel.entities.Kartensegment;

import jakarta.persistence.EntityManager;
import java.util.List;

public interface ICRUDKartensegment {
    public void setEntityManager(EntityManager em);
    public boolean insertKartensegment(Kartensegment kartensegment);
    public Kartensegment getKartensegmentById(int ksid);
    public boolean editKartensegment(Kartensegment kartensegment);
    public boolean deleteKartensegment(int ksid);
    public List<Kartensegment> getKartensegmentInRange(double vonLat, double bisLat, double vonLon, double bisLon);
    public Kartensegment getKartensegmentByMittelpunkt(double lat, double lon);
}
