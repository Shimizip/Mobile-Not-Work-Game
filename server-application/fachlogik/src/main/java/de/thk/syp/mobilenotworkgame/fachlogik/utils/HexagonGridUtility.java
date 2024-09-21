package de.thk.syp.mobilenotworkgame.fachlogik.utils;

import org.geotools.referencing.GeodeticCalculator;

import java.awt.geom.Point2D;

import static java.lang.Math.sqrt;

/*
 * Credits an https://www.redblobgames.com/grids/hexagons/#conversions für die Logik und die Funktionen für die
 * Hex Grid Berechnung!
 *
 */
public final class HexagonGridUtility {
    private static final double HEX_RADIUS_INKREIS = 100; // 100m // r
    private static final double HEX_RADIUS_UMKREIS = (HEX_RADIUS_INKREIS / Math.cos(Math.PI / 6)); // Umkreis auf dem die Punkte liegen // R
    private static final double HEX_SIZE = HEX_RADIUS_UMKREIS;
    private static final Point2D REFERENCE_POINT = new Point2D.Double(6.958281, 50.941278); // Kölner Dom

    // Privater Konstruktor, um die Instanziierung zu verhindern
    private HexagonGridUtility() {}

    public static Point2D calculateHexagonCoordinatesInHexGrid(Point2D spielerPosition) {
        // Berechnung der Distanz und Richtung vom Referenzpunkt zur Spielerposition
        GeodeticCalculator calc = new GeodeticCalculator();
        calc.setStartingGeographicPoint(REFERENCE_POINT.getX(), REFERENCE_POINT.getY());
        calc.setDestinationGeographicPoint(spielerPosition.getX(), spielerPosition.getY());

        double distance = calc.getOrthodromicDistance();
        double azimuth = Math.toRadians(calc.getAzimuth()) - (Math.PI / 2); // +90 Grad = (- (Math.PI / 2)), da der Azimuth von Norden aus geht, aber das HexGrid vom Osten aus

        // Umrechnung in Vektoren
        double deltaX = distance * Math.cos(azimuth);
        double deltaY = distance * Math.sin(azimuth);

        // Umrechnung der Vektoren in fraktionale Hexagon-Koordinaten
        Point2D fractionalHex = cartesianToFractionalHex(deltaX, deltaY);

        // Runden zu ganzzahligen Hexagon-Koordinaten
        Point2D axialHex =  axialRound(fractionalHex);

        // Zurück Umrechnen in Double Height Koordinaten
        return axialToDoubleHeight(axialHex);
    }

    // Funktion übernommen von https://www.redblobgames.com/grids/hexagons/#conversions
    private static Point2D cartesianToFractionalHex(double deltaX, double deltaY) {
        double q = (2.0 / 3 * deltaX) / HEX_SIZE;
        double r = (-1.0 / 3 * deltaX + sqrt(3) / 3 * deltaY) / HEX_SIZE;
        return new Point2D.Double(q, r);
    }

    // Schritte aus https://www.redblobgames.com/grids/hexagons/#conversions
    private static Point2D axialRound(Point2D axialHex) {
        // Schritt 1: Umwandlung von Axial-Koordinaten in Cube-Koordinaten
        Cube cubeHex = axialToCube(axialHex);

        // Schritt 2: Anwendung des cube_round-Algorithmus
        Cube roundedCubeHex = cubeRound(cubeHex);

        // Schritt 3: Rückumwandlung der Cube-Koordinaten in Axial-Koordinaten
        Point2D roundedAxialHex = cubeToAxial(roundedCubeHex);

        // Schritt 5: Rückumwandlung der Axial-Koordinaten in Double-Height-Koordinaten
        return roundedAxialHex;
    }

    // Funktion übernommen von https://www.redblobgames.com/grids/hexagons/#conversions
    private static Cube axialToCube(Point2D hex) {
        double q = hex.getX();
        double r = hex.getY();
        double s = -q - r;
        return new Cube(q, r, s);
    }

