package de.thk.syp.mobilenotworkgame.fachlogikapi;

import de.thk.syp.mobilenotworkgame.fachlogikapi.grenz.SpielerGrenz;

import java.util.List;

public interface ISpielerService {
    List<SpielerGrenz> getSpieler();
    SpielerGrenz getSpielerById(int sid);
    int spielerLogin(String geraeteId);
    String getSpielerBenutzernameById(int sid);
    boolean spielerRegistrieren(String geraeteId, boolean einwilligungAGB);
    boolean spielerBlockieren(int sid);
    boolean spielerFreischalten(int sid);
    boolean spielerBenutzernameAendern(int sid, String benutzername);
}