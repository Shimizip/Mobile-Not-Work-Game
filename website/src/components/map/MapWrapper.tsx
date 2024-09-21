import React, { useEffect, useState, useRef } from "react";
import { TileLayer, MapContainer, useMap, Polygon, Marker, Popup } from "react-leaflet";
import L, { DivIcon, latLng } from "leaflet";
import { useLocation } from 'react-router-dom';

L.Marker.prototype.options.icon = L.icon({
  iconUrl: "https://unpkg.com/leaflet@1.7.1/dist/images/marker-icon.png",
});

const defaultCenter = { lat: 50.933659, lng: 6.980267 };

const polygonOptions = {
  color: "green",
  weight: 1,
};

interface MapWrapperProps {
  height: string;
  width: string;
}
interface HexagonType {
  ksid: string;
  coordinates: L.LatLngExpression[];
  mobilfunkstandard: string;
  mobilfunkanbieter: string;
  rssi: number;
  rtt: number;
  snr: number;
  erreichtePunkte: number;
}
type PlayerPosition = {
  lat: number;
  lng: number;
} | null;

interface BoundsLoggerProps {
  setHexagons: React.Dispatch<React.SetStateAction<HexagonType[]>>;
  filterAuswahl: string;
  filterValueAuswahl: string;
  boundsLoggerTrigger: number;
}

interface Window {
  receiveLocationFromAndroid: (latitude: number, longitude: number) => void;
  triggerBoundsLogger: () => void;
}

interface PlayerPositionMarkerProps {
  playerPosition: PlayerPosition;
}

const hexagonArray: HexagonType[] = [];

// Für Anzeige der Spieler Poition auf der Karte
function PlayerPositionMarker({ playerPosition }: PlayerPositionMarkerProps) {
  const map = useMap(); // Diese Hook wird nun innerhalb des Geltungsbereichs des MapContainer verwendet

  useEffect(() => {
      if (playerPosition) {
          // Kartenansicht auf Spieler Standort zentrieren
          map.setView(playerPosition, map.getZoom());
      }
  }, [playerPosition, map]);

  return playerPosition ? (
      <Marker 
          position={playerPosition}
          icon={new DivIcon({
              className: 'custom-player-icon',
              html: '<span class="material-symbols-outlined">boy</span>',
              iconSize: [60, 60],
              iconAnchor: [0, 30] 
          })}
      />
  ) : null;
}

