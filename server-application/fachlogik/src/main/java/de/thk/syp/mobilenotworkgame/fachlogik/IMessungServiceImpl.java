package de.thk.syp.mobilenotworkgame.fachlogik;

import de.thk.syp.mobilenotworkgame.datenhaltungapi.*;
import de.thk.syp.mobilenotworkgame.fachlogikapi.IMessungService;
import de.thk.syp.mobilenotworkgame.mnwgdbmodel.entities.*;
import de.thk.syp.mobilenotworkgame.mnwgdbmodel.impl.IDatabaseImpl;
import de.thk.syp.mobilenotworkgame.mnwgdbmodel.services.IDatabase;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.EntityManager;
import java.lang.module.FindException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static de.thk.syp.mobilenotworkgame.fachlogik.utils.PunkteBerechnungUtility.punkteBerechnenFuerMessung;

public class IMessungServiceImpl implements IMessungService {
    Logger logger= LoggerFactory.getLogger("de.thk.mobilenotworkgame.fachlogik.IMessungServiceImpl");
    ICRUDMessung icrudMessung;
    ICRUDSpielperiode icrudSpielperiode;
    ICRUDSpieler icrudSpieler;
    ISpielerData iSpielerData;
    ICRUDKartensegment icrudKartensegment;
    IMobilfunkstandard iMobilfunkstandard;
    ICRUDMobilfunkanbieter icrudMobilfunkanbieter;
    ICRUDPunktestandSpielperiode icrudPsp;

    private EntityManager em;
    public IMessungServiceImpl() {
        logger.info("Starte IMessungServiceImpl");
        IDatabase db = new IDatabaseImpl();
        em = db.getEntityManager();
        if (em == null) {
            logger.info("EntityManager holen fehlgeschlagen");
            throw new EntityNotFoundException();
        }
        logger.info("IDatabase und em geholt.");


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

        // ISpielerData Implementierung ermitteln
        Iterator<ISpielerData> ispit = ServiceLoader.load(ISpielerData.class).iterator();
        if (ispit.hasNext()) {
            iSpielerData = ispit.next();
            iSpielerData.setEntityManager(em);
            logger.info("Implementierung fuer ISpielerData erfolgreich ermittelt.");
        } else {
            logger.error("Keine Implementierung fuer ISpielerData gefunden.");
            throw new FindException("Keine Implementierung fuer ISpielerData gefunden.");
        }

        // ICRUDSpieler Implementierung ermitteln
        Iterator<ICRUDSpieler> icrudsit = ServiceLoader.load(ICRUDSpieler.class).iterator();
        if (icrudsit.hasNext()) {
            icrudSpieler = icrudsit.next();
            icrudSpieler.setEntityManager(em);
            logger.info("Implementierung fuer ICRUDSpieler erfolgreich ermittelt.");
        } else {
            logger.error("Keine Implementierung fuer ICRUDSpieler gefunden.");
            throw new FindException("Keine Implementierung fuer ICRUDSpieler gefunden.");
        }

        // ICRUDKartensegment Implementierung ermitteln
        Iterator<ICRUDKartensegment> icrudkit = ServiceLoader.load(ICRUDKartensegment.class).iterator();
        if (icrudkit.hasNext()) {
            icrudKartensegment = icrudkit.next();
            icrudKartensegment.setEntityManager(em);
            logger.info("Implementierung fuer ICRUDKartensegment erfolgreich ermittelt.");
        } else {
            logger.error("Keine Implementierung fuer ICRUDKartensegment gefunden.");
            throw new FindException("Keine Implementierung fuer ICRUDKartensegment gefunden.");
        }

        // IMobilfunkstandard Implementierung ermitteln
        Iterator<IMobilfunkstandard> icrudmsit = ServiceLoader.load(IMobilfunkstandard.class).iterator();
        if (icrudmsit.hasNext()) {
            iMobilfunkstandard = icrudmsit.next();
            iMobilfunkstandard.setEntityManager(em);
            logger.info("Implementierung fuer IMobilfunkstandard erfolgreich ermittelt.");
        } else {
            logger.error("Keine Implementierung fuer IMobilfunkstandard gefunden.");
            throw new FindException("Keine Implementierung fuer IMobilfunkstandard gefunden.");
        }

        // ICRUDSpieler Implementierung ermitteln
        Iterator<ICRUDMobilfunkanbieter> icrudmfit = ServiceLoader.load(ICRUDMobilfunkanbieter.class).iterator();
        if (icrudmfit.hasNext()) {
            icrudMobilfunkanbieter = icrudmfit.next();
            icrudMobilfunkanbieter.setEntityManager(em);
            logger.info("Implementierung fuer ICRUDMobilfunkanbieter erfolgreich ermittelt.");
        } else {
            logger.error("Keine Implementierung fuer ICRUDMobilfunkanbieter gefunden.");
            throw new FindException("Keine Implementierung fuer ICRUDMobilfunkanbieter gefunden.");
        }

        // ICRUDPunktestandSpielperiode Implementierung ermitteln
        Iterator<ICRUDPunktestandSpielperiode> pspit = ServiceLoader.load(ICRUDPunktestandSpielperiode.class).iterator();
        if (pspit.hasNext()) {
            icrudPsp = pspit.next();
            icrudPsp.setEntityManager(em);
            logger.info("Implementierung fuer ICRUDPunktestandSpielperiode erfolgreich ermittelt.");
        } else {
            logger.error("Keine Implementierung fuer ICRUDPunktestandSpielperiode gefunden.");
            throw new FindException("Keine Implementierung fuer ICRUDPunktestandSpielperiode gefunden.");
        }
    }

