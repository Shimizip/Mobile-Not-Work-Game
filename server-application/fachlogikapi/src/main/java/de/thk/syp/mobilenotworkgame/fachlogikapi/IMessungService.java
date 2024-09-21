package de.thk.syp.mobilenotworkgame.fachlogikapi;

public interface IMessungService {
    int messungErfassen(int sid, int ksid, int msid, int mfid, int rssi, int rtt, int snr);
}