const MapWrapper: React.FC<MapWrapperProps> = ({ height, width }) => {
  const [hexagons, setHexagons] = useState<HexagonType[]>([]);

  // Zustand für das Triggern von boundsLogger
  const [boundsLoggerTrigger, setBoundsLoggerTrigger] = useState(0);

  // Funktion, um den boundsLoggerTrigger Zustand zu aktualisieren
  const triggerBoundsLogger = () => {
    setBoundsLoggerTrigger(prev => prev + 1);
  };

  // Default-Werte Filter Menü
  const [isFilterVisible, setIsFilterVisible] = useState(false);
  const [isValueSelectionDisabled, setIsValueSelectionDisabled] = useState(true);
  const [filterAuswahl, setFilterAuswahl] = useState("default");
  const [filterValueAuswahl, setFilterValueAuswahl] = useState("0");

  // Default-Wert für Spieler Standort
  const [playerPosition, setPlayerPosition] = useState<PlayerPosition>(null);
  const receiveLocationFromAndroidRef = useRef((latitude: number, longitude: number) => {
    setPlayerPosition({ lat: latitude, lng: longitude });
  });

  const [filterOptions, setFilterOptions] = useState([
    { label: "Höchste Messung", value: "default", isValueSelectionDisabled: true },
    { label: "Mobilfunkstandard", value: "msid", isValueSelectionDisabled: false },
    { label: "RSSI", value: "rssi", isValueSelectionDisabled: true },
    { label: "RTT", value: "rtt", isValueSelectionDisabled: true },
    { label: "SNR", value: "snr", isValueSelectionDisabled: true },
  ]);
  const [mobilfunkstandardOptions, setMobilfunkstandardOptions] = useState([
    { label: "Kein Netz", value: "1" },
    { label: "GPRS", value: "2" },
    { label: "2G", value: "3" },
    { label: "4G", value: "4" },
    { label: "5G", value: "5" },
  ]);

  // Funktion, um die Sichtbarkeit des Filter-Menüs umzuschalten
  const toggleFilterVisibility = () => {
    setIsFilterVisible(!isFilterVisible);
  };

  // Farbe des Hexagons bestimmen
  const getPolygonColor = (mobilfunkstandard: string) => {
    // Solarized Color Theme
    switch(mobilfunkstandard) {
      case "Kein Netz": return "#dc322f";
        break;
      case "GPRS": return "#cb4b16";
        break;
      case "EDGE (2G)": return "#b58900";
        break;
      case "LTE (4G)": return "#859900";
        break;
      case "5G": return "#2aa198";
        break;
      default: return "gray";
    }
  };

   // Transparenz des Hexagons bestimmen
   const getPolygonFillOpacity = (snr: number) => {
    if (snr <= 5) {
      return 0.2;
    } else if (snr <= 15) {
      return 0.3;
    } else if (snr <= 20) {
      return 0.45;
    } else if (snr <= 30) {
      return 0.6;
    } else {
      return 0.6; // Default-Wert, falls SNR > 30
    }
  };

  useEffect(() => {
    // Methode dem window-Objekt hinzufügen
    (window as any).receiveLocationFromAndroid = receiveLocationFromAndroidRef.current;
    (window as any).triggerBoundsLogger = triggerBoundsLogger;
  }, []);


  // Map Provider
  // Original: url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
  // Stadia: url="https://tiles.stadiamaps.com/tiles/stamen_toner/{z}/{x}/{y}.png"
  return (
    <MapContainer
    style={{ height: height, width: width }}
    center={playerPosition || defaultCenter}
    zoom={15.0}
    zoomSnap={0.5}
    dragging={true}
    tap={true}
  >
    <TileLayer
      attribution='<a href="https://www.maptiler.com/copyright/" target="_blank">© MapTiler</a> <a href="https://www.openstreetmap.org/copyright" target="_blank">© OpenStreetMap contributors</a>'
      url="https://api.maptiler.com/maps/bright-v2/{z}/{x}/{y}.png?key=KEY"
    />

    {hexagons.map((hexagonArray, index) => (
      <Polygon
        key={`${hexagonArray.ksid}+${hexagonArray.erreichtePunkte}`} // Für die Statemachine
        positions={hexagonArray.coordinates}
        pathOptions={{color : getPolygonColor(hexagonArray.mobilfunkstandard), weight: 1}}
        fill={true}
        fillOpacity={getPolygonFillOpacity(hexagonArray.snr)}
      >
        <Popup>
          <div>
            <span className="material-symbols-outlined">signal_cellular_alt</span> {hexagonArray.mobilfunkstandard} 
            <br />
            <span className="material-symbols-outlined">sim_card</span> {hexagonArray.mobilfunkanbieter}
            <br />
            <span className="material-symbols-outlined">toll</span> {hexagonArray.erreichtePunkte} Punkte
            <br />
            <br />
            <strong>RSSI:</strong> {hexagonArray.rssi} dBm
            <br />
            <strong>RTT:</strong> {hexagonArray.rtt} ms
            <br />
            <strong>SNR:</strong> {hexagonArray.snr} dB
            <br />
          </div>
        </Popup>
      </Polygon>
    ))}

<link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@24,400,0,0" />
<PlayerPositionMarker playerPosition={playerPosition} />

  {/* Button zum Umschalten des Filter-Menüs */}
  <button onClick={toggleFilterVisibility} style={{ position: 'absolute', top: '100px', left: '10px', zIndex: 1000, cursor: 'pointer', borderRadius: '8px' }}>
        <span className="material-symbols-outlined">filter_alt</span>
  </button>

<div style={{ position: 'absolute', top: '70px', left: '10px', zIndex: 1000 }}>
        {/* Filter-Menü, nur sichtbar, wenn isFilterVisible true ist */}
      {isFilterVisible && (
        <div style={{ position: 'absolute', top: '0px', left: '40px', zIndex: 1000, backgroundColor: '#f9f9f9', padding: '20px', borderRadius: '8px', boxShadow: '0 0 10px rgba(0, 0, 0, 0.1)' }}>
          <div style={{ marginBottom: '10px', fontSize: '20px', fontWeight: 'bold', textAlign: 'center' }}>Filter</div>
        
            <div>
              <select value={filterAuswahl} onChange={(e) => {
                const selectedOption = filterOptions.find(option => option.value === e.target.value);
                setFilterAuswahl(e.target.value);
                setIsValueSelectionDisabled(selectedOption ? selectedOption.isValueSelectionDisabled : true);
                if(selectedOption && selectedOption.value === "msid") {
                  setFilterValueAuswahl(mobilfunkstandardOptions[0].value);
                } else {
                  setFilterValueAuswahl("0");
                }
              }} style={{ fontSize: '16px', padding: '5px', marginTop: '10px', opacity: 0.6, borderRadius: '10px' }}>
                {filterOptions.map(option => (
                  <option key={option.value} value={option.value}>{option.label}</option>
                ))}
              </select>
            </div>
            <div style={{ display: isValueSelectionDisabled ? 'none' : 'block' }}>
              <select value={filterValueAuswahl} onChange={(e) => setFilterValueAuswahl(e.target.value)} style={{ fontSize: '16px', padding: '5px', marginTop: '10px', opacity: 0.6, borderRadius: '10px' }}>
                {filterAuswahl === "msid" ? mobilfunkstandardOptions.map(option => (
                  <option key={option.value} value={option.value}>{option.label}</option>
                )) : null}
              </select>
            </div>
          </div>)}
        </div>


    <BoundsLogger 
      setHexagons={setHexagons} 
      filterAuswahl={filterAuswahl} 
      filterValueAuswahl={filterValueAuswahl}
      boundsLoggerTrigger={boundsLoggerTrigger}
    />
  </MapContainer>
);
};

