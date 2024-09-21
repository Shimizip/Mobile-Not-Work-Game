package de.thk.syp.mobilenotworkgame.mnwgdbmodel.entities;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "punktestandspielperiode", schema = "syp", catalog = "syp-db")
@jakarta.persistence.NamedQueries({
        @jakarta.persistence.NamedQuery(name = "Punktestandspielperiode.findAll", query = "SELECT p FROM Punktestandspielperiode p"),
        @jakarta.persistence.NamedQuery(name = "Punktestandspielperiode.findByScid", query = "SELECT p FROM Punktestandspielperiode p WHERE p.scid = :scid"),
        @jakarta.persistence.NamedQuery(name = "Punktestandspielperiode.findBySpidSorted", query = "SELECT p FROM Punktestandspielperiode p WHERE p.spielperiode.spid = :spid ORDER BY p.summepunkte DESC"),
        @jakarta.persistence.NamedQuery(name = "Punktestandspielperiode.findBySpidAndSid", query = "SELECT p FROM Punktestandspielperiode p WHERE p.spielperiode.spid = :spid AND p.spieler.sid = :sid")
})
public class Punktestandspielperiode {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "scid")
    private int scid;
    @Basic
    @Column(name = "summepunkte")
    private Integer summepunkte;

    @ManyToOne
    @JoinColumn(name = "spid", referencedColumnName = "spid")
    private Spielperiode spielperiode;

    @ManyToOne
    @JoinColumn(name = "sid", referencedColumnName = "sid")
    private Spieler spieler;

    public Punktestandspielperiode(int scid, Integer summepunkte) {
        this.scid = scid;
        this.summepunkte = summepunkte;
    }
    public Punktestandspielperiode(){}
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

    public Spielperiode getSpielperiode() {
        return spielperiode;
    }

    public void setSpielperiode(Spielperiode spielperiode) {
        this.spielperiode = spielperiode;
    }

    public Spieler getSpieler() {
        return spieler;
    }

    public void setSpieler(Spieler spieler) {
        this.spieler = spieler;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Punktestandspielperiode that = (Punktestandspielperiode) o;
        return scid == that.scid && Objects.equals(summepunkte, that.summepunkte) && Objects.equals(spielperiode, that.spielperiode) && Objects.equals(spieler, that.spieler);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scid, summepunkte, spielperiode, spieler);
    }
}
