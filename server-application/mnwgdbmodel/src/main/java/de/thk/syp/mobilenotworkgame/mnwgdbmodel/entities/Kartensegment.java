package de.thk.syp.mobilenotworkgame.mnwgdbmodel.entities;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "kartensegment", schema = "syp", catalog = "syp-db")
@jakarta.persistence.NamedQueries({
        @jakarta.persistence.NamedQuery(name = "Kartensegment.findAll", query = "SELECT k FROM Kartensegment k"),
        @jakarta.persistence.NamedQuery(name = "Kartensegment.findByKsid", query = "SELECT k FROM Kartensegment k WHERE k.ksid = :ksid"),
        @jakarta.persistence.NamedQuery(name = "Kartensegment.findByLat", query = "SELECT k FROM Kartensegment k WHERE k.mittelpunktlat = :lat"),
        @jakarta.persistence.NamedQuery(name = "Kartensegment.findByLon", query = "SELECT k FROM Kartensegment k WHERE k.mittelpunktlon = :lon"),
        @jakarta.persistence.NamedQuery(name = "Kartensegment.findByCenterpointWithTolerance", query =
                "SELECT k FROM Kartensegment k WHERE " +
                        "ABS(k.mittelpunktlat - :lat) < 0.00005 AND " +
                        "ABS(k.mittelpunktlon - :lon) < 0.00005"),
        @jakarta.persistence.NamedQuery(name = "Kartensegment.findInRange", query = "SELECT k FROM Kartensegment k WHERE k.mittelpunktlat >= :vonLat AND k.mittelpunktlat <= :bisLat AND k.mittelpunktlon >= :vonLon AND k.mittelpunktlon <= :bisLon")
})
public class Kartensegment {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "ksid")
    private int ksid;
    @Basic
    @Column(name = "mittelpunktlat")
    private double mittelpunktlat;
    @Basic
    @Column(name = "mittelpunktlon")
    private double mittelpunktlon;
    @OneToMany(mappedBy = "kartensegment")
    private List<Messung> messungList;
    public Kartensegment(int ksid, double mittelpunktlat, double mittelpunktlon) {
        this.ksid = ksid;
        this.mittelpunktlat = mittelpunktlat;
        this.mittelpunktlon = mittelpunktlon;
    }
    public Kartensegment(){}

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

    public List<Messung> getMessungList() {
        return messungList;
    }

    public void setMessungList(List<Messung> messungList) {
        this.messungList = messungList;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Kartensegment that = (Kartensegment) o;
        return ksid == that.ksid && Objects.equals(mittelpunktlat, that.mittelpunktlat) && Objects.equals(mittelpunktlon, that.mittelpunktlon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ksid, mittelpunktlat, mittelpunktlon);
    }

}
