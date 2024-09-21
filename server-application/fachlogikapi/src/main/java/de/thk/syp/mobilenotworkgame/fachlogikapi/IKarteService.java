package de.thk.syp.mobilenotworkgame.fachlogikapi;

import de.thk.syp.mobilenotworkgame.fachlogikapi.grenz.KartensegmentGrenz;
import de.thk.syp.mobilenotworkgame.fachlogikapi.grenz.MessungGrenz;

import java.util.List;

public interface IKarteService {
    int getKsidFuerStandort(double lat, double lon);
    List<KartensegmentGrenz> getKartensegmenteInBereich(double linksObenLat, double linksObenLon, double rechtsObenLat, double rechtsObenLon, double rechtsUntenLat, double rechtsUntenLon, double linksUntenLat, double linksUntenLon);
    double[][] getCoordinatesOfHexCornersForKs(KartensegmentGrenz ks);
    MessungGrenz getHoechstePunktzahlMessungFuerKsid(int ksid);
    MessungGrenz getHoechstePunktzahlMessungFuerKsidUndSpid(int ksid, int spid);
    MessungGrenz getHoechstePunktzahlMessungFuerKsidByMfanbieter(int ksid, int mfid);
    MessungGrenz getHoechstePunktzahlMessungFuerKsidByMfanbieterUndSpid(int ksid, int mfid, int spid);
    MessungGrenz getHoechstePunktzahlMessungFuerKsidByMfstandard(int ksid, int msid);
    MessungGrenz getHoechstePunktzahlMessungFuerKsidByMfstandardUndSpid(int ksid, int msid, int spid);
    MessungGrenz getMessungFuerKsidByWorstRssi(int ksid);
    MessungGrenz getMessungFuerKsidByWorstRssiUndSpid(int ksid,int spid);
    MessungGrenz getMessungFuerKsidByWorstSnr(int ksid);
    MessungGrenz getMessungFuerKsidByWorstSnrUndSpid(int ksid, int spid);
    MessungGrenz getMessungFuerKsidByWorstRtt(int ksid);
    MessungGrenz getMessungFuerKsidByWorstRttUndSpid(int ksid, int spid);
    MessungGrenz getHoechstePunktzahlMessungFuerKsidBySpieler(int ksid, int sid);
    MessungGrenz getHoechstePunktzahlMessungFuerKsidByMfstandardAndSpieler(int ksid, int msid, int sid);
    MessungGrenz getHoechstePunktzahlMessungFuerKsidByMfanbieterAndSpieler(int ksid, int mfid, int sid);
    MessungGrenz getMessungFuerKsidByWorstRssiAndSpieler(int ksid, int sid);
    MessungGrenz getMessungFuerKsidByWorstSnrAndSpieler(int ksid, int sid);
    MessungGrenz getMessungFuerKsidByWorstRttAndSpieler(int ksid, int sid);

    String getMsidAsString(int msid);
    String getMfidAsString(int mfid);
}