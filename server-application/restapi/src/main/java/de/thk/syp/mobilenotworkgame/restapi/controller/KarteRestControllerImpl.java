package de.thk.syp.mobilenotworkgame.restapi.controller;


import de.thk.syp.mobilenotworkgame.fachlogikapi.IKarteService;
import de.thk.syp.mobilenotworkgame.fachlogikapi.ISpielerService;
import de.thk.syp.mobilenotworkgame.fachlogikapi.grenz.KartensegmentGrenz;
import de.thk.syp.mobilenotworkgame.fachlogikapi.grenz.MessungGrenz;
import de.thk.syp.mobilenotworkgame.restapi.KarteRestControllerApi;
import de.thk.syp.mobilenotworkgame.restapi.swagger.model.*;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.lang.module.FindException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

@RestController
//@CrossOrigin(origins= {"http://172.17.0.3:80", "http://localhost:3000", "http://IP:PORT", "http://DOMAIN:PORT", "http://IP:PORT", "http://DOMAIN:PORT"})
public class KarteRestControllerImpl implements KarteRestControllerApi {
    Logger logger= LoggerFactory.getLogger("de.thk.mobilenotworkgame.fachlogikapi.KarteRestControllerImpl");
    IKarteService karteSvc;

    public KarteRestControllerImpl() {
        logger.info("Starte KarteRestControllerImpl");

        Iterator<IKarteService> it = ServiceLoader.load(IKarteService.class).iterator();
        if (it.hasNext()) {
            karteSvc = it.next();
            logger.info("Implementierung fuer IKarteService erfolgreich ermittelt.");
        } else {
            logger.error("Keine Implementierung fuer IKarteService gefunden.");
            throw new FindException("Keine Implementierung fuer IKarteService gefunden.");
        }
    }
    @Override
    public ResponseEntity<List<KartenbereichDatenfuerAdmin>> getKartenbereichDatenFuerAdmin(@NotNull BigDecimal linksObenLat, @NotNull BigDecimal linksObenLon, @NotNull BigDecimal rechtsObenLat, @NotNull BigDecimal rechtsObenLon, @NotNull BigDecimal rechtsUntenLat, @NotNull BigDecimal rechtsUntenLon, @NotNull BigDecimal linksUntenLat, @NotNull BigDecimal linksUntenLon, @NotNull String filter, @NotNull Integer filterValue) {
        List<KartensegmentGrenz> kartensegmentList;
        List<KartenbereichDatenfuerAdmin> response = new ArrayList<>();

        try {
            kartensegmentList = karteSvc.getKartensegmenteInBereich(
                linksObenLat.doubleValue(),
                linksObenLon.doubleValue(),
                rechtsObenLat.doubleValue(),
                rechtsObenLon.doubleValue(),
                rechtsUntenLat.doubleValue(),
                rechtsUntenLon.doubleValue(),
                linksUntenLat.doubleValue(),
                linksUntenLon.doubleValue()
            );
        } catch (Exception e) {
            logger.error("KarteRestController: " + e.getMessage());
            return ResponseEntity.status(HttpStatusCode.valueOf(500)).build();
        }

        // Iteriere über alle Kartensegmente und baue die Datensätze zusammen
        Iterator<KartensegmentGrenz> kit = kartensegmentList.iterator();
        KartensegmentGrenz tmpKs;
        while (kit.hasNext()) {
            tmpKs = kit.next();

            // Hoechste Messung (nur diese soll angezeigt werden) und Hexagon-Koordinaten zum Kartensegment ermitteln
            MessungGrenz messung = null;

            switch (filter){
                case "msid":{
                    try {
                        messung = karteSvc.getHoechstePunktzahlMessungFuerKsidByMfstandard(tmpKs.getKsid(), filterValue);
                    } catch (Exception e) {
                        logger.error("KarteRestController: " + e.getMessage());
                        // versuche trotzdem die Messungen für die anderen Hexagone zu ermitteln, damit der Client "möglichst viel" bekommt
                        continue;
                    }
                    break;
                }
                case "rssi":{
                    try {
                        messung = karteSvc.getMessungFuerKsidByWorstRssi(tmpKs.getKsid());
                    } catch (Exception e) {
                        logger.error("KarteRestController: " + e.getMessage());
                        // versuche trotzdem die Messungen für die anderen Hexagone zu ermitteln, damit der Client "möglichst viel" bekommt
                        continue;
                    }
                    break;
                }
                case "rtt":{
                    try {
                        messung = karteSvc.getMessungFuerKsidByWorstRtt(tmpKs.getKsid());
                    } catch (Exception e) {
                        logger.error("KarteRestController: " + e.getMessage());
                        // versuche trotzdem die Messungen für die anderen Hexagone zu ermitteln, damit der Client "möglichst viel" bekommt
                        continue;
                    }
                    break;
                }
                case "snr":{
                    try {
                        messung = karteSvc.getMessungFuerKsidByWorstSnr(tmpKs.getKsid());
                    } catch (Exception e) {
                        logger.error("KarteRestController: " + e.getMessage());
                        // versuche trotzdem die Messungen für die anderen Hexagone zu ermitteln, damit der Client "möglichst viel" bekommt
                        continue;
                    }
                    break;
                }
                case "mfid":{
                    try {
                        messung = karteSvc.getHoechstePunktzahlMessungFuerKsidByMfanbieter(tmpKs.getKsid(), filterValue);
                    } catch (Exception e) {
                        logger.error("KarteRestController: " + e.getMessage());
                        // versuche trotzdem die Messungen für die anderen Hexagone zu ermitteln, damit der Client "möglichst viel" bekommt
                        continue;
                    }
                    break;
                }
                case "sid":{
                    try {
                        messung = karteSvc.getHoechstePunktzahlMessungFuerKsidBySpieler(tmpKs.getKsid(), filterValue);
                    } catch (Exception e) {
                        logger.error("KarteRestController: " + e.getMessage());
                        // versuche trotzdem die Messungen für die anderen Hexagone zu ermitteln, damit der Client "möglichst viel" bekommt
                        continue;
                    }
                    break;
                }
                default:{
                    try {
                        messung = karteSvc.getHoechstePunktzahlMessungFuerKsid(tmpKs.getKsid());
                    } catch (Exception e) {
                        logger.error("KarteRestController: " + e.getMessage());
                        // versuche trotzdem die Messungen für die anderen Hexagone zu ermitteln, damit der Client "möglichst viel" bekommt
                        continue;
                    }
                }
            }

            // Wenn es keine Messung gibt, gehe zum nächsten Kartenbereich über
            if (messung == null) {
                continue;
            }

            double [][] hexCorners = karteSvc.getCoordinatesOfHexCornersForKs(tmpKs);

            // Erstelle neue Response Entity und setze alle Attribute
            KartenbereichDatenfuerAdmin kartenbereichDaten = new KartenbereichDatenfuerAdmin();

            String mobilfunkstandard = "";
            try {
                mobilfunkstandard = karteSvc.getMsidAsString(messung.getMobilfunkstandard().getMsid());
            } catch (Exception e) {
                logger.error("KarteRestController: " + e.getMessage());
                // versuche trotzdem die Messungen für die anderen Hexagone zu ermitteln, damit der Client "möglichst viel" bekommt
                continue;
            }
            String mobilfunkanbieter = "";
            try {
                mobilfunkanbieter = karteSvc.getMfidAsString(messung.getMobilfunkanbieter().getMfid());
            } catch (Exception e) {
                logger.error("KarteRestController: " + e.getMessage());
                // versuche trotzdem die Messungen für die anderen Hexagone zu ermitteln, damit der Client "möglichst viel" bekommt
                continue;
            }

            kartenbereichDaten.setKsid(tmpKs.getKsid());
            kartenbereichDaten.setErreichtePunkte(messung.getPunkte());
            kartenbereichDaten.setRssi(messung.getRssi().intValue());
            kartenbereichDaten.setRtt(messung.getRtt().intValue());
            kartenbereichDaten.setSnr(messung.getSnr().intValue());
            kartenbereichDaten.setMobilfunkanbieterAsString(mobilfunkanbieter);
            kartenbereichDaten.setMobilfunkstandardAsString(mobilfunkstandard);

            kartenbereichDaten.setSid(messung.getSpieler().getSid());
            kartenbereichDaten.setSpielerBenutzername(messung.getSpieler().getBenutzername());

            // Baue das Hexagon zusammen
            MapHexagon hex = new MapHexagon();
            MapHexagonCorner hexc1 = new MapHexagonCorner();
            MapHexagonCorner hexc2 = new MapHexagonCorner();
            MapHexagonCorner hexc3 = new MapHexagonCorner();
            MapHexagonCorner hexc4 = new MapHexagonCorner();
            MapHexagonCorner hexc5 = new MapHexagonCorner();
            MapHexagonCorner hexc6 = new MapHexagonCorner();
            hexc1.setLatitude(new BigDecimal(hexCorners[0][0]).setScale(8, BigDecimal.ROUND_HALF_UP));
            hexc1.setLongitude(new BigDecimal(hexCorners[0][1]).setScale(8, BigDecimal.ROUND_HALF_UP));
            hexc2.setLatitude(new BigDecimal(hexCorners[1][0]).setScale(8, BigDecimal.ROUND_HALF_UP));
            hexc2.setLongitude(new BigDecimal(hexCorners[1][1]).setScale(8, BigDecimal.ROUND_HALF_UP));
            hexc3.setLatitude(new BigDecimal(hexCorners[2][0]).setScale(8, BigDecimal.ROUND_HALF_UP));
            hexc3.setLongitude(new BigDecimal(hexCorners[2][1]).setScale(8, BigDecimal.ROUND_HALF_UP));
            hexc4.setLatitude(new BigDecimal(hexCorners[3][0]).setScale(8, BigDecimal.ROUND_HALF_UP));
            hexc4.setLongitude(new BigDecimal(hexCorners[3][1]).setScale(8, BigDecimal.ROUND_HALF_UP));
            hexc5.setLatitude(new BigDecimal(hexCorners[4][0]).setScale(8, BigDecimal.ROUND_HALF_UP));
            hexc5.setLongitude(new BigDecimal(hexCorners[4][1]).setScale(8, BigDecimal.ROUND_HALF_UP));
            hexc6.setLatitude(new BigDecimal(hexCorners[5][0]).setScale(8, BigDecimal.ROUND_HALF_UP));
            hexc6.setLongitude(new BigDecimal(hexCorners[5][1]).setScale(8, BigDecimal.ROUND_HALF_UP));
            hex.setPunkt1(hexc1);
            hex.setPunkt2(hexc2);
            hex.setPunkt3(hexc3);
            hex.setPunkt4(hexc4);
            hex.setPunkt5(hexc5);
            hex.setPunkt6(hexc6);
            kartenbereichDaten.setPunkte(hex);

            // Füge den Datensatz des Kartensegments zur Liste hinzu
            response.add(kartenbereichDaten);
        }

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<KartenbereichDatenfuerSpieler>> getKartenbereichDatenFuerSpieler(@NotNull BigDecimal linksObenLat, @NotNull BigDecimal linksObenLon, @NotNull BigDecimal rechtsObenLat, @NotNull BigDecimal rechtsObenLon, @NotNull BigDecimal rechtsUntenLat, @NotNull BigDecimal rechtsUntenLon, @NotNull BigDecimal linksUntenLat, @NotNull BigDecimal linksUntenLon, @NotNull String filter, @NotNull Integer filterValue, @NotNull Integer sid) {
        List<KartensegmentGrenz> kartensegmentList;
        List<KartenbereichDatenfuerSpieler> response = new ArrayList<>();

        try {
            kartensegmentList = karteSvc.getKartensegmenteInBereich(
                linksObenLat.doubleValue(),
                linksObenLon.doubleValue(),
                rechtsObenLat.doubleValue(),
                rechtsObenLon.doubleValue(),
                rechtsUntenLat.doubleValue(),
                rechtsUntenLon.doubleValue(),
                linksUntenLat.doubleValue(),
                linksUntenLon.doubleValue()
            );
        } catch (Exception e) {
            logger.error("KarteRestController: " + e.getMessage());
            return ResponseEntity.status(HttpStatusCode.valueOf(500)).build();
        }

        if (kartensegmentList.isEmpty()) return ResponseEntity.ok(null);

        // Iteriere über alle Kartensegmente und baue die Datensätze zusammen
        Iterator<KartensegmentGrenz> kit = kartensegmentList.iterator();
        KartensegmentGrenz tmpKs;
        while (kit.hasNext()) {
            tmpKs = kit.next();

            // Hoechste Messung (nur diese soll angezeigt werden) und Hexagon-Koordinaten zum Kartensegment ermitteln
            MessungGrenz messung = null;

            switch (filter){
                case "msid":{
                    try {
                        messung = karteSvc.getHoechstePunktzahlMessungFuerKsidByMfstandardAndSpieler(tmpKs.getKsid(), filterValue, sid);
                    } catch (Exception e) {
                        logger.error("KarteRestController: " + e.getMessage());
                        // versuche trotzdem die Messungen für die anderen Hexagone zu ermitteln, damit der Client "möglichst viel" bekommt
                        continue;
                    }
                    break;
                }
                case "rssi":{
                    try {
                        messung = karteSvc.getMessungFuerKsidByWorstRssiAndSpieler(tmpKs.getKsid(),sid);
                    } catch (Exception e) {
                        logger.error("KarteRestController: " + e.getMessage());
                        // versuche trotzdem die Messungen für die anderen Hexagone zu ermitteln, damit der Client "möglichst viel" bekommt
                        continue;
                    }
                    break;
                }
                case "rtt":{
                    try {
                        messung = karteSvc.getMessungFuerKsidByWorstRttAndSpieler(tmpKs.getKsid(),sid);
                    } catch (Exception e) {
                        logger.error("KarteRestController: " + e.getMessage());
                        // versuche trotzdem die Messungen für die anderen Hexagone zu ermitteln, damit der Client "möglichst viel" bekommt
                        continue;
                    }
                    break;
                }
                case "snr":{
                    try {
                        messung = karteSvc.getMessungFuerKsidByWorstSnrAndSpieler(tmpKs.getKsid(),sid);
                    } catch (Exception e) {
                        logger.error("KarteRestController: " + e.getMessage());
                        // versuche trotzdem die Messungen für die anderen Hexagone zu ermitteln, damit der Client "möglichst viel" bekommt
                        continue;
                    }
                    break;
                }
                case "mfid":{
                    try {
                        messung = karteSvc.getHoechstePunktzahlMessungFuerKsidByMfanbieterAndSpieler(tmpKs.getKsid(), filterValue, sid);
                    } catch (Exception e) {
                        logger.error("KarteRestController: " + e.getMessage());
                        // versuche trotzdem die Messungen für die anderen Hexagone zu ermitteln, damit der Client "möglichst viel" bekommt
                        continue;
                    }
                    break;
                }
                default:{
                    try {
                        messung = karteSvc.getHoechstePunktzahlMessungFuerKsidBySpieler(tmpKs.getKsid(), sid);
                    } catch (Exception e) {
                        logger.error("KarteRestController: " + e.getMessage());
                        // versuche trotzdem die Messungen für die anderen Hexagone zu ermitteln, damit der Client "möglichst viel" bekommt
                        continue;
                    }
                }
            }
            double [][] hexCorners = karteSvc.getCoordinatesOfHexCornersForKs(tmpKs);

            // Wenn es keine Messung gibt, gehe zum nächsten Kartenbereich über
            if (messung == null) {
                continue;
            }

            // Erstelle neue Response Entity und setze alle Attribute
            KartenbereichDatenfuerSpieler kartenbereichDaten = new KartenbereichDatenfuerSpieler();

            String mobilfunkstandard = "";
            try {
                mobilfunkstandard = karteSvc.getMsidAsString(messung.getMobilfunkstandard().getMsid());
            } catch (Exception e) {
                logger.error("KarteRestController: " + e.getMessage());
                // versuche trotzdem die Messungen für die anderen Hexagone zu ermitteln, damit der Client "möglichst viel" bekommt
                continue;
            }
            String mobilfunkanbieter = "";
            try {
                mobilfunkanbieter = karteSvc.getMfidAsString(messung.getMobilfunkanbieter().getMfid());
            } catch (Exception e) {
                logger.error("KarteRestController: " + e.getMessage());
                // versuche trotzdem die Messungen für die anderen Hexagone zu ermitteln, damit der Client "möglichst viel" bekommt
                continue;
            }

            kartenbereichDaten.setKsid(tmpKs.getKsid());
            kartenbereichDaten.setErreichtePunkte(messung.getPunkte());
            kartenbereichDaten.setRssi(messung.getRssi().intValue());
            kartenbereichDaten.setRtt(messung.getRtt().intValue());
            kartenbereichDaten.setSnr(messung.getSnr().intValue());
            kartenbereichDaten.setMobilfunkanbieterAsString(mobilfunkanbieter);
            kartenbereichDaten.setMobilfunkstandardAsString(mobilfunkstandard);

            // Baue das Hexagon zusammen
            MapHexagon hex = new MapHexagon();
            MapHexagonCorner hexc1 = new MapHexagonCorner();
            MapHexagonCorner hexc2 = new MapHexagonCorner();
            MapHexagonCorner hexc3 = new MapHexagonCorner();
            MapHexagonCorner hexc4 = new MapHexagonCorner();
            MapHexagonCorner hexc5 = new MapHexagonCorner();
            MapHexagonCorner hexc6 = new MapHexagonCorner();
            hexc1.setLatitude(new BigDecimal(hexCorners[0][0]).setScale(8, BigDecimal.ROUND_HALF_UP));
            hexc1.setLongitude(new BigDecimal(hexCorners[0][1]).setScale(8, BigDecimal.ROUND_HALF_UP));
            hexc2.setLatitude(new BigDecimal(hexCorners[1][0]).setScale(8, BigDecimal.ROUND_HALF_UP));
            hexc2.setLongitude(new BigDecimal(hexCorners[1][1]).setScale(8, BigDecimal.ROUND_HALF_UP));
            hexc3.setLatitude(new BigDecimal(hexCorners[2][0]).setScale(8, BigDecimal.ROUND_HALF_UP));
            hexc3.setLongitude(new BigDecimal(hexCorners[2][1]).setScale(8, BigDecimal.ROUND_HALF_UP));
            hexc4.setLatitude(new BigDecimal(hexCorners[3][0]).setScale(8, BigDecimal.ROUND_HALF_UP));
            hexc4.setLongitude(new BigDecimal(hexCorners[3][1]).setScale(8, BigDecimal.ROUND_HALF_UP));
            hexc5.setLatitude(new BigDecimal(hexCorners[4][0]).setScale(8, BigDecimal.ROUND_HALF_UP));
            hexc5.setLongitude(new BigDecimal(hexCorners[4][1]).setScale(8, BigDecimal.ROUND_HALF_UP));
            hexc6.setLatitude(new BigDecimal(hexCorners[5][0]).setScale(8, BigDecimal.ROUND_HALF_UP));
            hexc6.setLongitude(new BigDecimal(hexCorners[5][1]).setScale(8, BigDecimal.ROUND_HALF_UP));
            hex.setPunkt1(hexc1);
            hex.setPunkt2(hexc2);
            hex.setPunkt3(hexc3);
            hex.setPunkt4(hexc4);
            hex.setPunkt5(hexc5);
            hex.setPunkt6(hexc6);
            kartenbereichDaten.setPunkte(hex);

            // Füge den Datensatz des Kartensegments zur Liste hinzu
            response.add(kartenbereichDaten);
        }

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<KsidFuerStandort> getKsidFuerStandort(@NotNull BigDecimal lat, @NotNull BigDecimal lon) {
        KsidFuerStandort response = new KsidFuerStandort();
        int ksid = 0;
        try {
            ksid = karteSvc.getKsidFuerStandort(lat.doubleValue(),lon.doubleValue());
        } catch (Exception e) {
            logger.error("KarteRestController: " + e.getMessage());
            return ResponseEntity.status(HttpStatusCode.valueOf(500)).build();
        }

        if (ksid > 0) {
            response.setKsid(ksid);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatusCode.valueOf(400)).build();
    }
}