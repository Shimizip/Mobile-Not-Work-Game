package de.thk.syp.mobilenotworkgame.fachlogikapi.grenz;

import java.util.List;
import java.util.Objects;

/**
 * Grenzklasse, welche die Entitätsklasse Spieler repräsentiert.
 */
public class SpielerGrenz {
    private int sid;
    private String benutzername;
    private String geraeteid;
    private boolean einwilligungagb;
    private boolean blockiert;
    private List<MessungGrenz> messungGrenzList;
    private List<SpielperiodeGrenz> spielperiodeGrenzList;
    public boolean isEinwilligungagb() {
        return einwilligungagb;
    }

    public void setEinwilligungagb(boolean einwilligungagb) {
        this.einwilligungagb = einwilligungagb;
    }


    private PunktestandspielperiodeGrenz punktestandspielperiodeGrenz;

    public SpielerGrenz(int sid, String benutzername, String geraeteid, boolean einwilligungagb) {
        this.sid = sid;
        this.benutzername = benutzername;
        this.geraeteid = geraeteid;
        this.einwilligungagb = einwilligungagb;
    }
    public SpielerGrenz() {}


    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public String getBenutzername() {
        return benutzername;
    }

    public void setBenutzername(String benutzername) {
        this.benutzername = benutzername;
    }

    public String getGeraeteid() {
        return geraeteid;
    }

    public void setGeraeteid(String geraeteid) {
        this.geraeteid = geraeteid;
    }

    public Boolean getEinwilligungagb() {
        return einwilligungagb;
    }

    public void setEinwilligungagb(Boolean einwilligungagb) {
        this.einwilligungagb = einwilligungagb;
    }

    public boolean isBlockiert() {
        return blockiert;
    }

    public void setBlockiert(boolean blockiert) {
        this.blockiert = blockiert;
    }

    public List<MessungGrenz> getMessungList() {
        return messungGrenzList;
    }

    public void setMessungList(List<MessungGrenz> messungGrenzList) {
        this.messungGrenzList = messungGrenzList;
    }

    public List<SpielperiodeGrenz> getSpielperiodeList() {
        return spielperiodeGrenzList;
    }

    public void setSpielperiodeList(List<SpielperiodeGrenz> spielperiodeGrenzList) {
        this.spielperiodeGrenzList = spielperiodeGrenzList;
    }
    public PunktestandspielperiodeGrenz getPunktestandspielperiode() {
        return punktestandspielperiodeGrenz;
    }

    public void setPunktestandspielperiode(PunktestandspielperiodeGrenz punktestandspielperiodeGrenz) {
        this.punktestandspielperiodeGrenz = punktestandspielperiodeGrenz;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpielerGrenz that = (SpielerGrenz) o;
        return sid == that.sid && Objects.equals(benutzername, that.benutzername) && Objects.equals(geraeteid, that.geraeteid) && Objects.equals(einwilligungagb, that.einwilligungagb);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sid, benutzername, geraeteid, einwilligungagb);
    }
}
