package de.thk.syp.mobilenotworkgame.fachlogikapi.grenz;

import java.sql.Date;
import java.util.List;
import java.util.Objects;

/**
 * Grenzklasse, welche die Entitätsklasse Spielperiode repräsentiert.
 */
public class SpielperiodeGrenz {
    private int spid;
    private Date vondatum;
    private Date bisdatum;
    private List<PunktestandspielperiodeGrenz> punktestandspielperiodeGrenzList;
    private SpielerGrenz spielerGrenz;

    public SpielperiodeGrenz(int spid, Date vondatum, Date bisdatum) {
        this.spid = spid;
        this.vondatum = vondatum;
        this.bisdatum = bisdatum;
    }
    public SpielperiodeGrenz(){}

    public int getSpid() {
        return spid;
    }

    public void setSpid(int spid) {
        this.spid = spid;
    }

    public Date getVondatum() {
        return vondatum;
    }

    public void setVondatum(Date vondatum) {
        this.vondatum = vondatum;
    }

    public Date getBisdatum() {
        return bisdatum;
    }

    public void setBisdatum(Date bisdatum) {
        this.bisdatum = bisdatum;
    }

    public void setPunktestandspielperiodeList(List<PunktestandspielperiodeGrenz> punktestandspielperiodeGrenzList) {
        this.punktestandspielperiodeGrenzList = punktestandspielperiodeGrenzList;
    }

    public SpielerGrenz getSpieler() {
        return spielerGrenz;
    }

    public void setSpieler(SpielerGrenz spielerGrenz) {
        this.spielerGrenz = spielerGrenz;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpielperiodeGrenz that = (SpielperiodeGrenz) o;
        return spid == that.spid && Objects.equals(vondatum, that.vondatum) && Objects.equals(bisdatum, that.bisdatum) && Objects.equals(punktestandspielperiodeGrenzList, that.punktestandspielperiodeGrenzList) && Objects.equals(spielerGrenz, that.spielerGrenz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(spid, vondatum, bisdatum, punktestandspielperiodeGrenzList, spielerGrenz);
    }
}
