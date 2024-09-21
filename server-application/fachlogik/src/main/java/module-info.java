module de.thk.syp.mobilenotworkgame.fachlogik {
    requires transitive de.thk.syp.mobilenotworkgame.mnwgdbmodel;
    requires transitive de.thk.syp.mobilenotworkgame.datenhaltungapi;
    requires transitive de.thk.syp.mobilenotworkgame.fachlogikapi;
    requires org.slf4j;
    requires java.sql;
    requires jakarta.persistence;
    requires org.geotools.referencing;
    requires java.desktop;
    requires org.apache.commons.codec;

    uses de.thk.syp.mobilenotworkgame.datenhaltungapi.IAdministrator;
    uses de.thk.syp.mobilenotworkgame.datenhaltungapi.ICRUDKartensegment;
    uses de.thk.syp.mobilenotworkgame.datenhaltungapi.ICRUDMessung;
    uses de.thk.syp.mobilenotworkgame.datenhaltungapi.ICRUDMobilfunkanbieter;
    uses de.thk.syp.mobilenotworkgame.datenhaltungapi.IMobilfunkstandard;
    uses de.thk.syp.mobilenotworkgame.datenhaltungapi.ICRUDPunktestandSpielperiode;
    uses de.thk.syp.mobilenotworkgame.datenhaltungapi.ICRUDSpieler;
    uses de.thk.syp.mobilenotworkgame.datenhaltungapi.ICRUDSpielperiode;
    uses de.thk.syp.mobilenotworkgame.datenhaltungapi.ISpielerData;

    provides de.thk.syp.mobilenotworkgame.fachlogikapi.IAdminService with de.thk.syp.mobilenotworkgame.fachlogik.IAdminServiceImpl;
    provides de.thk.syp.mobilenotworkgame.fachlogikapi.IKarteService with de.thk.syp.mobilenotworkgame.fachlogik.IKarteServiceImpl;
    provides de.thk.syp.mobilenotworkgame.fachlogikapi.IMessungService with de.thk.syp.mobilenotworkgame.fachlogik.IMessungServiceImpl;
    provides de.thk.syp.mobilenotworkgame.fachlogikapi.ISpielerService with de.thk.syp.mobilenotworkgame.fachlogik.ISpielerServiceImpl;
    provides de.thk.syp.mobilenotworkgame.fachlogikapi.ISpielService with de.thk.syp.mobilenotworkgame.fachlogik.ISpielServiceImpl;
}