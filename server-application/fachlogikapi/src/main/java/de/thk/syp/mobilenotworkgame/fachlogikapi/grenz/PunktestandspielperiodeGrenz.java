package de.thk.syp.mobilenotworkgame.fachlogikapi.grenz;

import java.util.List;
import java.util.Objects;

/**
 * Grenzklasse, welche die Entitätsklasse Punktestandspielperiode repräsentiert.
 */
public class PunktestandspielperiodeGrenz {
    private int scid;
    private Integer summepunkte;
    private SpielperiodeGrenz spielperiodeGrenz;
    private SpielerGrenz spielerGrenz;


    public PunktestandspielperiodeGrenz(int scid, Integer summepunkte) {
        this.scid = scid;
        this.summepunkte = summepunkte;
    }
    public PunktestandspielperiodeGrenz(){}
    public int getScid() {
        return scid;
    }

    public void setScid(int scid) {
        this.scid = scid;
    }

    public Integer getSummepunkte() {
        return summepunkte;
    }

    public void setSummepunkte(Integer summepunkte) {
        this.summepunkte = summepunkte;
    }

    public SpielperiodeGrenz getSpielperiode() {
        return spielperiodeGrenz;
    }

    public void setSpielperiode(SpielperiodeGrenz spielperiodeGrenz) {
        this.spielperiodeGrenz = spielperiodeGrenz;
    }

    public SpielerGrenz getSpielerGrenz() {
        return spielerGrenz;
    }

    public void setSpielerGrenz(SpielerGrenz spielerGrenz) {
        this.spielerGrenz = spielerGrenz;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PunktestandspielperiodeGrenz that = (PunktestandspielperiodeGrenz) o;
        return scid == that.scid && Objects.equals(summepunkte, that.summepunkte) && Objects.equals(spielperiodeGrenz, that.spielperiodeGrenz) && Objects.equals(spielerGrenz, that.spielerGrenz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scid, summepunkte, spielperiodeGrenz, spielerGrenz);
    }
}
