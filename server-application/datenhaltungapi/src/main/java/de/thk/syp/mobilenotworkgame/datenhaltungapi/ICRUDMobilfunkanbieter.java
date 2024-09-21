package de.thk.syp.mobilenotworkgame.datenhaltungapi;

import de.thk.syp.mobilenotworkgame.mnwgdbmodel.entities.Mobilfunkanbieter;

import jakarta.persistence.EntityManager;
import java.util.List;

public interface ICRUDMobilfunkanbieter {
    public void setEntityManager(EntityManager em);
    public boolean insertMobilfunkanbieter(Mobilfunkanbieter mobilfunkanbieter);
    public Mobilfunkanbieter getMobilfunkanbieterById(int mfid);
    public boolean editMobilfunkanbieter(Mobilfunkanbieter mobilfunkanbieter);
    public boolean deleteMobilfunkanbieter(int mfid);
    public List<Mobilfunkanbieter> getAllMobilfunkanbieter();
}
