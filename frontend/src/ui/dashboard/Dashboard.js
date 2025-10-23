import React, { useState, useEffect } from "react";
import { MapContainer, TileLayer, Marker, Popup } from "react-leaflet";
import L from "leaflet";
import "leaflet/dist/leaflet.css";
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

export default function Home() {
    const [stations, setStations] = useState([]);
    const [role, setRole] = useState(localStorage.getItem("role"));
    const [reservedBike, setReservedBike] = useState(null);
    const [movingBike, setMovingBike] = useState(null);
    const [consoleMessages, setConsoleMessages] = useState([]);
    const[isReserved, setIsReserved] = useState(false);
    const [isUndocking,setIsUndocked] = useState(false);
    const [isDocked,setIsDocked] = useState(false);
    const [reservationID, setReservationID] = useState("");
    const [count, setCount] = useState(0);

    useEffect(() => {
        fetch("/api/create/getAllStations")
            .then((r) => r.json())
            .then(setStations)
            .catch((e) => console.error("Error fetching stations:", e));
    }, [count]);

    const logMessage = (msg) => setConsoleMessages((prev) => [msg, ...prev]);


    const handleReserve = (bikeID, stationID) => {
        setIsReserved(true);
    };

    const handleCheckout = () => {
        if (!reservedBike) {
            alert("No reserved bike to checkout!");
            return;
        }
        setIsReserved(false);
        setIsUndocked(true);
        logMessage(`Checked out bike ${reservedBike.bikeID}`);
        setReservedBike((prev) => ({ ...prev, checkedOut: true }));
        setCount(count+1);
    };

    const handleReturn = (dockID) => {
        setIsReserved(false);
        setIsUndocked(false);
        setIsDocked(false);
        setReservedBike(null);
    };

    // Operator actions
    const handleMoveStart = (dockID, stationID) => {
    };

    const handleMoveComplete = (stationID) => {
        if (!movingBike) return;

        if (movingBike.stationID === stationID.stationID) {
            logMessage("Cannot move bike to same station!");
            return;
        }

        if (stationID.freeDocks === 0) {
            logMessage(`Cannot move bike to ${stationID.name}: no free docks.`);
            return;
        }

        logMessage(`Moved bike ${movingBike.bikeID} → ${stationID.name}`);

        setStations((prev) =>
            prev.map((s) => {
                if (s.stationID === movingBike.fromStation)
                    return { ...s, bikesAvailable: s.bikesAvailable - 1, freeDocks: s.freeDocks + 1 };
                if (s.stationID === stationID.stationID)
                    return { ...s, bikesAvailable: s.bikesAvailable + 1, freeDocks: s.freeDocks - 1 };
                return s;
            })
        );

        setMovingBike(null);
    };

    return (
        <div className="flex flex-col gap-4 p-4">
            <h1 className="text-2xl font-bold text-center">Bike Sharing Dashboard</h1>

            {/* Console */}
            <div className="p-2 bg-gray-50 border rounded h-24 overflow-y-auto text-xs">
                {consoleMessages.map((msg, idx) => (
                    <div key={idx}>• {msg}</div>
                ))}
            </div>

            {/* Map */}
            <div className="h-[600px] rounded overflow-hidden border">
                <MapContainer center={[45.5017, -73.5673]} zoom={13} style={{ height: "500px", width: "100%" }}>
                    <TileLayer
                        attribution='&copy; OpenStreetMap contributors'
                        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                    />

                    {stations.map((station) => (
                        <Marker key={station.stationID} position={[station.latitude, station.longitude]}>
                            <Popup>
                                <div style={{ maxHeight: "400px", overflowY: "auto", width: "300px" }}>
                                    <p><strong>{station.name}</strong></p>
                                    <p>{station.address}</p>
                                    <p>Bikes Available: {station.bikesAvailable}</p>
                                    <p>Free Docks: {station.freeDocks}</p>

                                    <div className="grid grid-cols-2 gap-2 mt-2 max-h-[150px] overflow-y-auto">
                                        {station.docks.map((dock) => (
                                            <div
                                                key={dock.dockID}
                                                className={`p-2 border rounded ${
                                                    dock.state === "OCCUPIED" ? "bg-green-100" : "bg-gray-100"
                                                }`}
                                            >
                                                <p><strong>{dock.dockID}</strong></p>
                                                <p>State: {dock.state}</p>
                                                {dock.bike ? (
                                                    <>
                                                        <p>{dock.bike.type}</p>
                                                        BikeID: <p><strong>{dock.bike.bikeID}</strong></p>
                                                        {role === "rider" && !isReserved && !isUndocking && (
                                                            <button
                                                                onClick={() => handleReserve(dock.bike.bikeID, station.stationID)}
                                                                className="bg-blue-500 text-white px-2 py-1 mt-1 rounded text-xs"
                                                            >
                                                                Reserve
                                                            </button>


                                                        )}

                                                        {role === "operator" && (
                                                            <button
                                                                onClick={() => handleMoveStart(dock.dockID, station.stationID)}
                                                                className="bg-orange-500 text-white px-2 py-1 mt-1 rounded text-xs"
                                                            >
                                                                Move
                                                            </button>
                                                        )}
                                                    </>
                                                ) : (
                                                    <>
                                                    <p className="italic text-gray-500 text-sm">Empty</p>
                                                { role === "rider" && isUndocking && dock.state === "EMPTY" && (
                                                    <button
                                                    onClick={() => handleReturn(dock.dockID)}
                                                className="bg-blue-500 text-white px-2 py-1 mt-1 rounded text-xs"
                                            >
                                                Return
                                            </button>)}
                                                        {role === "operator" && movingBike && dock.state === "EMPTY" && (
                                                            <button
                                                                onClick={() => handleMoveComplete(station)}
                                                                className="mt-2 bg-green-700 text-white px-3 py-1 rounded"
                                                            >
                                                                Move Here
                                                            </button>
                                                        )}
                                                    </>
                                                )}
                                            </div>
                                        ))}
                                    </div>

                                    {role === "rider" && isReserved && (
                                        <>
                                            {!isUndocking && (
                                                <button
                                                    onClick={handleCheckout}
                                                    className="mt-2 bg-green-600 text-white px-3 py-1 rounded"
                                                >
                                                    Checkout
                                                </button>
                                            )}
                                        </>
                                    )}


                                </div>
                            </Popup>
                        </Marker>
                    ))}
                </MapContainer>
            </div>
        </div>
    );
}
