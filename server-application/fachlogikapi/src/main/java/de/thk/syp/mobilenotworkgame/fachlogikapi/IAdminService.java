package de.thk.syp.mobilenotworkgame.fachlogikapi;

import de.thk.syp.mobilenotworkgame.fachlogikapi.grenz.AdministratorGrenz;

public interface IAdminService {
    AdministratorGrenz getAdminById(int aid);
    int adminLogin(String benutzername, String passwort);
}
