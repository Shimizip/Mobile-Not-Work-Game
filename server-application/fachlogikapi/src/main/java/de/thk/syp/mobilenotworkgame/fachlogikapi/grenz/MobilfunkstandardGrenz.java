package de.thk.syp.mobilenotworkgame.fachlogikapi.grenz;

import java.util.List;
import java.util.Objects;

/**
 * Grenzklasse, welche die Entitätsklasse Mpbolfunkstandard repräsentiert.
 */
public class MobilfunkstandardGrenz {
    private int msid;
    private String bezeichnung;
    private List<MessungGrenz> messungGrenzList;

    public MobilfunkstandardGrenz(int msid, String bezeichnung) {
        this.msid = msid;
        this.bezeichnung = bezeichnung;
    }
    public MobilfunkstandardGrenz(){}
    public int getMsid() {
        return msid;
    }

    public void setMsid(int msid) {
        this.msid = msid;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
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
        MobilfunkstandardGrenz that = (MobilfunkstandardGrenz) o;
        return msid == that.msid && Objects.equals(bezeichnung, that.bezeichnung);
    }

    @Override
    public int hashCode() {
        return Objects.hash(msid, bezeichnung);
    }
}
