package de.thk.syp.mobilenotworkgame.fachlogikapi.grenz;

import java.util.List;
import java.util.Objects;

/**
 * Grenzklasse, welche die Entitätsklasse Kartensegment repräsentiert.
 */
public class KartensegmentGrenz {
    private int ksid;
    private double mittelpunktlat;
    private double mittelpunktlon;
    private List<MessungGrenz> messungGrenzList;
    public KartensegmentGrenz(int ksid, double mittelpunktlat, double mittelpunktlon) {
        this.ksid = ksid;
        this.mittelpunktlat = mittelpunktlat;
        this.mittelpunktlon = mittelpunktlon;
    }
    public KartensegmentGrenz(){}

    public int getKsid() {
        return ksid;
    }

    public void setKsid(int ksid) {
        this.ksid = ksid;
    }

    public double getMittelpunktlat() {
        return mittelpunktlat;
    }

    public void setMittelpunktlat(double mittelpunktlat) {
        this.mittelpunktlat = mittelpunktlat;
    }

    public double getMittelpunktlon() {
        return mittelpunktlon;
    }

    public void setMittelpunktlon(double mittelpunktlon) {
        this.mittelpunktlon = mittelpunktlon;
    }

    public List<MessungGrenz> getMessungList() {
        return messungGrenzList;
    }

    public void setMessungList(List<MessungGrenz> messungGrenzList) {
        this.messungGrenzList = messungGrenzList;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KartensegmentGrenz that = (KartensegmentGrenz) o;
        return ksid == that.ksid && Objects.equals(mittelpunktlat, that.mittelpunktlat) && Objects.equals(mittelpunktlon, that.mittelpunktlon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ksid, mittelpunktlat, mittelpunktlon);
    }

}
