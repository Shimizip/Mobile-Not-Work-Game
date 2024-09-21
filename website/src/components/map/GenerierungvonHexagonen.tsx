import React, { useEffect, useState } from "react";
import { TileLayer, MapContainer, useMap, Polygon } from "react-leaflet";
import L from "leaflet";

L.Marker.prototype.options.icon = L.icon({
  iconUrl: "https://unpkg.com/leaflet@1.7.1/dist/images/marker-icon.png",
});

const center = { lat: 50.933659, lng: 6.980267 };



const polygonOptions = {
  color: "red",
  weight: 1,
};

interface MapWrapperProps {
  height: string;
  width: string;
}

const MapWrapper: React.FC<MapWrapperProps> = ({ height, width }) => {
  const [polygons, setPolygons] = useState<[number, number][][]>([]);

  return (
    <MapContainer
      style={{ height: height, width: width }}
      center={center}
      zoom={12}
    >
      <TileLayer
        attribution='&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
      />

      {polygons.map((coordinates, index) => (
        <Polygon
          key={index}
          positions={coordinates}
          pathOptions={polygonOptions}
          fill={true}
          fillOpacity={0.5}
        />
      ))}
    </MapContainer>
  );
};

interface BoundsLoggerProps {
  setPolygons: React.Dispatch<React.SetStateAction<[number, number][][]>>;
}

const BoundsLogger: React.FC<BoundsLoggerProps> = ({ setPolygons }) => {
  const map = useMap();

  useEffect(() => {
    const handleMoveEnd = async () => {
      if (map) {
        const bounds = map.getBounds();
        const northEast = bounds.getNorthEast();
        const northWest = bounds.getNorthWest();
        const southEast = bounds.getNorthEast();
        const southWest = bounds.getSouthWest();

        const northWestLatitude = northWest.lat;
        const northWestLongitude = northWest.lng;

        const northEastLatitude = northEast.lat;
        const northEastLongitude = northEast.lng;

        const southWestLatitude = southWest.lat;
        const southWestLongitude = southWest.lng;

        const southEastLatitude = southWest.lat;
        const southEastLongitude = southWest.lng;


        // Führe einen GET-Request durch, um neue Koordinaten abzurufen
        try {
          const response = await fetch("DEIN_API_ENDPUNKT");
          const newCoordinates = await response.json();

          // Füge die neuen Koordinaten dem bestehenden Array von Polygonen hinzu
          setPolygons((prevPolygons) => [...prevPolygons, newCoordinates]);
        } catch (error) {
          console.error("Fehler beim Abrufen der Koordinaten:", error);
        }
      }
    };

    if (map) {
      map.on("moveend", handleMoveEnd);
    }

    return () => {
      if (map) {
        map.off("moveend", handleMoveEnd);
      }
    };
  }, [map, setPolygons]);

  return null;
};

export default MapWrapper;

