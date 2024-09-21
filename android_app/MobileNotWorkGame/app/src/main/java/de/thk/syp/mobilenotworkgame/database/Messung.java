package de.thk.syp.mobilenotworkgame.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Messung {
    @PrimaryKey(autoGenerate = true)
    public int id; // Eindeutige ID für jede Messung

    public int sid;
    public int ksid;
    public int msid;
    public int mfid;
    public int rssi;
    public int rtt;
    public int snr;
    public double lat;
    public double lon;

    public int getId() {
        return id;
    }
    public int getSid(){
        return sid;
    }
    public void setSid(int sid){
        this.sid = sid;
    }
    public int getKsid(){
        return ksid;
    }
    public void setKsid(int ksid){
        this.ksid = ksid;
    }
    public int getMsid(){
        return msid;
    }
    public void setMsid(int msid){
        this.msid = msid;
    }
    public int getMfid(){
        return mfid;
    }
    public void setMfid(int mfid){
        this.mfid = mfid;
    }
    public int getRssi(){
        return rssi;
    }
    public void setRssi(int rssi){
        this.rssi = rssi;
    }
    public int getRtt(){
        return rtt;
    }
    public void setRtt(int rtt){
        this.rtt = rtt;
    }
    public int getSnr(){
        return snr;
    }
    public void setSnr(int snr){
        this.snr = snr;
    }
    public double getLat() {
        return lat;
    }
    public void setLat(double lat) {
        this.lat = lat;
    }
    public double getLon() {
        return lon;
    }
    public void setLon(double lon) {
        this.lon = lon;
    }
}
