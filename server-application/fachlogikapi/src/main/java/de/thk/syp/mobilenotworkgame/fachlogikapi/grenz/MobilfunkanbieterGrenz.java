package de.thk.syp.mobilenotworkgame.fachlogikapi.grenz;

import java.util.List;
import java.util.Objects;

/**
 * Grenzklasse, welche die Entitätsklasse Mobilfunkanbieter repräsentiert.
 */
public class MobilfunkanbieterGrenz {
    private int mfid;
    private String bezeichnung;
    private List<MessungGrenz> messungGrenzList;

    public MobilfunkanbieterGrenz(int mfid, String bezeichnung) {
        this.mfid = mfid;
        this.bezeichnung = bezeichnung;
    }
    public MobilfunkanbieterGrenz(){}

    public int getMfid() {
        return mfid;
    }

    public void setMfid(int mfid) {
        this.mfid = mfid;
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
        MobilfunkanbieterGrenz that = (MobilfunkanbieterGrenz) o;
        return mfid == that.mfid && Objects.equals(bezeichnung, that.bezeichnung);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mfid, bezeichnung);
    }
}
