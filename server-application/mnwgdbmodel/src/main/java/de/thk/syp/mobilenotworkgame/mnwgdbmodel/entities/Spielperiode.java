package de.thk.syp.mobilenotworkgame.mnwgdbmodel.entities;

import jakarta.persistence.*;

import java.sql.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "spielperiode", schema = "syp", catalog = "syp-db")
@jakarta.persistence.NamedQueries({
        @jakarta.persistence.NamedQuery(name = "Spielperiode.findAll", query = "SELECT s FROM Spielperiode s"),
        @jakarta.persistence.NamedQuery(name = "Spielperiode.findBySpid", query = "SELECT s FROM Spielperiode s WHERE s.spid = :spid"),
        @jakarta.persistence.NamedQuery(name = "Spielperiode.findByVonDatum", query = "SELECT s FROM Spielperiode s WHERE s.vondatum = :vonDatum"),
        @jakarta.persistence.NamedQuery(name = "Spielperiode.findByBisDatum", query = "SELECT s FROM Spielperiode s WHERE s.bisdatum = :bisDatum"),
        @jakarta.persistence.NamedQuery(name = "Spielperiode.findByDatum", query = "SELECT s FROM Spielperiode s WHERE s.bisdatum >= :datum AND s.vondatum <= :datum"),
})
public class Spielperiode {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "spid")
    private int spid;
    @Basic
    @Column(name = "vondatum")
    private Date vondatum;
    @Basic
    @Column(name = "bisdatum")
    private Date bisdatum;

    @OneToMany(mappedBy = "spielperiode")
    private List<Punktestandspielperiode> punktestandspielperiodeList;

    @JoinColumn(name = "sid" , referencedColumnName = "sid")
    @ManyToOne
    private Spieler spieler;

    public Spielperiode(int spid, Date vondatum, Date bisdatum) {
        this.spid = spid;
        this.vondatum = vondatum;
        this.bisdatum = bisdatum;
    }
    public Spielperiode(){}

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

    public List<Punktestandspielperiode> getPunktestandspielperiodeList() {
        return punktestandspielperiodeList;
    }

    public void setPunktestandspielperiodeList(List<Punktestandspielperiode> punktestandspielperiodeList) {
        this.punktestandspielperiodeList = punktestandspielperiodeList;
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
        Spielperiode that = (Spielperiode) o;
        return spid == that.spid && Objects.equals(vondatum, that.vondatum) && Objects.equals(bisdatum, that.bisdatum) && Objects.equals(spieler, that.spieler);
    }

    @Override
    public int hashCode() {
        return Objects.hash(spid, vondatum, bisdatum, spieler);
    }
}
