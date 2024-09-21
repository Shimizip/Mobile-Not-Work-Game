package de.thk.syp.mobilenotworkgame.fachlogik;

import de.thk.syp.mobilenotworkgame.datenhaltungapi.IAdministrator;
import de.thk.syp.mobilenotworkgame.fachlogikapi.IAdminService;
import de.thk.syp.mobilenotworkgame.mnwgdbmodel.entities.Administrator;
import de.thk.syp.mobilenotworkgame.fachlogikapi.grenz.AdministratorGrenz;
import de.thk.syp.mobilenotworkgame.mnwgdbmodel.impl.IDatabaseImpl;
import de.thk.syp.mobilenotworkgame.mnwgdbmodel.services.IDatabase;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.module.FindException;
import java.util.Iterator;
import java.util.ServiceLoader;
import org.apache.commons.codec.digest.DigestUtils;

public class IAdminServiceImpl implements IAdminService {
    Logger logger= LoggerFactory.getLogger("de.thk.syp.mobilenotworkgame.fachlogik.IKarteServiceImpl");
    IAdministrator iAdministrator;
    private static EntityManager em;
    public IAdminServiceImpl(){
        logger.info("Starte IKarteServiceImpl");
        IDatabase db = new IDatabaseImpl();
        em = db.getEntityManager();
        if (em == null) {
            logger.info("EntityManager holen fehlgeschlagen");
            throw new EntityNotFoundException();
        }
        logger.info("IDatabase und em geholt.");

        // IAdministrator Implementierung ermitteln
        Iterator<IAdministrator> it = ServiceLoader.load(IAdministrator.class).iterator();
        if (it.hasNext()) {
            iAdministrator = it.next();
            iAdministrator.setEntityManager(em);
            logger.info("Implementierung fuer IAdministrator erfolgreich ermittelt.");
        } else {
            logger.error("Keine Implementierung fuer IAdministrator gefunden.");
            throw new FindException("Keine Implementierung fuer IAdministrator gefunden.");
        }
    }
    /*
     * Die Methode ermittelt für eine Administrator-ID (aid) den Administrator
     */
    public AdministratorGrenz getAdminById(int aid) {
        Administrator adm = iAdministrator.getAdministratorById(aid);
        if(adm == null)
            return null;
        AdministratorGrenz administratorGrenz= new  AdministratorGrenz();
        administratorGrenz.setAid(aid);
        administratorGrenz.setBenutzername(adm.getBenutzername());
        administratorGrenz.setPasswort(adm.getPasswort());
        return administratorGrenz;
    }

    @Override
    public int adminLogin(String benutzername, String passwort) {
        Administrator admin = iAdministrator.getAdministratorByBenutzername(benutzername);

        // wenn kein matchender Administrator gefunden wurde
        if (admin == null) return -2;

        // Prüfen auf Korrektheit des Passwortes, dazu SHA-256 hashen und vergleichen
        String sha256hex = DigestUtils.sha256Hex(passwort);
        if (! sha256hex.equals(admin.getPasswort())) return -2;

        // Erfolgreiche Authentifizierung
        return admin.getAid();
    }
}