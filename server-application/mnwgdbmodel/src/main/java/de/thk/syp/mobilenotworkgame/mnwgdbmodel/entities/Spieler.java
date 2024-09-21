package de.thk.syp.mobilenotworkgame.mnwgdbmodel.entities;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "spieler", schema = "syp", catalog = "syp-db")
@jakarta.persistence.NamedQueries({
        @jakarta.persistence.NamedQuery(name = "Spieler.findAll", query = "SELECT s FROM Spieler s"),
        @jakarta.persistence.NamedQuery(name = "Spieler.findBySid", query = "SELECT s FROM Spieler s WHERE s.sid = :sid"),
        @jakarta.persistence.NamedQuery(name = "Spieler.findByBenutzername", query = "SELECT s FROM Spieler s WHERE s.benutzername = :benutzername"),
        @jakarta.persistence.NamedQuery(name = "Spieler.findByGeraeteId", query = "SELECT s FROM Spieler s WHERE s.geraeteid = :geraeteId"),
        @jakarta.persistence.NamedQuery(name = "Spieler.updateSpieler", query = "UPDATE Spieler s SET s.blockiert = :block WHERE s.sid = :sid")
})
public class Spieler {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "sid")
    private int sid;
    @Basic
    @Column(name = "benutzername")
    private String benutzername;
    @Basic
    @Column(name = "geraeteid")
    private String geraeteid;
    @Basic
    @Column(name = "einwilligungagb")
    private boolean einwilligungagb;
    @Basic
    @Column(name = "blockiert")
    private boolean blockiert;

    @OneToMany(mappedBy = "spieler")
    private List<Messung> messungList;

    @OneToMany(mappedBy = "spieler")
    private List<Spielperiode> spielperiodeList;

    public boolean isEinwilligungagb() {
        return einwilligungagb;
    }

    public void setEinwilligungagb(boolean einwilligungagb) {
        this.einwilligungagb = einwilligungagb;
    }




    public Spieler(int sid, String benutzername, String geraeteid, boolean einwilligungagb) {
        this.sid = sid;
        this.benutzername = benutzername;
        this.geraeteid = geraeteid;
        this.einwilligungagb = einwilligungagb;
    }
    public Spieler() {}


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

    public List<Messung> getMessungList() {
        return messungList;
    }

    public void setMessungList(List<Messung> messungList) {
        this.messungList = messungList;
    }

    public List<Spielperiode> getSpielperiodeList() {
        return spielperiodeList;
    }

    public void setSpielperiodeList(List<Spielperiode> spielperiodeList) {
        this.spielperiodeList = spielperiodeList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Spieler spieler = (Spieler) o;
        return sid == spieler.sid && einwilligungagb == spieler.einwilligungagb && blockiert == spieler.blockiert && Objects.equals(benutzername, spieler.benutzername) && Objects.equals(geraeteid, spieler.geraeteid) && Objects.equals(messungList, spieler.messungList) && Objects.equals(spielperiodeList, spieler.spielperiodeList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sid, benutzername, geraeteid, einwilligungagb, blockiert, messungList, spielperiodeList);
    }
}