const BoundsLogger: React.FC<BoundsLoggerProps> = ({ setHexagons, filterAuswahl, filterValueAuswahl, boundsLoggerTrigger }) => {
  const map = useMap();

  useEffect(() => {
    const handleMoveEnd = () => {
      boundsLogger(map, setHexagons, filterAuswahl, filterValueAuswahl);
    };

    if (map) {
      map.on("moveend", handleMoveEnd);
    }
    
    // Aufrufen des boundsLogger, wenn sich Filter Werte oder boundsLoggerTrigger ändern (Trigger durch Android)
    boundsLogger(map, setHexagons, filterAuswahl, filterValueAuswahl);

    return () => {
      if (map) {
        map.off("moveend", handleMoveEnd);
      }
    };
  }, [map, setHexagons, filterAuswahl, filterValueAuswahl, boundsLoggerTrigger]);

  return null;
};

// URL Params auslesen, um Token und SID zu ermitteln
const urlParams = new URLSearchParams(window.location.search);
const playerSid = urlParams.get('sid');
const playerToken = urlParams.get('token');

const boundsLogger = (
  map: L.Map,
  setHexagons: React.Dispatch<React.SetStateAction<HexagonType[]>>,
  filterAuswahl: string,
  filterValueAuswahl: string
) => {
  let bounds = map.getBounds();

  let northWest = bounds.getNorthWest();
  let northEast = bounds.getNorthEast();
  let southEast = bounds.getSouthEast();
  let southWest = bounds.getSouthWest();

  // Daten sollen für einen etwas größeren Bereich geladen werden für eine bessere User Experience
  const expansionFactor = 0.01;
  const linksObenLat = northWest.lat + expansionFactor;
  const linksObenLon = northWest.lng - expansionFactor;
  const rechtsObenLat = northEast.lat + expansionFactor;
  const rechtsObenLon = northEast.lng + expansionFactor;
  const rechtsUntenLat = southEast.lat - expansionFactor;
  const rechtsUntenLon = southEast.lng + expansionFactor;
  const linksUntenLat = southWest.lat - expansionFactor;
  const linksUntenLon = southWest.lng - expansionFactor;

  let jsonResponse;

  const apiUrl = `http://IP:PORT/karte/get-kartenbereich-daten/spieler-ansicht?
linksObenLat=${linksObenLat}
&linksObenLon=${linksObenLon}
&rechtsObenLat=${rechtsObenLat}
&rechtsObenLon=${rechtsObenLon}
&rechtsUntenLat=${rechtsUntenLat}
&rechtsUntenLon=${rechtsUntenLon}
&linksUntenLat=${linksUntenLat}
&linksUntenLon=${linksUntenLon}
&sid=${playerSid}
&filter=${filterAuswahl}
&filterValue=${filterValueAuswahl}`;

  fetch(apiUrl,{
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${playerToken}`,
    },
  })
  .then(response => {
    if (!response.ok) {
      throw new Error(`HTTP-Fehler! Status: ${response.status}`);
    }
    return response.json();
  })
  .then(data => {
    // Verarbeiten der Antwort
    jsonResponse = data;
    // JSON-Antwort in JavaScript-Objekt umwandeln
    const hexagonArrayBuffer = [];

    // Iteriere über jedes Hexagon
    for (const hexagon of jsonResponse) {

      // Array für die Koordinaten des aktuellen Hexagons
      const hexagonCoordinates : L.LatLngExpression[] = [];
      const punkte: { [key: string]: { latitude: number; longitude: number } } = hexagon.punkte;

      // Iteriere über die sechs Punkte jedes Hexagons
      for (const punktKey in punkte) {
        const punkt = punkte[punktKey];
        hexagonCoordinates.push([punkt.latitude, punkt.longitude]);
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
      

      hexagonArrayBuffer.push(hexagonObject);
    }
    
    setHexagons(hexagonArrayBuffer);
  })
  .catch(error => {
    console.error('Fetch-Fehler:', error);
  });
};
export default MapWrapper;
