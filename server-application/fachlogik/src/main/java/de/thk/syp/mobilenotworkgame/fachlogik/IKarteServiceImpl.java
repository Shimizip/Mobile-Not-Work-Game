package de.thk.syp.mobilenotworkgame.fachlogik;

import de.thk.syp.mobilenotworkgame.fachlogikapi.IKarteService;
import de.thk.syp.mobilenotworkgame.fachlogikapi.grenz.*;
import de.thk.syp.mobilenotworkgame.datenhaltungapi.*;
import de.thk.syp.mobilenotworkgame.mnwgdbmodel.entities.*;
import de.thk.syp.mobilenotworkgame.mnwgdbmodel.impl.IDatabaseImpl;
import de.thk.syp.mobilenotworkgame.mnwgdbmodel.services.IDatabase;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.EntityManager;

import java.awt.geom.Point2D;
import java.lang.module.FindException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import static de.thk.syp.mobilenotworkgame.fachlogik.utils.HexagonGridUtility.*;

public class IKarteServiceImpl implements IKarteService {
    Logger logger= LoggerFactory.getLogger("de.thk.syp.mobilenotworkgame.fachlogik.IKarteServiceImpl");

    ICRUDKartensegment icrudKs;
    ICRUDMessung icrudMessung;
    ICRUDMobilfunkanbieter icrudMfa;
    IMobilfunkstandard iMfs;
    ICRUDSpielperiode icrudSpielperiode;

    private static EntityManager em;

    public IKarteServiceImpl() {
        logger.info("Starte IKarteServiceImpl");
        IDatabase db = new IDatabaseImpl();
        em = db.getEntityManager();
        if (em == null) {
            logger.info("EntityManager holen fehlgeschlagen");
            throw new EntityNotFoundException();
        }
        logger.info("IDatabase und em geholt.");

        // ICRUDKarte Implementierung ermitteln
        Iterator<ICRUDKartensegment> it = ServiceLoader.load(ICRUDKartensegment.class).iterator();
        if (it.hasNext()) {
            icrudKs = it.next();
            icrudKs.setEntityManager(em);
            logger.info("Implementierung fuer ICRUDKartensegment erfolgreich ermittelt.");
        } else {
            logger.error("Keine Implementierung fuer ICRUDKartensegment gefunden.");
            throw new FindException("Keine Implementierung fuer ICRUDKartensegment gefunden.");
        }

        // ICRUDMessung Implementierung ermitteln
        Iterator<ICRUDMessung> mit = ServiceLoader.load(ICRUDMessung.class).iterator();
        if (mit.hasNext()) {
            icrudMessung = mit.next();
            icrudMessung.setEntityManager(em);
            logger.info("Implementierung fuer ICRUDMessung erfolgreich ermittelt.");
        } else {
            logger.error("Keine Implementierung fuer ICRUDMessung gefunden.");
            throw new FindException("Keine Implementierung fuer ICRUDMessung gefunden.");
        }

        // ICRUDMobilfunkanbieter Implementierung ermitteln
        Iterator<ICRUDMobilfunkanbieter> mfit = ServiceLoader.load(ICRUDMobilfunkanbieter.class).iterator();
        if (mfit.hasNext()) {
            icrudMfa = mfit.next();
            icrudMfa.setEntityManager(em);
            logger.info("Implementierung fuer ICRUDMobilfunkanbieter erfolgreich ermittelt.");
        } else {
            logger.error("Keine Implementierung fuer ICRUDMobilfunkanbieter gefunden.");
            throw new FindException("Keine Implementierung fuer ICRUDMobilfunkanbieter gefunden.");
        }

        // IMobilfunkstandard Implementierung ermitteln
        Iterator<IMobilfunkstandard> msit = ServiceLoader.load(IMobilfunkstandard.class).iterator();
        if (msit.hasNext()) {
            iMfs = msit.next();
            iMfs.setEntityManager(em);
            logger.info("Implementierung fuer IMobilfunkstandard erfolgreich ermittelt.");
        } else {
            logger.error("Keine Implementierung fuer IMobilfunkstandard gefunden.");
            throw new FindException("Keine Implementierung fuer IMobilfunkstandard gefunden.");
        }

        // ICRUDSpielperiode Implementierung ermitteln
        Iterator<ICRUDSpielperiode> spit = ServiceLoader.load(ICRUDSpielperiode.class).iterator();
        if (spit.hasNext()) {
            icrudSpielperiode = spit.next();
            icrudSpielperiode.setEntityManager(em);
            logger.info("Implementierung fuer ICRUDSpielperiode erfolgreich ermittelt.");
        } else {
            logger.error("Keine Implementierung fuer ICRUDSpielperiode gefunden.");
            throw new FindException("Keine Implementierung fuer ICRUDSpielperiode gefunden.");
        }
    }


