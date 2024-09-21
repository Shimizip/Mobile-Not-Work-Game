package de.thk.syp.mobilenotworkgame.fachlogikapi.grenz;

import java.util.Objects;

/**
 * Grenzklasse, welche die Entitätsklasse Messung repräsentiert.
 */
public class MessungGrenz {
    private int mid;
    private Integer punkte;
    private Integer rssi;
    private Integer rtt;
    private Integer snr;
    private KartensegmentGrenz kartensegmentGrenz;
    private MobilfunkstandardGrenz mobilfunkstandardGrenz;
    private MobilfunkanbieterGrenz mobilfunkanbieterGrenz;
    private SpielperiodeGrenz spielperiodeGrenz;


    private SpielerGrenz spielerGrenz;

    public MessungGrenz(int mid, Integer punkte, Integer rssi, Integer rtt, Integer snr) {
        this.mid = mid;
        this.punkte = punkte;
        this.rssi = rssi;
        this.rtt = rtt;
        this.snr = snr;
    }
    public MessungGrenz(){}

    public KartensegmentGrenz getKartensegment() {
        return kartensegmentGrenz;
    }

    public void setKartensegment(KartensegmentGrenz kartensegmentGrenz) {
        this.kartensegmentGrenz = kartensegmentGrenz;
    }

    public MobilfunkstandardGrenz getMobilfunkstandard() {
        return mobilfunkstandardGrenz;
    }

    public void setMobilfunkstandard(MobilfunkstandardGrenz mobilfunkstandardGrenz) {
        this.mobilfunkstandardGrenz = mobilfunkstandardGrenz;
    }

    public MobilfunkanbieterGrenz getMobilfunkanbieter() {
        return mobilfunkanbieterGrenz;
    }

    public void setMobilfunkanbieter(MobilfunkanbieterGrenz mobilfunkanbieterGrenz) {
        this.mobilfunkanbieterGrenz = mobilfunkanbieterGrenz;
    }

    public SpielperiodeGrenz getSpielperiode() {
        return spielperiodeGrenz;
    }

    public void setSpielperiode(SpielperiodeGrenz spielperiodeGrenz) {
        this.spielperiodeGrenz = spielperiodeGrenz;
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

    public SpielerGrenz getSpieler() {
        return spielerGrenz;
    }

    public void setSpieler(SpielerGrenz spielerGrenz) {
        this.spielerGrenz = spielerGrenz;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessungGrenz that = (MessungGrenz) o;
        return mid == that.mid && Objects.equals(punkte, that.punkte) && Objects.equals(rssi, that.rssi) && Objects.equals(rtt, that.rtt) && Objects.equals(snr, that.snr) && Objects.equals(kartensegmentGrenz, that.kartensegmentGrenz) && Objects.equals(mobilfunkstandardGrenz, that.mobilfunkstandardGrenz) && Objects.equals(mobilfunkanbieterGrenz, that.mobilfunkanbieterGrenz) && Objects.equals(spielperiodeGrenz, that.spielperiodeGrenz) && Objects.equals(spielerGrenz, that.spielerGrenz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mid, punkte, rssi, rtt, snr, kartensegmentGrenz, mobilfunkstandardGrenz, mobilfunkanbieterGrenz, spielperiodeGrenz, spielerGrenz);
    }
}
