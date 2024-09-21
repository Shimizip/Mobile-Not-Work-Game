package de.thk.syp.mobilenotworkgame.fachlogik.utils;

import java.util.Map;

public final class PunkteBerechnungUtility {
    // Basispunktzahl pro Netzwerktyp
    private static final Map<Integer, Integer> BASE_POINTS_BY_NETWORK = Map.of(
            1, 100, // Kein Netz
            2, 70,  // GPRS
            3, 40,  // EDGE
            4, 0,  // 4G
            5, 0    // 5G
    );

    // Konstanten definieren
    private static final int minRssi = -110; // schlechtestes Signal
    private static final int maxRssi = -30;  // bestes Signal
    private static final int minRtt = 0;     // bestes Signal
    private static final int maxRtt = 200;   // schlechtestes Signal
    private static final int minSnr = 0;    // schlechtestes Signal
    private static final int maxSnr = 40;    // bestes Signal

    // Privater Konstruktor, um die Instanziierung zu verhindern
    private PunkteBerechnungUtility() {}

    /*
     * Helper Funktion für die Berechnung der erreichten Punkte einer Messung
     */
    public static int punkteBerechnenFuerMessung(int msid, int rssi, int rtt, int snr) {
        // Bei Kein netz => 100 Punkte, bei 5G => 0 Punkte
        if (msid == 1) {
            return 100;
        } else if (msid == 5) {
            return 0;
        }

        // Eingabewerte begrenzen
        rssi = Math.max(minRssi, Math.min(rssi, maxRssi));
        rtt = Math.max(minRtt, Math.min(rtt, maxRtt));
        snr = Math.max(minSnr, Math.min(snr, maxSnr));

        // Signalqualitätskomponenten normalisieren
        double normRssi = (double) ((-1)*(rssi - maxRssi)) / (maxRssi - minRssi); // höher ist besser
        double normRtt = 1.0 - (double) (maxRtt - rtt) / (maxRtt - minRtt); // höher ist schlechter, daher 1.0 minus den Wert
        double normSnr = (double) (maxSnr - snr) / (maxSnr - minSnr); // höher ist besser

        // Signalqualität als gewichteten Durchschnitt der normalisierten Komponenten
        double signalQuality = (normRssi * 0.2 + normRtt * 0.3 + normSnr * 0.5);

        // Punkte für Signalqualität berechnen (zwischen 0 und 20 Punkte)
        int qualityPoints = (int) (signalQuality * 20);

        // Basispunkte für Netzwerktyp holen
        int basePoints = BASE_POINTS_BY_NETWORK.getOrDefault(msid, 0);

        // Endgültige Punktzahl als das Minimum von Basispunkten und Signalqualitätspunkten
        int finalPoints = basePoints + qualityPoints;

        return finalPoints;
    }
}