    /*
     * Für einen gegebenen Standort bestehend aus Längen- und Breitengrad wird die Kartensegment-ID ermittelt
     * Wenn noch keines existiert, wird eines generiert
     */
    public int getKsidFuerStandort(double lat, double lon) {
        Kartensegment ks;
        int ksid;

        // Mittekpunkt des Hexagons ermitteln, in dem sich der Spieler gerade befindet
        Point2D standort = new Point2D.Double(lon, lat); // X = Lon / Y = Lat
        Point2D mpHexagon = calculateHexagonCoordinatesInHexGrid(standort); // Position im HexGrid ermitteln
        logger.info("Standort wurde im HexGrid zugeordnet: " + mpHexagon);
        mpHexagon = hexagonCoordinatesToGeographic(mpHexagon); // HexGrid in Geo-Koordinaten umrechnen

        // Prüfen, ob es bereits ein Hexagon mit entsprechendem Mittelpunkt gibt
        ks = icrudKs.getKartensegmentByMittelpunkt(mpHexagon.getY(), mpHexagon.getX());

        // Wenn nicht, neues Kartensegment erstellen
        if (ks == null) {
            Kartensegment ksNeu = new Kartensegment();
            ksNeu.setMittelpunktlat(mpHexagon.getY());
            ksNeu.setMittelpunktlon(mpHexagon.getX());

            em.getTransaction().begin();
            boolean success = icrudKs.insertKartensegment(ksNeu);
            if (success)
                em.getTransaction().commit();
            else
                em.getTransaction().rollback();

            ks = icrudKs.getKartensegmentByMittelpunkt(mpHexagon.getY(), mpHexagon.getX());
        }

        if(ks == null)
            return  -1;

        return ks.getKsid();
    }

    /*
     * Für einen gegebenen geographischen Bereich werden alle Kartensegment-IDs ermittelt und zurückgegeben
     */
    public List<KartensegmentGrenz> getKartensegmenteInBereich(double linksObenLat, double linksObenLon, double rechtsObenLat, double rechtsObenLon, double rechtsUntenLat, double rechtsUntenLon, double linksUntenLat, double linksUntenLon) {
        List<Kartensegment> kartensegmentList;
        Kartensegment tmpKs;
        List<KartensegmentGrenz> ksGrenzList = new ArrayList<>();
        kartensegmentList = icrudKs.getKartensegmentInRange(rechtsUntenLat, linksObenLat, linksObenLon, rechtsUntenLon);

        if(kartensegmentList.isEmpty())
            return new ArrayList<KartensegmentGrenz>();

        // Kartensegment Objekte in Grenzklassen Objekte kopieren
        Iterator<Kartensegment> it = kartensegmentList.iterator();
        while (it.hasNext()) {
            tmpKs = it.next();
            KartensegmentGrenz ksGrenz = new KartensegmentGrenz();
            ksGrenz.setKsid(tmpKs.getKsid());
            ksGrenz.setMittelpunktlat(tmpKs.getMittelpunktlat());
            ksGrenz.setMittelpunktlon(tmpKs.getMittelpunktlon());
            ksGrenzList.add(ksGrenz);
        }
        return ksGrenzList;
    }

