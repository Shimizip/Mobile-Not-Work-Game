package de.thk.syp.mobilenotworkgame.restapi.controller;

import de.thk.syp.mobilenotworkgame.fachlogikapi.IAdminService;
import de.thk.syp.mobilenotworkgame.fachlogikapi.ISpielerService;
import de.thk.syp.mobilenotworkgame.restapi.AdminRestControllerApi;
import de.thk.syp.mobilenotworkgame.restapi.service.TokenService;
import de.thk.syp.mobilenotworkgame.restapi.swagger.model.LoginReplyDaten;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.lang.module.FindException;
import java.util.*;

@RestController
//@CrossOrigin(origins= {"http://172.17.0.3:80", "http://localhost:3000", "http://IP:PORT", "http://DOMAIN:PORT", "http://IP:PORT", "http://DOMAIN:PORT"})
public class AdminRestControllerImpl implements AdminRestControllerApi {
    Logger logger= LoggerFactory.getLogger("de.thk.mobilenotworkgame.fachlogikapi.SpielerRestControllerImpl");
    IAdminService adminSvc;
    private final TokenService tokenService;

    public AdminRestControllerImpl(TokenService tokenService) {
        logger.info("Starte SpielerRestControllerImpl");
        this.tokenService = tokenService;
        Iterator<IAdminService> it = ServiceLoader.load(IAdminService.class).iterator();
        if (it.hasNext()) {
            adminSvc = it.next();
            logger.info("Implementierung fuer IAdminService erfolgreich ermittelt.");
        } else {
            logger.error("Keine Implementierung fuer IAdminService gefunden.");
            throw new FindException("Keine Implementierung fuer IAdminService gefunden.");
        }
    }
    @Override
    public ResponseEntity<LoginReplyDaten> adminLogin(@NotNull String benutzername, @NotNull String passwort) {
        int aid;
        LoginReplyDaten reply = new LoginReplyDaten();

        // Rufe Funktion aus der Fachlogik auf
        try {
            aid = adminSvc.adminLogin(benutzername, passwort);
        } catch (Exception e) {
            logger.error("AdminRestController: " + e.getMessage());
            return ResponseEntity.status(HttpStatusCode.valueOf(500)).build();
        }

        if (aid > 0) {
            // Authentifizierung erfolgreich, JWT-Token generieren
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ADMIN"));
            Authentication authentication = new UsernamePasswordAuthenticationToken(benutzername, passwort, authorities);
            String jwtToken = tokenService.generateToken(authentication);
            reply.setId(aid);
            reply.setToken(jwtToken);
            return ResponseEntity.ok(reply);
        } else {
            return ResponseEntity.status(HttpStatusCode.valueOf(401)).build();
        }
    }
}