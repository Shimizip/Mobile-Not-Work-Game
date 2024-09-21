package de.thk.syp.mobilenotworkgame.restapi.controller;

import de.thk.syp.mobilenotworkgame.fachlogikapi.ISpielerService;
import de.thk.syp.mobilenotworkgame.fachlogikapi.grenz.SpielerGrenz;
import de.thk.syp.mobilenotworkgame.restapi.SpielerRestControllerApi;
import de.thk.syp.mobilenotworkgame.restapi.service.TokenService;
import de.thk.syp.mobilenotworkgame.restapi.swagger.model.LoginReplyDaten;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.lang.module.FindException;
import java.util.*;

@RestController
//@CrossOrigin(origins= {"http://172.17.0.3:80", "http://localhost:3000", "http://IP:PORT", "http://DOMAIN:PORT", "http://IP:PORT", "http://DOMAIN:PORT"})
public class SpielerRestControllerImpl implements SpielerRestControllerApi {

    Logger logger= LoggerFactory.getLogger("de.thk.mobilenotworkgame.fachlogikapi.SpielerRestControllerImpl");

    ISpielerService spielerSvc;


    private final TokenService tokenService;

    public SpielerRestControllerImpl(TokenService tokenService) {
        logger.info("Starte SpielerRestControllerImpl");
        this.tokenService = tokenService;
        Iterator<ISpielerService> it = ServiceLoader.load(ISpielerService.class).iterator();
        if (it.hasNext()) {
            spielerSvc = it.next();
            logger.info("Implementierung fuer ISpielerService erfolgreich ermittelt.");
        } else {
            logger.error("Keine Implementierung fuer ISpielerService gefunden.");
            throw new FindException("Keine Implementierung fuer ISpielerService gefunden.");
        }
    }

    @Override
    public ResponseEntity<List<SpielerGrenz>> getSpieler(@Parameter(in = ParameterIn.PATH, description = "Die Spieler ID", required=false, schema=@Schema()) @PathVariable("sid") Integer sid)  {
        // Einzelnen Spieler ermitteln und zurückgeben
        if (sid != null){
            List<SpielerGrenz> spielerByIdList = new ArrayList<>();
            SpielerGrenz spielerGrenz = spielerSvc.getSpielerById(sid);
            if (spielerGrenz != null) {
                spielerByIdList.add(spielerGrenz);
                return ResponseEntity.ok(spielerByIdList);
            }
            return ResponseEntity.status(HttpStatusCode.valueOf(404)).build();
        }
        // Alle Spieler zurückgeben
        List<SpielerGrenz> alleSpieler = spielerSvc.getSpieler();
        return ResponseEntity.ok(alleSpieler);
    }


    @Override
    public ResponseEntity<String> spielerBenutzernameAendern(Integer sid, String neuerBenutzername) {
        logger.info("versuche spielerSvc.spielerBenutzernameAendern");
        boolean success;

        try {
        success = spielerSvc.spielerBenutzernameAendern(sid,neuerBenutzername);
        } catch (Exception e) {
            logger.error("SpielerRestController: " + e.getMessage());
            return ResponseEntity.status(HttpStatusCode.valueOf(500)).build();
        }

        if(success){
            return ResponseEntity.ok(neuerBenutzername);
        }else {
            return ResponseEntity.status(HttpStatusCode.valueOf(500)).build();
        }
    }

    @Override
    public ResponseEntity<String> getBenutzername(Integer sid) {
        String benutzername;
        try {
            benutzername = spielerSvc.getSpielerBenutzernameById(sid);
        } catch (Exception e) {
            logger.error("SpielerRestController: " + e.getMessage());
            return ResponseEntity.status(HttpStatusCode.valueOf(500)).build();
        }

        if (!benutzername.isEmpty()) {
            return ResponseEntity.ok(benutzername);
        } else {
            return ResponseEntity.status(HttpStatusCode.valueOf(404)).build();
        }
    }

    @Override
    public ResponseEntity<Void> spielerBlockieren(Integer sid) {
        boolean success;

        try {
            success = spielerSvc.spielerBlockieren(sid);
        } catch (Exception e) {
            logger.error("SpielerRestController: " + e.getMessage());
            return ResponseEntity.status(HttpStatusCode.valueOf(500)).build();
        }

        if(success){
            return ResponseEntity.ok().build();
        }else {
            return ResponseEntity.status(HttpStatusCode.valueOf(500)).build();
        }
    }

    @Override
    public ResponseEntity<Void> spielerFreischalten(Integer sid) {
        boolean success;
        try {
            success = spielerSvc.spielerFreischalten(sid);
        } catch (Exception e) {
            logger.error("SpielerRestController: " + e.getMessage());
            return ResponseEntity.status(HttpStatusCode.valueOf(500)).build();
        }

        if(success){
            return ResponseEntity.ok().build();
        }else {
            return ResponseEntity.status(HttpStatusCode.valueOf(500)).build();
        }
    }

    @Override
    public ResponseEntity<String> spielerRegistrieren(String geraeteID, Boolean einwilligungAGB) {
        boolean sucess;
        try {
            sucess = spielerSvc.spielerRegistrieren(geraeteID, einwilligungAGB);
        } catch (Exception e) {
            logger.error("SpielerRestController: " + e.getMessage());
            return ResponseEntity.status(HttpStatusCode.valueOf(500)).build();
        }

        if (sucess) {
            return ResponseEntity.ok("Registrierung erfolgreich");
        } else {
            return ResponseEntity.status(HttpStatusCode.valueOf(400)).build();
        }
    }

    @Override
    public ResponseEntity<LoginReplyDaten> spielerLogin(String geraeteID) {
        int sid;
        LoginReplyDaten reply = new LoginReplyDaten();

        // Rufe Funktion aus der Fachlogik auf
        try {
            sid = spielerSvc.spielerLogin(geraeteID);
        } catch (Exception e) {
            logger.error("SpielerRestController: " + e.getMessage());
            return ResponseEntity.status(HttpStatusCode.valueOf(500)).build();
        }


        if (sid > 0) {
            // Authentifizierung erfolgreich, JWT-Token generieren
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("SPIELER"));
            Authentication authentication = new UsernamePasswordAuthenticationToken(geraeteID, null, authorities);
            String jwtToken = tokenService.generateToken(authentication);
            reply.setId(sid);
            reply.setToken(jwtToken);
            return ResponseEntity.ok(reply);
        } else if (sid == -1) { // Spieler blockiert
            return ResponseEntity.status(HttpStatusCode.valueOf(403)).build();
        } else {
            return ResponseEntity.status(HttpStatusCode.valueOf(401)).build();
        }
    }
}