    /*
     * Ermittelt für ein Kartensegment die Koordinaten der sechs Ecken des Hexagons in der Karte
     */
    public double[][] getCoordinatesOfHexCornersForKs(KartensegmentGrenz ks) {
        double hexCornerCoordniates[][] = new double[6][2];

        if (ks == null){
            throw new EntityNotFoundException();
        }

        Point2D mpHexagon = new Point2D.Double(ks.getMittelpunktlon(), ks.getMittelpunktlat());
        
        Point2D[] hexCornerCoordinates = calculateCoordinatesOfHexCorners(mpHexagon);
        // Konvertierung der Point2D-Objekte in ein Array von Koordinaten
        double[][] coordinates = new double[6][2];
        for (int i = 0; i < 6; i++) {
            coordinates[i][0] = hexCornerCoordinates[i].getY(); // X = Lon / Y = Lat
            coordinates[i][1] = hexCornerCoordinates[i].getX();
        }

        return coordinates;
    }

    /*
     * Die Methode ermittelt fuer eine gegebene ksid die Messung mit den hoechsten Punkten
     */
    public MessungGrenz getHoechstePunktzahlMessungFuerKsid(int ksid) {
        Messung tmpMessung, hoechsteMessung;
        List<Messung> messungList;
        Spielperiode aktSP = icrudSpielperiode.getSpielperiodeByDatum(new java.sql.Date(System.currentTimeMillis()));
        if(aktSP == null) return null;

        messungList = icrudMessung.getMessungenByKartensegmentForSpielperiode(ksid, aktSP.getSpid());
        if(messungList.isEmpty()) return null;

        hoechsteMessung = null;
        Iterator<Messung> it = messungList.iterator();
        while(it.hasNext()) {
            tmpMessung = it.next();
            if (hoechsteMessung == null || tmpMessung.getPunkte() > hoechsteMessung.getPunkte()) {
                hoechsteMessung = tmpMessung;
            }
        }

        return convertMessungToGrenz(hoechsteMessung);
    }

    @Override
    public MessungGrenz getHoechstePunktzahlMessungFuerKsidUndSpid(int ksid, int spid) {
        Messung tmpMessung, hoechsteMessung;
        List<Messung> messungList;

        messungList = icrudMessung.getMessungenByKartensegmentForSpielperiode(ksid, spid);
        if(messungList.isEmpty()) return null;

        hoechsteMessung = null;
        Iterator<Messung> it = messungList.iterator();
        while(it.hasNext()) {
            tmpMessung = it.next();
            if (hoechsteMessung == null || tmpMessung.getPunkte() > hoechsteMessung.getPunkte()) {
                hoechsteMessung = tmpMessung;
            }
        }

        return convertMessungToGrenz(hoechsteMessung);
    }

    @Override
    public MessungGrenz getHoechstePunktzahlMessungFuerKsidByMfanbieter(int ksid, int mfid) {
        Spielperiode aktSP = icrudSpielperiode.getSpielperiodeByDatum(new java.sql.Date(System.currentTimeMillis()));
        if (aktSP == null) return null;

        List<Messung> messungList = icrudMessung.getMessungenByKartensegmentForSpielperiode(ksid, aktSP.getSpid());
        if (messungList.isEmpty()) return null;

        Messung highestMessung = null;
        for (Messung messung : messungList) {
            if (messung.getMobilfunkanbieter().getMfid() == mfid) {
                if (highestMessung == null || highestMessung.getPunkte() < messung.getPunkte()) {
                    highestMessung = messung;
                }
            }
        }

        if (highestMessung == null) return null;

        return convertMessungToGrenz(highestMessung);
    }

