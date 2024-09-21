package de.thk.syp.mobilenotworkgame.restapi.controller;

import de.thk.syp.mobilenotworkgame.fachlogikapi.IMessungService;
import de.thk.syp.mobilenotworkgame.restapi.MessungRestControllerApi;
import de.thk.syp.mobilenotworkgame.restapi.swagger.model.MessungReply;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.lang.module.FindException;
import java.util.Iterator;
import java.util.ServiceLoader;

@RestController
//@CrossOrigin(origins= {"http://172.17.0.3:80", "http://localhost:3000", "http://IP:PORT", "http://DOMAIN:PORT", "http://IP:PORT", "http://DOMAIN:PORT"})
public class MessungRestControllerImpl implements MessungRestControllerApi {

    Logger logger= LoggerFactory.getLogger("de.thk.mobilenotworkgame.fachlogikapi.MessungRestControllerImpl");
    IMessungService messungSvc;
    public MessungRestControllerImpl() {
        logger.info("Starte MessungRestControllerImpl");
        Iterator<IMessungService> it = ServiceLoader.load(IMessungService.class).iterator();
        if (it.hasNext()) {
            messungSvc = it.next();
            logger.info("Implementierung fuer IMessungService erfolgreich ermittelt.");
        } else {
            logger.error("Keine Implementierung fuer IMessungService gefunden.");
            throw new FindException("Keine Implementierung fuer IMessungService gefunden.");
        }
    }



    @Override
    public ResponseEntity<MessungReply> messungErfassen(@NotNull Integer sid, @NotNull Integer ksid, @NotNull Integer mfid, @NotNull Integer msid, @NotNull Integer rssi, @NotNull Integer rtt, @NotNull Integer snr) {
        int punkte;
        try {
            punkte = messungSvc.messungErfassen(sid, ksid, msid, mfid, rssi, rtt, snr);
        } catch (Exception e) {
            logger.error("MessungRestController: " + e.getMessage());
            return ResponseEntity.status(HttpStatusCode.valueOf(500)).build();
        }
        MessungReply reply = new MessungReply();
        reply.setPunkte(punkte);
        if (punkte >= 0) {
            return ResponseEntity.ok(reply);
        } else {
            return ResponseEntity.status(HttpStatusCode.valueOf(400)).build();
        }
    }
}