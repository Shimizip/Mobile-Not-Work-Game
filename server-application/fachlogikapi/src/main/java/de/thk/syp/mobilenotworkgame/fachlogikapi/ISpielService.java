package de.thk.syp.mobilenotworkgame.fachlogikapi;

import de.thk.syp.mobilenotworkgame.fachlogikapi.grenz.PunktestandspielperiodeGrenz;
import de.thk.syp.mobilenotworkgame.fachlogikapi.grenz.SpielperiodeGrenz;

import java.util.ArrayList;

public interface ISpielService {
    SpielperiodeGrenz getCurrentSpielperiode();

    SpielperiodeGrenz getSpielperiodeByDatum(java.sql.Date date);

    ArrayList<SpielperiodeGrenz> getAllSpielperiode();
    ArrayList<PunktestandspielperiodeGrenz> getPunktestandSpielperiodeBySpid(int spid);
}