    @Override
    public MessungGrenz getHoechstePunktzahlMessungFuerKsidByMfanbieterUndSpid(int ksid, int mfid, int spid) {
        List<Messung> messungList = icrudMessung.getMessungenByKartensegmentForSpielperiode(ksid, spid);
        if (messungList.isEmpty()) return null;

        Messung highestMessung = null;
        for (Messung messung : messungList) {
            if (messung.getMobilfunkanbieter().getMfid() == mfid) {
                if (highestMessung == null || highestMessung.getPunkte() < messung.getPunkte()) {
                    highestMessung = messung;
                }
            }
        }

        if (highestMessung == null) return null;

        return convertMessungToGrenz(highestMessung);
    }

    @Override
    public MessungGrenz getHoechstePunktzahlMessungFuerKsidByMfstandard(int ksid, int msid) {
        Spielperiode aktSP = icrudSpielperiode.getSpielperiodeByDatum(new java.sql.Date(System.currentTimeMillis()));
        if (aktSP == null) return null;

        List<Messung> messungList = icrudMessung.getMessungenByKartensegmentForSpielperiode(ksid, aktSP.getSpid());
        if (messungList.isEmpty()) return null;

        Messung highestMessung = null;
        for (Messung messung : messungList) {
            if (messung.getMobilfunkstandard().getMsid() == msid) {
                if (highestMessung == null || highestMessung.getPunkte() < messung.getPunkte()) {
                    highestMessung = messung;
                }
            }
        }

        if (highestMessung == null) return null;

        return convertMessungToGrenz(highestMessung);
    }

    @Override
    public MessungGrenz getHoechstePunktzahlMessungFuerKsidByMfstandardUndSpid(int ksid, int msid, int spid) {
        List<Messung> messungList = icrudMessung.getMessungenByKartensegmentForSpielperiode(ksid, spid);
        if (messungList.isEmpty()) return null;

        Messung highestMessung = null;
        for (Messung messung : messungList) {
            if (messung.getMobilfunkstandard().getMsid() == msid) {
                if (highestMessung == null || highestMessung.getPunkte() < messung.getPunkte()) {
                    highestMessung = messung;
                }
            }
        }

        if (highestMessung == null) return null;

        return convertMessungToGrenz(highestMessung);
    }

    @Override
    public MessungGrenz getMessungFuerKsidByWorstRssi(int ksid) {
        Spielperiode aktSP = icrudSpielperiode.getSpielperiodeByDatum(new java.sql.Date(System.currentTimeMillis()));
        if (aktSP == null) return null;

        List<Messung> messungList = icrudMessung.getMessungenByKartensegmentForSpielperiode(ksid, aktSP.getSpid());
        if (messungList.isEmpty()) return null;

        Messung lowestMessung = null;
        for (Messung messung : messungList) {
            if (lowestMessung == null || lowestMessung.getRssi() > messung.getRssi()) {
                lowestMessung = messung;
            }
        }

        if (lowestMessung == null) return null;

        return convertMessungToGrenz(lowestMessung);
    }

    @Override
    public MessungGrenz getMessungFuerKsidByWorstRssiUndSpid(int ksid, int spid) {
        List<Messung> messungList = icrudMessung.getMessungenByKartensegmentForSpielperiode(ksid, spid);
        if (messungList.isEmpty()) return null;

        Messung lowestMessung = null;
        for (Messung messung : messungList) {
            if (lowestMessung == null || lowestMessung.getRssi() > messung.getRssi()) {
                lowestMessung = messung;
            }
        }

        if (lowestMessung == null) return null;

        return convertMessungToGrenz(lowestMessung);
    }

    @Override
    public MessungGrenz getMessungFuerKsidByWorstSnr(int ksid) {
        Spielperiode aktSP = icrudSpielperiode.getSpielperiodeByDatum(new java.sql.Date(System.currentTimeMillis()));
        if (aktSP == null) return null;

        List<Messung> messungList = icrudMessung.getMessungenByKartensegmentForSpielperiode(ksid, aktSP.getSpid());
        if (messungList.isEmpty()) return null;

        Messung lowestMessung = null;
        for (Messung messung : messungList) {
            if (lowestMessung == null || lowestMessung.getSnr() > messung.getSnr()) {
                lowestMessung = messung;
            }
        }

        if (lowestMessung == null) return null;

        return convertMessungToGrenz(lowestMessung);
    }

