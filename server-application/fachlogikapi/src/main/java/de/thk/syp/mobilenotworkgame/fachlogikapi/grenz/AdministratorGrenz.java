package de.thk.syp.mobilenotworkgame.fachlogikapi.grenz;

import java.util.Objects;


/**
 * Grenzklasse, welche die Entitätsklasse Administrator repräsentiert.
 */

public class AdministratorGrenz {

    private int aid;
    private String benutzername;
    private String passwort;

    public AdministratorGrenz(int aid, String benutzername, String passwort){
        this.aid = aid;
        this.benutzername = benutzername;
        this.passwort = passwort;
    }
    public AdministratorGrenz(){}

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public String getBenutzername() {
        return benutzername;
    }

    public void setBenutzername(String benutzername) {
        this.benutzername = benutzername;
    }

    public String getPasswort() {
        return passwort;
    }

    public void setPasswort(String passwort) {
        this.passwort = passwort;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdministratorGrenz that = (AdministratorGrenz) o;
        return aid == that.aid && Objects.equals(benutzername, that.benutzername) && Objects.equals(passwort, that.passwort);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aid, benutzername, passwort);
    }
}