    /*
     * Die Methode erfasst eine Messung und gibt die Anzahl der erreichten Punkte zurück
     */
    public int messungErfassen(int sid, int ksid, int msid, int mfid, int rssi, int rtt, int snr) {
        int punkte;
        boolean success = false;

        // Aktuelle Spielperiode ermitteln
        Spielperiode spielperiode = icrudSpielperiode.getSpielperiodeByDatum(new java.sql.Date(System.currentTimeMillis()));
        logger.info("Aktuelle Spielperiode: " + spielperiode.getSpid() + " Sid: " + sid + " Ksid: " + ksid);
        List<Messung> messungList;
        messungList = icrudMessung.getMessungenByKartensegmentAndSpielerForSpielperiode(ksid, sid, spielperiode.getSpid());

        // potenzielle Punkte für die Messung berechnen
        punkte = punkteBerechnenFuerMessung(msid, rssi, rtt, snr);

        // Wenn es bereits Messungen des Spielers für das Kartensegment gibt
        if (!messungList.isEmpty()) {
            // hoechste Messung des Spielers für das Kartensegment ermitteln
            Messung tmpMessung;
            Messung hoechsteMessung = null;
            Iterator<Messung> mit = messungList.iterator();
            while(mit.hasNext()) {
                tmpMessung = mit.next();
                if (hoechsteMessung == null || tmpMessung.getPunkte() > hoechsteMessung.getPunkte()) {
                    hoechsteMessung = tmpMessung;
                }
            }
            // Fallunterscheidung: Ist die neue Messung die mit der höchtsten Punktzahl?
            if (punkte > hoechsteMessung.getPunkte()) {
                // neue Messung ist besser (mehr Punkte), also werden die Punkte der vorherigen hoechsten Messung auf 0 gesetzt
                // die Punkte der neuen Messung gelten dann für das Kartensegment
                hoechsteMessung.setPunkte(0);
                em.getTransaction().begin();
                success = icrudMessung.editMessung(hoechsteMessung);
                if (success) {
                    em.getTransaction().commit();
                } else {
                    em.getTransaction().rollback();
                    return -1;
                }

            } else {
                // Eine vorherige Messung war besser (mehr Punkte), also wird die Punktzahl der neuen Messung auf 0 gesetzt
                punkte = 0;
            }
        }

        // neue Messung erstellen und Attribute setzen
        Spieler spieler = icrudSpieler.getSpielerByID(sid);
        Messung messung = new Messung();
        messung.setSpieler(spieler);
        messung.setKartensegment(icrudKartensegment.getKartensegmentById(ksid));
        messung.setMobilfunkanbieter(icrudMobilfunkanbieter.getMobilfunkanbieterById(mfid));
        messung.setMobilfunkstandard(iMobilfunkstandard.getMobilfunkstandardById(msid));
        messung.setRssi(rssi);
        messung.setRtt(rtt);
        messung.setSnr(snr);
        messung.setPunkte(punkte);
        messung.setSpielperiode(spielperiode);
        logger.info("Neue Messung: SID: " + sid + " KSID: " + ksid +" MSID: " + msid + " MFID: " + mfid + " RSSI: "  + rssi + " RTT: " + rtt + " SNR: " + snr + "Punkte: " + punkte);

        // Messung inserten
        em.getTransaction().begin();
        success = icrudMessung.insertMessung(messung);
        if (success) {
            em.getTransaction().commit();
        } else {
            em.getTransaction().rollback();
            return -1;
        }

        // PunktestandSpielperiode Objekt ermitteln und wenn noch keines existiert, neues erstellen
        Punktestandspielperiode psp = icrudPsp.getPunktestandSpielperiodeBySpidAndSid(spielperiode.getSpid(),sid);
        if (psp == null) {
            Punktestandspielperiode pspNeu = new Punktestandspielperiode();
            pspNeu.setSpieler(spieler);
            pspNeu.setSpielperiode(spielperiode);
            pspNeu.setSummepunkte(0);
            em.getTransaction().begin();
            success = icrudPsp.insertPunktestandSpielperiode(pspNeu);
            if (success) {
                em.getTransaction().commit();
            } else {
                em.getTransaction().rollback();
                return -1;
            }
            // Neu erstelltes Obj holen
            psp = icrudPsp.getPunktestandSpielperiodeBySpidAndSid(spielperiode.getSpid(),sid);
        }

        // Punkte in PunktestandSpielperiode hochzaehlen
        int summePunkte = icrudMessung.getSumPunkteForSpielerInSpielperiode(sid, spielperiode.getSpid());
        // Fehler bei der Ermittlung der Punkte
        if (summePunkte < 0) {
            return -1;
        }
        psp.setSummepunkte(summePunkte);

        em.getTransaction().begin();
        success = icrudPsp.editPunktestandSpielperiode(psp);
        // TA finalisieren
        if (success) {
            em.getTransaction().commit();
        } else {
            em.getTransaction().rollback();
            punkte = -1;
        }
        return punkte;
    }
}