    @Override
    public MessungGrenz getMessungFuerKsidByWorstSnrUndSpid(int ksid, int spid) {
        List<Messung> messungList = icrudMessung.getMessungenByKartensegmentForSpielperiode(ksid, spid);
        if (messungList.isEmpty()) return null;

        Messung lowestSnrMessung = null;
        for (Messung messung : messungList) {
            if (lowestSnrMessung == null || lowestSnrMessung.getSnr() > messung.getSnr()) {
                lowestSnrMessung = messung;
            }
        }

        if (lowestSnrMessung == null) return null;

        return convertMessungToGrenz(lowestSnrMessung);
    }

    @Override
    public MessungGrenz getMessungFuerKsidByWorstRtt(int ksid) {
        Spielperiode aktSP = icrudSpielperiode.getSpielperiodeByDatum(new java.sql.Date(System.currentTimeMillis()));
        if (aktSP == null) return null;

        List<Messung> messungList = icrudMessung.getMessungenByKartensegmentForSpielperiode(ksid, aktSP.getSpid());
        if (messungList.isEmpty()) return null;

        Messung highestMessung = null;
        for (Messung messung : messungList) {
            if (highestMessung == null || highestMessung.getRtt() < messung.getRtt()) {
                highestMessung = messung;
            }
        }

        if (highestMessung == null) return null;

        return convertMessungToGrenz(highestMessung);
    }

    @Override
    public MessungGrenz getMessungFuerKsidByWorstRttUndSpid(int ksid, int spid) {
        List<Messung> messungList = icrudMessung.getMessungenByKartensegmentForSpielperiode(ksid, spid);
        if (messungList.isEmpty()) return null;

        Messung highestMessung = null;
        for (Messung messung : messungList) {
            if (highestMessung == null || highestMessung.getRtt() < messung.getRtt()) {
                highestMessung = messung;
            }
        }

        if (highestMessung == null) return null;

        return convertMessungToGrenz(highestMessung);
    }

    @Override
    public MessungGrenz getHoechstePunktzahlMessungFuerKsidBySpieler(int ksid, int sid) {
        Spielperiode aktSP = icrudSpielperiode.getSpielperiodeByDatum(new java.sql.Date(System.currentTimeMillis()));
        if (aktSP == null) return null;

        List<Messung> messungList = icrudMessung.getMessungenByKartensegmentAndSpielerForSpielperiode(ksid, sid, aktSP.getSpid());
        if (messungList.isEmpty()) return null;

        Messung highestMessung = null;
        for (Messung messung : messungList) {
            if (highestMessung == null || highestMessung.getPunkte() < messung.getPunkte()) {
                highestMessung = messung;
            }
        }

        if (highestMessung == null) return null;

        return convertMessungToGrenz(highestMessung);
    }

    @Override
    public MessungGrenz getHoechstePunktzahlMessungFuerKsidByMfstandardAndSpieler(int ksid, int msid, int sid) {
        Spielperiode aktSP = icrudSpielperiode.getSpielperiodeByDatum(new java.sql.Date(System.currentTimeMillis()));
        if (aktSP == null) return null;

        List<Messung> messungList = icrudMessung.getMessungenByKartensegmentAndSpielerForSpielperiode(ksid, sid, aktSP.getSpid());
        if (messungList.isEmpty()) return null;

        Messung highestMessung = null;
        for (Messung messung : messungList) {
            if (messung.getMobilfunkstandard().getMsid() == msid) {
                if (highestMessung == null || highestMessung.getPunkte() < messung.getPunkte()) {
                    highestMessung = messung;
                }
            }
        }

        if (highestMessung == null || highestMessung.getMobilfunkstandard().getMsid() != msid) return null;

        return convertMessungToGrenz(highestMessung);
    }