/**
let jsonResponse;

const apiUrl = `/karte/get-kartenbereich-daten/spieler-ansicht?
linksObenLat=${linksObenLat}
&linksObenLon=${linksObenLon}
&rechtsObenLat=${rechtsObenLat}
&rechtsObenLon=${rechtsObenLon}
&rechtsUntenLat=${rechtsUntenLat}
&rechtsUntenLon=${rechtsUntenLon}
&linksUntenLat=${linksUntenLat}
&linksUntenLon=${linksUntenLon}`;

fetch(apiUrl)
  .then(response => {
    if (!response.ok) {
      throw new Error(`HTTP-Fehler! Status: ${response.status}`);
    }
    return response.json();
  })
  .then(data => {
    // Verarbeite die Antwort hier
    console.log(data);

    jsonResponse = data;

    // JSON-Antwort in JavaScript-Objekt umwandeln
    const hexagonArray = [];

    // Iteriere über jedes Hexagon
    for (const hexagon of data) {
      console.log(`KSID: ${hexagon.ksid}`);

      // Array für die Koordinaten des aktuellen Hexagons
      const hexagonCoordinates = [];

      // Iteriere über die sechs Punkte jedes Hexagons
      for (const punkt of hexagon.punkte) {
        const punktNummer = Object.keys(punkt)[0]; // Extrahiere die Punktnummer
        const koordinaten = punkt[punktNummer];

        console.log(`${punktNummer}: Breitengrad ${koordinaten.latitude}, Längengrad ${koordinaten.longitude}`);
      }

      const hexagonObject = {
        ksid: hexagon.ksid,
        coordinates: hexagonCoordinates,
        mobilfunkstandard: hexagon.mobilfunkstandardAsString,
        mobilfunkanbieter: hexagon.mobilfunkanbieterAsString,
        rssi: hexagon.rssi,
        rtt: hexagon.rtt,
        snr: hexagon.snr,
        erreichtePunkte: hexagon.erreichtePunkte
      };

      hexagonArray.push(hexagonObject);

      console.log('---');
    }

    console.log(hexagonArray);
  })
  .catch(error => {
    console.error('Fetch-Fehler:', error);
  });

Erwarteter jsonResponse in JavaScript-Objekt umwandeln

[
  {
    "ksid": 0,
    "punkte": [
      {"punkt1": {"latitude": 51.1657, "longitude": 10.4515}},
      {"punkt2": {"latitude": 51.1857, "longitude": 10.5515}},
      {"punkt3": {"latitude": 51.2057, "longitude": 10.4515}},
      {"punkt4": {"latitude": 51.2057, "longitude": 10.3515}},
      {"punkt5": {"latitude": 51.1857, "longitude": 10.2515}},
      {"punkt6": {"latitude": 51.1657, "longitude": 10.3515}}
    ],
    "mobilfunkstandardAsString": "string",
    "mobilfunkanbieterAsString": "string",
    "rssi": 0,
    "rtt": 0,
    "snr": 0,
    "erreichtePunkte": 0
  },
  {
    "ksid": 1,
    "punkte": [
      {"punkt1": {"latitude": 51.2257, "longitude": 10.6515}},
      {"punkt2": {"latitude": 51.2457, "longitude": 10.7515}},
      {"punkt3": {"latitude": 51.2657, "longitude": 10.6515}},
      {"punkt4": {"latitude": 51.2657, "longitude": 10.5515}},
      {"punkt5": {"latitude": 51.2457, "longitude": 10.4515}},
      {"punkt6": {"latitude": 51.2257, "longitude": 10.5515}}
    ],
    "mobilfunkstandardAsString": "string",
    "mobilfunkanbieterAsString": "string",
    "rssi": 0,
    "rtt": 0,
    "snr": 0,
    "erreichtePunkte": 0
  }
]
*/

/*
const hexagonArray = [
  {
    ksid: "1",
    coordinates: [
      [50.93443957881328,6.979549207770316],
      [50.93443957881328,6.980979420377523], 
      [50.933658991265766,6.981694526722329],
      [50.93287841681961,6.98097942046008],
      [50.93287841681961,6.979549207852286],
      [50.933658991265766,6.978834101507775]
    
    ],
    mobilfunkstandard: "4G",
    mobilfunkanbieter: "Telecom",
    rssi: -70,
    rtt: 30,
    snr: 20,
    erreichtePunkte: 100,
  },
  {
    ksid: "2",
    coordinates: [
      [50.93577857881317,6.983046187109955],
      [50.93577857881317,6.984476440896705], 
      [50.93499799126534,6.985191567831282],
      [50.9342174168195,6.984476440979261],
      [50.9342174168195,6.983046187191925],
      [50.93499799126534,6.982331060257641]
    
    ],
    mobilfunkstandard: "5G",
    mobilfunkanbieter: "Vodafone",
    rssi: -65,
    rtt: 25,
    snr: 22,
    erreichtePunkte: 120,
  },
  {
    ksid: "3",
    coordinates: [
      [50.93281657881341,6.985170232810598],
      [50.93281657881341,6.986600395508363], 
      [50.93203599126627,6.987315476898449],
      [50.93125541681973,6.98660039559092],
      [50.93125541681973,6.9851702328925676],
      [50.93203599126627,6.984455151502776]
    
    ],
    mobilfunkstandard: "3G",
    mobilfunkanbieter: "O2",
    rssi: -75,
    rtt: 35,
    snr: 18,
    erreichtePunkte: 90,
  },
];
*/

