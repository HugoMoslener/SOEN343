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
    const [role, setRole] = useState("rider"); // "rider" or "operator"
    const [reservedBike, setReservedBike] = useState(null);
    const [movingBike, setMovingBike] = useState(null);
    const [consoleMessages, setConsoleMessages] = useState([]);

    useEffect(() => {
        fetch("/api/create/getAllStations")
            .then((r) => r.json())
            .then(setStations)
            .catch((e) => console.error("Error fetching stations:", e));
    }, []);

    const logMessage = (msg) => setConsoleMessages((prev) => [msg, ...prev]);

    // Rider actions
    const handleReserve = (bike, stationID) => {
        if (reservedBike) {
            alert("You already have a reserved bike!");
            return;
        }
        setReservedBike({ ...bike, stationID });
        logMessage(`Reserved bike ${bike.bikeID} from station ${stationID}`);
    };

    const handleCheckout = () => {
        if (!reservedBike) {
            alert("No reserved bike to checkout!");
            return;
        }
        logMessage(`Checked out bike ${reservedBike.bikeID}`);
        setReservedBike((prev) => ({ ...prev, checkedOut: true }));
    };

    const handleReturn = (station) => {
        if (!reservedBike || !reservedBike.checkedOut) {
            alert("No bike to return!");
            return;
        }
        if (station.freeDocks === 0) {
            logMessage(`Cannot return to ${station.name}: no free docks.`);
            return;
        }
        logMessage(`Returned bike ${reservedBike.bikeID} to ${station.name}`);

        setStations((prev) =>
            prev.map((s) =>
                s.stationID === station.stationID
                    ? { ...s, bikesAvailable: s.bikesAvailable + 1, freeDocks: s.freeDocks - 1 }
                    : s
            )
        );

        setReservedBike(null);
    };

    // Operator actions
    const handleMoveStart = (bike, fromStation) => {
        setMovingBike({ ...bike, fromStation });
        logMessage(`Operator moving bike ${bike.bikeID} from ${fromStation}`);
    };

    const handleMoveComplete = (toStation) => {
        if (!movingBike) return;

        if (movingBike.fromStation === toStation.stationID) {
            logMessage("Cannot move bike to same station!");
            return;
        }

        if (toStation.freeDocks === 0) {
            logMessage(`Cannot move bike to ${toStation.name}: no free docks.`);
            return;
        }

        logMessage(`Moved bike ${movingBike.bikeID} → ${toStation.name}`);

        setStations((prev) =>
            prev.map((s) => {
                if (s.stationID === movingBike.fromStation)
                    return { ...s, bikesAvailable: s.bikesAvailable - 1, freeDocks: s.freeDocks + 1 };
                if (s.stationID === toStation.stationID)
                    return { ...s, bikesAvailable: s.bikesAvailable + 1, freeDocks: s.freeDocks - 1 };
                return s;
            })
        );

        setMovingBike(null);
    };

    return (
        <div className="flex flex-col gap-4 p-4">
            <h1 className="text-2xl font-bold text-center">Bike Sharing Dashboard</h1>

            {/* Role Switch */}
            <div className="flex justify-center gap-3 mb-2">
                <span className="font-semibold">Role:</span>
                <button
                    onClick={() => setRole("rider")}
                    className={`px-3 py-1 rounded ${role === "rider" ? "bg-blue-600 text-white" : "bg-gray-200"}`}
                >
                    Rider
                </button>
                <button
                    onClick={() => setRole("operator")}
                    className={`px-3 py-1 rounded ${role === "operator" ? "bg-orange-600 text-white" : "bg-gray-200"}`}
                >
                    Operator
                </button>
            </div>

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
                                                        {role === "rider" && !reservedBike && (
                                                            <button
                                                                onClick={() => handleReserve(dock.bike, station.stationID)}
                                                                className="bg-blue-500 text-white px-2 py-1 mt-1 rounded text-xs"
                                                            >
                                                                Reserve
                                                            </button>
                                                        )}
                                                        {role === "operator" && (
                                                            <button
                                                                onClick={() => handleMoveStart(dock.bike, station.stationID)}
                                                                className="bg-orange-500 text-white px-2 py-1 mt-1 rounded text-xs"
                                                            >
                                                                Move
                                                            </button>
                                                        )}
                                                    </>
                                                ) : (
                                                    <p className="italic text-gray-500 text-sm">Empty</p>
                                                )}
                                            </div>
                                        ))}
                                    </div>

                                    {role === "rider" && reservedBike && (
                                        <>
                                            {!reservedBike.checkedOut ? (
                                                <button
                                                    onClick={handleCheckout}
                                                    className="mt-2 bg-green-600 text-white px-3 py-1 rounded"
                                                >
                                                    Checkout
                                                </button>
                                            ) : (
                                                <button
                                                    onClick={() => handleReturn(station)}
                                                    className="mt-2 bg-purple-600 text-white px-3 py-1 rounded"
                                                >
                                                    Return
                                                </button>
                                            )}
                                        </>
                                    )}

                                    {role === "operator" && movingBike && (
                                        <button
                                            onClick={() => handleMoveComplete(station)}
                                            className="mt-2 bg-green-700 text-white px-3 py-1 rounded"
                                        >
                                            Move Here
                                        </button>
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