    // Funktion übernommen von https://www.redblobgames.com/grids/hexagons/#conversions
    private static Cube cubeRound(Cube frac) {
        double q = Math.round(frac.q);
        double r = Math.round(frac.r);
        double s = Math.round(frac.s);

        double qDiff = Math.abs(q - frac.q);
        double rDiff = Math.abs(r - frac.r);
        double sDiff = Math.abs(s - frac.s);

        if (qDiff > rDiff && qDiff > sDiff) {
            q = -r - s;
        } else if (rDiff > sDiff) {
            r = -q - s;
        } else {
            s = -q - r;
        }

        return new Cube(q, r, s);
    }

    // Funktion übernommen von https://www.redblobgames.com/grids/hexagons/#conversions
    private static Point2D cubeToAxial(Cube cube) {
        double q = cube.q;
        double r = cube.r;
        return new Point2D.Double(q, r);
    }

    // Funktion übernommen von https://www.redblobgames.com/grids/hexagons/#conversions
    private static Point2D axialToDoubleHeight(Point2D hex) {
        double col = hex.getX();
        double row = 2 * hex.getY() + hex.getX();
        return new Point2D.Double(col, row);
    }

    public static Point2D hexagonCoordinatesToGeographic(Point2D hexCoord) {
        double col = hexCoord.getX();
        double row = hexCoord.getY();

        // Umwandlung der Hexagon-Gitterkoordinaten in kartesische Koordinaten
        // Formel übernommen von https://www.redblobgames.com/grids/hexagons/#conversions
        double x = HEX_SIZE * 3.0 / 2 * col;
        double y = HEX_SIZE * Math.sqrt(3) / 2 * row;

        // Umrechnung der kartesischen Koordinaten in geografische Koordinaten
        GeodeticCalculator calc = new GeodeticCalculator();
        calc.setStartingGeographicPoint(REFERENCE_POINT.getX(), REFERENCE_POINT.getY());

        double azimuth = Math.toDegrees(Math.atan2(y, x)) + 90; // +90 Grad, da der Azimuth von Norden aus geht, aber das HexGrid vom Osten aus
        double distance = Math.hypot(x, y);

        calc.setDirection(azimuth, distance);
        Point2D geoPoint = calc.getDestinationGeographicPoint();

        return new Point2D.Double(geoPoint.getX(), geoPoint.getY());
    }

    public static Point2D[] calculateCoordinatesOfHexCorners(Point2D hexCenterCoordinates) {
        Point2D[] hexCornerCoordinates = new Point2D[6];
        GeodeticCalculator calc = new GeodeticCalculator();

        double centerLatitude = hexCenterCoordinates.getY();
        double centerLongitude = hexCenterCoordinates.getX();

        // Winkel der inneren Dreiecke des Sechsecks
        // Reihenfolge bestimmt die Reihenfolge der Punkte (so: beginnend links oben im Uhrzeigersinn)
        // Winkel bestimmen die Drehung, normal wären 0, 60, 120, 180, 240, 300
        //double[] angles = new double[]{210, 270, 330, 30, 90, 150};
        double[] angles = new double[]{330, 30, 90, 150, 210, 270};

        for (int i = 0; i < 6; i++) {
            calc.setStartingGeographicPoint(centerLongitude, centerLatitude);
            calc.setDirection(angles[i], HEX_RADIUS_UMKREIS);
            Point2D cornerPoint = calc.getDestinationGeographicPoint();
            hexCornerCoordinates[i] = cornerPoint;
        }

        return hexCornerCoordinates;
    }

    // Hilfsklasse für Cube-Koordinaten
    private static class Cube {
        public final double q;
        public final double r;
        public final double s;

        public Cube(double q, double r, double s) {
            this.q = q;
            this.r = r;
            this.s = s;
        }
    }
}