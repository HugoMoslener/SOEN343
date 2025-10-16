import React from 'react';
import {MapContainer, Marker, Popup, TileLayer, useMap} from 'react-leaflet';
import 'leaflet/dist/leaflet.css';
import L from "leaflet";
import markerIcon2x from "leaflet/dist/images/marker-icon-2x.png";
import markerIcon from "leaflet/dist/images/marker-icon.png";
import markerShadow from "leaflet/dist/images/marker-shadow.png";

// Fix Leaflet marker icons
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
    iconRetinaUrl: markerIcon2x,
    iconUrl: markerIcon,
    shadowUrl: markerShadow,
});

const Home = () => {
    const positions = [  // A query can fetch all dockStation from the backend and display them using a mapping function in React
      { id: 1, latitude: 45.5017, longitude: -73.5673, name: "Montreal" },
      { id: 2, latitude: 45.5120, longitude: -73.5540, name: "Toronto" },
      { id: 3, latitude: 45.5200, longitude: -73.5800, name: "New York" },
    ];
    const handleClick = () => {
        alert("Button inside popup clicked!");
    };
  return (
  <div>


    <MapContainer center={[45.5017,-73.5673]} zoom={11} scrollWheelZoom={true}  style={{ height: "500px", width: "100%" }}>
      <TileLayer
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
      />
       {positions.map((pos) => (
      <Marker position={[pos.latitude,pos.longitude]}>
        <Popup>
            <button
                onClick={handleClick}
                className="mt-2 px-3 py-1 bg-blue-600 text-white rounded hover:bg-blue-700"
            >
                Show Alert
            </button>
        </Popup>
      </Marker>))}
    </MapContainer>

     </div>
  );
};

export default Home;