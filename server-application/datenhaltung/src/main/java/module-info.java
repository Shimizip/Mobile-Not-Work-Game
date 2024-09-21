module de.thk.syp.mobilenotworkgame.datenhaltung {
    requires de.thk.syp.mobilenotworkgame.datenhaltungapi;
    requires de.thk.syp.mobilenotworkgame.mnwgdbmodel;
    requires java.logging;
    requires jakarta.persistence;

    provides de.thk.syp.mobilenotworkgame.datenhaltungapi.IAdministrator with de.thk.syp.mobilenotworkgame.datenhaltung.IAdministratorImpl;
    provides de.thk.syp.mobilenotworkgame.datenhaltungapi.ICRUDKartensegment with de.thk.syp.mobilenotworkgame.datenhaltung.ICRUDKartensegmentImpl;
    provides de.thk.syp.mobilenotworkgame.datenhaltungapi.ICRUDMessung with de.thk.syp.mobilenotworkgame.datenhaltung.ICRUDMessungImpl;
    provides de.thk.syp.mobilenotworkgame.datenhaltungapi.ICRUDMobilfunkanbieter with de.thk.syp.mobilenotworkgame.datenhaltung.ICRUDMobilfunkanbieterImpl;
    provides de.thk.syp.mobilenotworkgame.datenhaltungapi.ICRUDPunktestandSpielperiode with de.thk.syp.mobilenotworkgame.datenhaltung.ICRUDPunktestandSpielperiodeImpl;
    provides de.thk.syp.mobilenotworkgame.datenhaltungapi.ICRUDSpieler with de.thk.syp.mobilenotworkgame.datenhaltung.ICRUDSpielerImpl;
    provides de.thk.syp.mobilenotworkgame.datenhaltungapi.ICRUDSpielperiode with de.thk.syp.mobilenotworkgame.datenhaltung.ICRUDSpielperiodeImpl;
    provides de.thk.syp.mobilenotworkgame.datenhaltungapi.IMobilfunkstandard with de.thk.syp.mobilenotworkgame.datenhaltung.IMobilfunkstandardImpl;
    provides de.thk.syp.mobilenotworkgame.datenhaltungapi.ISpielerData with de.thk.syp.mobilenotworkgame.datenhaltung.ISpielerDataImpl;
}
