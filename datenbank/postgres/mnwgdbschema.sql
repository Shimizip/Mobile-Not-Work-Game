BEGIN;

--DROP SCHEMA IF EXISTS syp CASCADE;
--CREATE SCHEMA syp;
SET SCHEMA 'syp';

CREATE TABLE Administrator (
    aid serial PRIMARY KEY,
    benutzername character varying(50),
    passwort character varying(64) -- SHA-256 Hash = 64 Zeichen
);

CREATE TABLE Spielperiode (
    spid serial PRIMARY KEY,
    vonDatum Date,
    bisDatum Date,
    sid integer REFERENCES Spieler(sid) -- Gewinner
);

CREATE TABLE PunktestandSpielperiode (
    scid serial PRIMARY KEY,
    summePunkte int,
    spid integer REFERENCES Spielperiode(spid),
    sid integer REFERENCES Spieler(sid)
);

CREATE TABLE Spieler (
    sid serial PRIMARY KEY,
    benutzername character varying(50),
    geraeteID character varying(50),
    einwilligungAGB bool,
    blockiert bool
);

ALTER TABLE PunktestandSpielperiode ADD COLUMN sid integer REFERENCES Spieler(sid);

CREATE TABLE Kartensegment (
    ksid serial PRIMARY KEY,
    mittelpunktLat numeric,
    mittelpunktLon numeric
);

CREATE TABLE Mobilfunkstandard (
    msid serial PRIMARY KEY,
    bezeichnung character varying(50)
);

CREATE TABLE Mobilfunkanbieter (
    mfid serial PRIMARY KEY,
    bezeichnung character varying(50)
);

CREATE TABLE Messung (
    mid serial PRIMARY KEY,
    punkte integer,
    rssi numeric,
    rtt numeric,
    snr numeric,
    sid integer REFERENCES Spieler(sid),
    ksid integer REFERENCES Kartensegment(ksid),
    spid integer REFERENCES Spielperiode(spid),
    msid integer REFERENCES Mobilfunkstandard(msid),
    mfid integer REFERENCES Mobilfunkanbieter(mfid)
);

ALTER TABLE Messung ADD CONSTRAINT fk_durchgefuehrt_von FOREIGN KEY (sid) REFERENCES Spieler(sid);
ALTER TABLE Messung ADD CONSTRAINT fk_gilt_fuer_periode FOREIGN KEY (spid) REFERENCES Spielperiode(spid);
ALTER TABLE Messung ADD CONSTRAINT fk_gilt_fuer_kartensegment FOREIGN KEY (ksid) REFERENCES Kartensegment(ksid);
ALTER TABLE Messung ADD CONSTRAINT fk_wird_festgestellt_durch_ms FOREIGN KEY (msid) REFERENCES Mobilfunkstandard(msid);
ALTER TABLE Messung ADD CONSTRAINT fk_wird_festgestellt_durch_mf FOREIGN KEY (mfid) REFERENCES Mobilfunkanbieter(mfid);

ALTER TABLE PunktestandSpielperiode ADD CONSTRAINT fk_gilt_fuer_spielperiode FOREIGN KEY (spid) REFERENCES Spielperiode(spid);
ALTER TABLE PunktestandSpielperiode ADD CONSTRAINT fk_hat_erreicht_punkte FOREIGN KEY (sid) REFERENCES Spieler(sid);

ALTER TABLE Spielperiode ADD CONSTRAINT fk_spieler_gewinnt FOREIGN KEY (sid) REFERENCES Spieler(sid);


-- benoetigte werte einfuegen
INSERT INTO Mobilfunkstandard (msid, bezeichnung) VALUES (1, 'Kein Netz');
INSERT INTO Mobilfunkstandard (msid, bezeichnung) VALUES (2, 'GPRS');
INSERT INTO Mobilfunkstandard (msid, bezeichnung) VALUES (3, 'EDGE (2G)');
INSERT INTO Mobilfunkstandard (msid, bezeichnung) VALUES (4, 'LTE (4G)');
INSERT INTO Mobilfunkstandard (msid, bezeichnung) VALUES (5, '5G');

INSERT INTO Mobilfunkanbieter (mfid, bezeichnung) VALUES (1, 'Telekom (D1)');
INSERT INTO Mobilfunkanbieter (mfid, bezeichnung) VALUES (2, 'Vodafone (D2)');
INSERT INTO Mobilfunkanbieter (mfid, bezeichnung) VALUES (3, 'E-Netz');

SELECT * FROM Spieler;
SELECT * FROM Administrator;
SELECT * FROM Messung;
SELECT * FROM Kartensegment;
SELECT * FROM Mobilfunkstandard;
SELECT * FROM Mobilfunkanbieter;
SELECT * FROM Spielperiode;
SELECT * FROM PunktestandSpielperiode;


COMMIT;