    @Override
    public MessungGrenz getHoechstePunktzahlMessungFuerKsidByMfanbieterAndSpieler(int ksid, int mfid, int sid) {
        Spielperiode aktSP = icrudSpielperiode.getSpielperiodeByDatum(new java.sql.Date(System.currentTimeMillis()));
        if (aktSP == null) return null;

        List<Messung> messungList = icrudMessung.getMessungenByKartensegmentAndSpielerForSpielperiode(ksid, sid, aktSP.getSpid());
        if (messungList.isEmpty()) return null;

        Messung highestMessung = null;
        for (Messung messung : messungList) {
            if (messung.getMobilfunkanbieter().getMfid() == mfid) {
                if (highestMessung == null || highestMessung.getPunkte() < messung.getPunkte()) {
                    highestMessung = messung;
                }
            }
        }

        if (highestMessung == null || highestMessung.getMobilfunkanbieter().getMfid() != mfid) return null;

        return convertMessungToGrenz(highestMessung);
    }

    @Override
    public MessungGrenz getMessungFuerKsidByWorstRssiAndSpieler(int ksid, int sid) {
        Spielperiode aktSP = icrudSpielperiode.getSpielperiodeByDatum(new java.sql.Date(System.currentTimeMillis()));
        if (aktSP == null) return null;

        List<Messung> messungList = icrudMessung.getMessungenByKartensegmentAndSpielerForSpielperiode(ksid, sid, aktSP.getSpid());
        if (messungList.isEmpty()) return null;

        Messung lowestMessung = null;
        for (Messung messung : messungList) {
            if (lowestMessung == null || lowestMessung.getRssi() > messung.getRssi()) {
                lowestMessung = messung;
            }
        }

        if (lowestMessung == null) return null;

        return convertMessungToGrenz(lowestMessung);
    }

    @Override
    public MessungGrenz getMessungFuerKsidByWorstSnrAndSpieler(int ksid, int sid) {
        Spielperiode aktSP = icrudSpielperiode.getSpielperiodeByDatum(new java.sql.Date(System.currentTimeMillis()));
        if (aktSP == null) return null;

        List<Messung> messungList = icrudMessung.getMessungenByKartensegmentAndSpielerForSpielperiode(ksid, sid, aktSP.getSpid());
        if (messungList.isEmpty()) return null;

        Messung lowestMessung = null;
        for (Messung messung : messungList) {
            if (lowestMessung == null || lowestMessung.getSnr() > messung.getSnr()) {
                lowestMessung = messung;
            }
        }

        if (lowestMessung == null) return null;

        return convertMessungToGrenz(lowestMessung);
    }

    @Override
    public MessungGrenz getMessungFuerKsidByWorstRttAndSpieler(int ksid, int sid) {
        Spielperiode aktSP = icrudSpielperiode.getSpielperiodeByDatum(new java.sql.Date(System.currentTimeMillis()));
        if (aktSP == null) return null;

        List<Messung> messungList = icrudMessung.getMessungenByKartensegmentAndSpielerForSpielperiode(ksid, sid, aktSP.getSpid());
        if (messungList.isEmpty()) return null;

        Messung highestMessung = null;
        for (Messung messung : messungList) {
            if (highestMessung == null || highestMessung.getRtt() < messung.getRtt()) {
                highestMessung = messung;
            }
        }

        if (highestMessung == null) return null;

        return convertMessungToGrenz(highestMessung);
    }

    public String getMsidAsString(int msid) {
        if(iMfs.getMobilfunkstandardById(msid) == null)
            return "";
        return iMfs.getMobilfunkstandardById(msid).getBezeichnung();
    }

