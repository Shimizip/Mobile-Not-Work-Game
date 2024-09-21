package de.thk.syp.mobilenotworkgame.restapi.controller;

import de.thk.syp.mobilenotworkgame.fachlogikapi.IKarteService;
import de.thk.syp.mobilenotworkgame.fachlogikapi.ISpielService;
import de.thk.syp.mobilenotworkgame.fachlogikapi.grenz.PunktestandspielperiodeGrenz;
import de.thk.syp.mobilenotworkgame.fachlogikapi.grenz.SpielperiodeGrenz;
import de.thk.syp.mobilenotworkgame.restapi.SpielRestControllerApi;
import de.thk.syp.mobilenotworkgame.restapi.swagger.model.KartenbereichDatenfuerAdmin;
import de.thk.syp.mobilenotworkgame.restapi.swagger.model.ScoreboardItem;
import de.thk.syp.mobilenotworkgame.restapi.swagger.model.Spielperiode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.lang.module.FindException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

@RestController
//@CrossOrigin(origins= {"http://172.17.0.3:80", "http://localhost:3000", "http://IP:PORT", "http://DOMAIN:PORT", "http://IP:PORT", "http://DOMAIN:PORT"})
public class SpielRestControllerImpl implements SpielRestControllerApi {
    Logger logger= LoggerFactory.getLogger("de.thk.mobilenotworkgame.fachlogikapi.KarteRestControllerImpl");
    ISpielService spielSvc;
    public SpielRestControllerImpl() {
        logger.info("Starte SpielRestControllerImpl");

        Iterator<ISpielService> it = ServiceLoader.load(ISpielService.class).iterator();
        if (it.hasNext()) {
            spielSvc = it.next();
            logger.info("Implementierung fuer IS ISpielService ermittelt.");
        } else {
            logger.error("Keine Implementierung fuer ISpielService gefunden.");
            throw new FindException("Keine Implementierung fuer ISpielService gefunden.");
        }
    }
    @Override
    public ResponseEntity<List<ScoreboardItem>> getScoreboard(Integer spid) {
        int spielperiode;
        List<ScoreboardItem> response = new ArrayList<>();
        ArrayList<PunktestandspielperiodeGrenz> list;

        // Wenn keine spid angegeben wurde, das Scoreboard der aktuellen Spielperiode zurückgeben
        if (spid == null) {
            try {
                spielperiode = spielSvc.getCurrentSpielperiode().getSpid();
            } catch (Exception e) {
                logger.error("SpielRestController: " + e.getMessage());
                return ResponseEntity.status(HttpStatusCode.valueOf(500)).build();
            }

        } else {
            spielperiode = spid;
        }

        try {
            list = spielSvc.getPunktestandSpielperiodeBySpid(spielperiode);
        } catch (Exception e) {
            logger.error("SpielRestController: " + e.getMessage());
            return ResponseEntity.status(HttpStatusCode.valueOf(500)).build();
        }

        Iterator<PunktestandspielperiodeGrenz> it = list.iterator();
        PunktestandspielperiodeGrenz buf;
        while (it.hasNext()) {
            buf = it.next();
            ScoreboardItem neu = new ScoreboardItem();
            neu.setScid(buf.getScid());
            neu.setSpielerName(buf.getSpielerGrenz().getBenutzername());
            neu.setSummepunkte(buf.getSummepunkte());
            neu.setSpid(buf.getSpielperiode().getSpid());
            neu.setSid(buf.getSpielerGrenz().getSid());
            response.add(neu);
        }
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<Spielperiode>> getSpielperiode(String date) {
        List<Spielperiode> response = new ArrayList<>();
        ArrayList<SpielperiodeGrenz> list;
        SpielperiodeGrenz spielperiodeGrenz;

        if (date == null) {
            try {
                list = spielSvc.getAllSpielperiode();
            } catch (Exception e) {
                logger.error("SpielRestController: " + e.getMessage());
                return ResponseEntity.status(HttpStatusCode.valueOf(500)).build();
            }

            Iterator<SpielperiodeGrenz> it = list.iterator();
            while (it.hasNext()) {
                spielperiodeGrenz = it.next();
                Spielperiode spielperiode = new Spielperiode();
                spielperiode.setVondatum(spielperiodeGrenz.getVondatum().toLocalDate());
                spielperiode.setBisdatum(spielperiodeGrenz.getBisdatum().toLocalDate());
                spielperiode.setSpid(spielperiodeGrenz.getSpid());
                response.add(spielperiode);
            }
        } else {
            java.sql.Date sqlDate = java.sql.Date.valueOf(date);
            try {
                spielperiodeGrenz = spielSvc.getSpielperiodeByDatum(sqlDate);
            } catch (Exception e) {
                logger.error("SpielRestController: " + e.getMessage());
                return ResponseEntity.status(HttpStatusCode.valueOf(500)).build();
            }

            if (spielperiodeGrenz == null) return ResponseEntity.status(HttpStatusCode.valueOf(404)).build();

            // in REST Response entity kopieren
            Spielperiode spielperiode = new Spielperiode();
            spielperiode.setVondatum(spielperiodeGrenz.getVondatum().toLocalDate());
            spielperiode.setBisdatum(spielperiodeGrenz.getBisdatum().toLocalDate());
            spielperiode.setSpid(spielperiodeGrenz.getSpid());
            response.add(spielperiode);
        }

        return ResponseEntity.ok(response);
    }
}