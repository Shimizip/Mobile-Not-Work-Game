package de.thk.syp.mobilenotworkgame.mnwgdbmodel.entities;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "messung", schema = "syp", catalog = "syp-db")
@jakarta.persistence.NamedQueries({
        @jakarta.persistence.NamedQuery(name = "Messung.findAll", query = "SELECT m FROM Messung m"),
        @jakarta.persistence.NamedQuery(name = "Messung.findByMid", query = "SELECT m FROM Messung m WHERE m.mid = :mid"),
        @jakarta.persistence.NamedQuery(name = "Messung.findBySid", query = "SELECT m FROM Messung m WHERE m.spieler.sid = :sid"),
        @jakarta.persistence.NamedQuery(name = "Messung.findByMsidUndSid", query = "SELECT m FROM Messung m WHERE m.mobilfunkstandard.msid = :msid AND m.spieler.sid = :sid"),
        @jakarta.persistence.NamedQuery(name = "Messung.findByMfid", query = "SELECT m FROM Messung m WHERE m.mobilfunkanbieter.mfid = :mfid"),
        @jakarta.persistence.NamedQuery(name = "Messung.findByKsid", query = "SELECT m FROM Messung m WHERE m.kartensegment.ksid = :ksid"),
        @jakarta.persistence.NamedQuery(name = "Messung.findByKsidUndSpid", query = "SELECT m FROM Messung m WHERE m.kartensegment.ksid = :ksid AND m.spielperiode.spid = :spid"),
        @jakarta.persistence.NamedQuery(name = "Messung.findByKsidUndSidUndSpid", query = "SELECT m FROM Messung m WHERE m.kartensegment.ksid = :ksid AND m.spieler.sid = :sid AND m.spielperiode.spid = :spid"),
        @jakarta.persistence.NamedQuery(name = "Messung.findBySignalstaerkeUndSid", query = "SELECT m FROM Messung m WHERE m.rssi >= :von AND m.rssi <= :bis AND m.spieler.sid = :sid"),
        @jakarta.persistence.NamedQuery(name = "Messung.getSumPunkteForSpielerInSpielperiode", query = "SELECT SUM(m.punkte) FROM Messung m WHERE m.spieler.sid = :sid AND m.spielperiode.spid = :spid")
})
public class Messung {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "mid")
    private int mid;
    @Basic
    @Column(name = "punkte")
    private Integer punkte;
    @Basic
    @Column(name = "rssi")
    private Integer rssi;
    @Basic
    @Column(name = "rtt")
    private Integer rtt;
    @Basic
    @Column(name = "snr")
    private Integer snr;

    @JoinColumn(name = "ksid" , referencedColumnName = "ksid")
    @ManyToOne
    private Kartensegment kartensegment;

    @JoinColumn(name = "msid" , referencedColumnName = "msid")
    @ManyToOne
    private Mobilfunkstandard mobilfunkstandard;

    @JoinColumn(name = "mfid" , referencedColumnName = "mfid")
    @ManyToOne
    private Mobilfunkanbieter mobilfunkanbieter;

    @JoinColumn(name = "spid" , referencedColumnName = "spid")
    @ManyToOne
    private Spielperiode spielperiode;

    @ManyToOne
    @JoinColumn(name = "sid", referencedColumnName = "sid")
    private Spieler spieler;

    public Messung(int mid, Integer punkte, Integer rssi, Integer rtt, Integer snr) {
        this.mid = mid;
        this.punkte = punkte;
        this.rssi = rssi;
        this.rtt = rtt;
        this.snr = snr;
    }
    public Messung(){}

    public Kartensegment getKartensegment() {
        return kartensegment;
    }

    public void setKartensegment(Kartensegment kartensegment) {
        this.kartensegment = kartensegment;
    }

    public Mobilfunkstandard getMobilfunkstandard() {
        return mobilfunkstandard;
    }

    public void setMobilfunkstandard(Mobilfunkstandard mobilfunkstandard) {
        this.mobilfunkstandard = mobilfunkstandard;
    }

    public Mobilfunkanbieter getMobilfunkanbieter() {
        return mobilfunkanbieter;
    }

    public void setMobilfunkanbieter(Mobilfunkanbieter mobilfunkanbieter) {
        this.mobilfunkanbieter = mobilfunkanbieter;
    }

    public Spielperiode getSpielperiode() {
        return spielperiode;
    }

    public void setSpielperiode(Spielperiode spielperiode) {
        this.spielperiode = spielperiode;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public Integer getPunkte() {
        return punkte;
    }

    public void setPunkte(Integer punkte) {
        this.punkte = punkte;
    }

    public Integer getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public Integer getRtt() {
        return rtt;
    }

    public void setRtt(int rtt) {
        this.rtt = rtt;
    }

    public Integer getSnr() {
        return snr;
    }

    public void setSnr(int snr) {
        this.snr = snr;
    }


    public void setRssi(Integer rssi) {
        this.rssi = rssi;
    }

    public void setRtt(Integer rtt) {
        this.rtt = rtt;
    }

    public void setSnr(Integer snr) {
        this.snr = snr;
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
        Messung messung = (Messung) o;
        return mid == messung.mid && Objects.equals(punkte, messung.punkte) && Objects.equals(rssi, messung.rssi) && Objects.equals(rtt, messung.rtt) && Objects.equals(snr, messung.snr) && Objects.equals(kartensegment, messung.kartensegment) && Objects.equals(mobilfunkstandard, messung.mobilfunkstandard) && Objects.equals(mobilfunkanbieter, messung.mobilfunkanbieter) && Objects.equals(spielperiode, messung.spielperiode) && Objects.equals(spieler, messung.spieler);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mid, punkte, rssi, rtt, snr, kartensegment, mobilfunkstandard, mobilfunkanbieter, spielperiode, spieler);
    }
}