    public String getMfidAsString(int mfid) {
        if(icrudMfa.getMobilfunkanbieterById(mfid) == null)
            return "";
        return(icrudMfa.getMobilfunkanbieterById(mfid).getBezeichnung());
    }
    private MessungGrenz convertMessungToGrenz(Messung toConv){
        MessungGrenz messungGrenz = new MessungGrenz();
        messungGrenz.setPunkte(toConv.getPunkte());
        messungGrenz.setMid(toConv.getMid());
        messungGrenz.setRssi(toConv.getRssi());
        messungGrenz.setRtt(toConv.getRtt());
        messungGrenz.setSnr(toConv.getSnr());
        messungGrenz.setSpielperiode(
                convertSpielperiodeToGrenz(
                        toConv.getSpielperiode()
                )
        );
        messungGrenz.setKartensegment(
                convertKartensegmentToGrenz(
                        toConv.getKartensegment()
                )
        );
        messungGrenz.setSpieler(
                convertSpielerToGrenz(
                        toConv.getSpieler()
                )
        );
        messungGrenz.setMobilfunkanbieter(
                convertMobilfunkanbieterToGrenz(
                        toConv.getMobilfunkanbieter()
                )
        );
        messungGrenz.setMobilfunkstandard(
                convertMobilfunkstandardToGrenz(
                        toConv.getMobilfunkstandard()
                )
        );
        return messungGrenz;
    }

    private MobilfunkstandardGrenz convertMobilfunkstandardToGrenz(Mobilfunkstandard toConv){
        MobilfunkstandardGrenz mobilfunkstandardGrenz = new MobilfunkstandardGrenz();
        mobilfunkstandardGrenz.setBezeichnung(toConv.getBezeichnung());
        mobilfunkstandardGrenz.setMsid(toConv.getMsid());
        return mobilfunkstandardGrenz;
    }

    private MobilfunkanbieterGrenz convertMobilfunkanbieterToGrenz(Mobilfunkanbieter toConv){
        MobilfunkanbieterGrenz mobilfunkanbieterGrenz = new MobilfunkanbieterGrenz();
        mobilfunkanbieterGrenz.setBezeichnung(toConv.getBezeichnung());
        mobilfunkanbieterGrenz.setMfid(toConv.getMfid());
        return mobilfunkanbieterGrenz;
    }

    private SpielerGrenz convertSpielerToGrenz(Spieler toConv){
        SpielerGrenz spielerGrenz = new SpielerGrenz();
        spielerGrenz.setSid(toConv.getSid());
        spielerGrenz.setGeraeteid(toConv.getGeraeteid());
        spielerGrenz.setEinwilligungagb(toConv.getEinwilligungagb());
        spielerGrenz.setBenutzername(toConv.getBenutzername());
        spielerGrenz.setBlockiert(toConv.isBlockiert());
        return spielerGrenz;
    }

    private KartensegmentGrenz convertKartensegmentToGrenz(Kartensegment toConv){
        KartensegmentGrenz kartensegmentGrenz = new KartensegmentGrenz();
        kartensegmentGrenz.setMittelpunktlon(toConv.getMittelpunktlon());
        kartensegmentGrenz.setMittelpunktlat(toConv.getMittelpunktlat());
        kartensegmentGrenz.setKsid(toConv.getKsid());
        return kartensegmentGrenz;
    }

    private SpielperiodeGrenz convertSpielperiodeToGrenz(Spielperiode aktSP){
        SpielperiodeGrenz spielperiodeGrenz = new SpielperiodeGrenz();

        spielperiodeGrenz.setSpid(aktSP.getSpid());
        spielperiodeGrenz.setBisdatum(aktSP.getBisdatum());
        spielperiodeGrenz.setVondatum(aktSP.getVondatum());
        if (aktSP.getSpieler() != null) {
            spielperiodeGrenz.setSpieler(
                    convertSpielerToGrenz(aktSP.getSpieler())
            );
        }

        return spielperiodeGrenz;
    }

    private PunktestandspielperiodeGrenz convertPunktestandspielperiodeToGrenz(Punktestandspielperiode psp){
        PunktestandspielperiodeGrenz pspGrenz = new PunktestandspielperiodeGrenz();
        pspGrenz.setScid(psp.getScid());
        pspGrenz.setSummepunkte(psp.getSummepunkte());
        pspGrenz.setSpielperiode(convertSpielperiodeToGrenz(psp.getSpielperiode()));

        return  pspGrenz;
    }

}