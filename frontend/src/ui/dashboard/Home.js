import React, { useState, useEffect } from "react";
import { MapContainer, TileLayer, Marker, Popup } from "react-leaflet";
import L from "leaflet";
import "leaflet/dist/leaflet.css";
import markerIcon2x from "leaflet/dist/images/marker-icon-2x.png";
import markerIcon from "leaflet/dist/images/marker-icon.png";
import markerShadow from "leaflet/dist/images/marker-shadow.png";
import { authService } from '../../services/authService'; // Adjust path as needed

// Fix Leaflet marker icons
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
    iconRetinaUrl: markerIcon2x,
    iconUrl: markerIcon,
    shadowUrl: markerShadow,
});

export default function Home() {
    const [stations, setStations] = useState([]);
    const [user, setUser] = useState(null);
    const [role, setRole] = useState(null);
    const [userId, setUserId] = useState(null);
    const [reservedBike, setReservedBike] = useState(null);
    const [movingBike, setMovingBike] = useState(null);
    const [consoleMessages, setConsoleMessages] = useState([]);
    const [isReserved, setIsReserved] = useState(false);
    const [isUndocking, setIsUndocked] = useState(false);
    const [isMoving, setIsMoving] = useState(true);
    const [reservationID, setReservationID] = useState("");
    const [count, setCount] = useState(0);

    const fetchUserDetails = async (firebaseUser) => {
        try {
            const email = firebaseUser.email;
            const username = email.split('@')[0];

            console.log("Fetching user data for:", username);

            const response = await fetch("/api/signIn/getUserData", {
                method: "POST",
                headers: { "Content-Type": "text/plain" },
                body: username
            });

            if (response.ok) {
                const userData = await response.json();
                setUser(userData);
                setRole(userData.role);
                setUserId(userData.username);
                logMessage(`Welcome ${userData.fullName || userData.username} (${userData.role})`);
            } else {
                logMessage(`Failed to fetch user details for: ${username}`);
            }
        } catch (error) {
            logMessage(`Error: ${error.message}`);
        }
    };

    useEffect(() => {
        const unsubscribe = authService.onAuthStateChanged(async (firebaseUser) => {
            if (firebaseUser) {
                // User is logged in via Firebase
                await fetchUserDetails(firebaseUser);
            } else {
                // No user logged in
                logMessage("Please log in first");
            }
        });

        fetchStations();

        return () => unsubscribe();
    }, [count]);

    const fetchStations = () => {
        fetch("/api/create/getAllStations")
            .then((r) => r.json())
            .then(setStations)
            .catch((e) => console.error("Error fetching stations:", e));
    };

    const logMessage = (msg) => setConsoleMessages((prev) => [msg, ...prev]);

    const handleReserve = (bikeID, stationID, stationName) => {
        if (!userId || isReserved || role !== 'rider') {
            logMessage("Error: Must be a logged-in Rider without an active reservation.");
            return;
        }

        const reservationData = {
            riderID: userId,
            bikeID: bikeID,
            stationName: stationName,
        };

        logMessage(`Attempting to reserve bike ${bikeID} at ${stationName}...`);

        fetch("/api/action/reserveBike", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(reservationData),
        })
            .then((r) => r.text())
            .then((result) => {
                if (result && result !== "false") {
                    setIsReserved(true);
                    setReservationID(result);
                    setReservedBike({
                        bikeID: bikeID,
                        stationName: stationName,
                        reservationID: result,
                        checkedOut: false
                    });
                    logMessage(`Reserved bike ${bikeID}! ID: ${result}`);
                    setCount(c => c + 1);
                    fetchStations();
                } else {
                    logMessage(`❌ Reservation failed for bike ${bikeID}.`);
                }
            })
            .catch((e) => {
                logMessage(`❌ Network error during reservation: ${e.message}`);
            });
    };

    const handleCheckout = () => {
        if (!reservedBike) {
            alert("No reserved bike to checkout!");
            return;
        }

        const undockingData = {
            riderID: userId,
            reservationID: reservedBike.reservationID,
        };

        logMessage(`Attempting to checkout bike ${reservedBike.bikeID}...`);

        fetch("/api/action/undockBike", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(undockingData),
        })
            .then((r) => r.text())
            .then((message) => {
                if (message && message !== "false") {
                    setIsReserved(false);
                    setIsUndocked(true);
                    setReservedBike((prev) => ({ ...prev, checkedOut: true }));
                    logMessage(`Checked out bike ${reservedBike.bikeID}.`);
                    setCount(c => c + 1);
                    fetchStations();
                } else {
                    logMessage("Checkout failed. Bike may no longer be available.");
                }
            })
            .catch((e) => logMessage(`❌ Network error during checkout: ${e.message}`));
    };

    const handleReturn = (dockID, stationName) => {
        if (!reservedBike || !reservedBike.checkedOut) {
            logMessage("No bike currently checked out to return.");
            return;
        }

        const dockingData = {
            riderID: userId,
            reservationID: reservedBike.reservationID,
            dockID: dockID,
        };

        logMessage(`Attempting to return bike ${reservedBike.bikeID} to dock ${dockID} at ${stationName}...`);

        fetch("/api/action/dockBike", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(dockingData),
        })
            .then((r) => r.text())
            .then((message) => {
                if (message && message !== "false") {
                    setIsUndocked(false);
                    setIsReserved(false);
                    setReservedBike(null);
                    setReservationID("");
                    logMessage(`✅ Trip complete! Bike returned to dock ${dockID}. Trip Summary: ${message}`);
                    setCount(c => c + 1);
                    fetchStations();
                } else {
                    logMessage("❌ Return failed. Check if dock is free or station is active.");
                }
            })
            .catch((e) => logMessage(`❌ Network error during return: ${e.message}`));
    };

    const handleCancelReservation = () => {
        if (!isReserved || !reservedBike || reservedBike.checkedOut) {
            logMessage("No active reservation to cancel.");
            return;
        }

        const cancelData = {
            riderID: userId,
            reservationID: reservedBike.reservationID,
        };

        logMessage(`Attempting to cancel reservation ${reservedBike.reservationID}...`);

        fetch("/api/action/cancelReserveBike", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(cancelData),
        })
            .then((r) => r.text())
            .then((message) => {
                if (message && message !== "false" && !message.includes("Error")) {
                    setIsReserved(false);
                    setReservedBike(null);
                    setReservationID("");
                    logMessage(`✅ Reservation cancelled successfully.`);
                    setCount(c => c + 1);
                    fetchStations();
                } else {
                    logMessage(`❌ Reservation cancellation failed. ${message}`);
                }
            })
            .catch((e) => logMessage(`❌ Network error during cancellation: ${e.message}`));
    };

    // Operator actions
    const handleMoveStart = (dockID, stationID, bikeID) => {
        if (role !== 'operator') {
            logMessage("Error: Only Operators can move bikes.");
            return;
        }
        if (movingBike) {
            logMessage(`Already moving bike ${movingBike.bikeID}. Complete that move first.`);
            return;
        }
        logMessage(`Operator picked up bike ${bikeID} from Dock ${dockID}. Ready to move.`);
        setMovingBike({ dockID, fromStation: stationID, bikeID, operatorID: userId });
        fetchStations();
        setIsMoving(false);
    };

    const handleMoveComplete = (targetStation, targetDockID) => {
        if (!movingBike) {
            logMessage("No bike currently selected for moving.");
            return;
        }

        const { bikeID, fromStation, dockID: sourceDockID } = movingBike;

        if (fromStation === targetStation.stationID) {
            logMessage("Cannot move bike to same station!");
            setMovingBike(null);
            return;
        }

        const moveData = {
            bikeID: bikeID,
            dock1ID: sourceDockID, // Source dock
            dock2ID: targetDockID, // Destination dock
            riderID: userId
        };

        logMessage(`Operator moving bike ${bikeID} from Station ${fromStation} to Dock ${targetDockID} at ${targetStation.name}...`);

        fetch("/api/action/moveABikefromDockAToDockB", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(moveData),
        })
            .then(r => r.text())
            .then(message => {
                if (message && message !== "false" && !message.includes("Error")) {
                    logMessage(`✅ Move successful! ${message}`);
                    setMovingBike(null);
                    setCount(c => c + 1);
                    setIsMoving(true);
                    fetchStations();
                } else {
                    logMessage(`❌ Move failed. ${message}`);
                }
            })
            .catch(e => logMessage(`❌ Network error during move: ${e.message}`));
    };

    const handleToggleStationService = (stationID, currentState) => {
        if (role !== 'operator') {
            logMessage("Error: Only Operators can change station status.");
            return;
        }
        const endpoint = currentState === 'ACTIVE' ? "/api/action/setAStationAsOutOfService" : "/api/action/setAStationAsActive";
        const action = currentState === 'ACTIVE' ? "Out of Service" : "Active";

        logMessage(`Setting station ${stationID} to ${action}...`);

        fetch(endpoint, {
            method: "POST",
            headers: {
                "Content-Type": "text/plain"
            },
            body: stationID,
        })
            .then(r => r.text())
            .then(message => {
                if (message && message !== "false" && !message.includes("Error")) {
                    logMessage(`✅ Station ${stationID} status updated: ${message}`);
                    setCount(c => c + 1);
                    fetchStations();
                } else {
                    logMessage(`❌ Station status update failed. ${message}`);
                }
            })
            .catch(e => logMessage(`❌ Network error: ${e.message}`));
    }

    return (
        <div className="flex flex-col gap-4 p-4">
            <h1 className="text-2xl font-bold text-center">Bike Sharing Dashboard</h1>

            {/* User Info */}
            <div className="p-2 bg-blue-50 border rounded">
                {user ? (
                    <>
                        <p><strong>User:</strong> {user.fullName || user.username} | <strong>Role:</strong> {role}</p>
                        <p><strong>Email:</strong> {user.email}</p>
                    </>
                ) : (
                    <p>Please log in to use the system</p>
                )}
                {reservedBike && (
                    <p><strong>Active Reservation:</strong> Bike {reservedBike.bikeID} at {reservedBike.stationName}</p>
                )}
                {movingBike && (
                    <p><strong>Moving Bike:</strong> Bike {movingBike.bikeID} from Dock {movingBike.dockID}</p>
                )}
            </div>

            {/* Console */}
            <div className="p-2 bg-gray-50 border rounded h-24 overflow-y-auto text-xs">
                {consoleMessages.map((msg, idx) => (
                    <div key={idx}>• {msg}</div>
                ))}
            </div>

            {/* Action Buttons */}
            <div className="flex gap-2">
                {isReserved && reservedBike && !reservedBike.checkedOut && (
                    <button
                        onClick={handleCancelReservation}
                        className="bg-red-500 text-white px-4 py-2 rounded"
                    >
                        Cancel Reservation
                    </button>
                )}
                {isReserved && reservedBike && !reservedBike.checkedOut && (
                    <button
                        onClick={handleCheckout}
                        className="bg-green-600 text-white px-4 py-2 rounded"
                    >
                        Checkout Bike
                    </button>
                )}
                {movingBike && (
                    <button
                        onClick={() => setMovingBike(null)}
                        className="bg-gray-500 text-white px-4 py-2 rounded"
                    >
                        Cancel Move
                    </button>
                )}
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

                                    {role === "operator" && (
                                        <button
                                            onClick={() => handleToggleStationService(station.stationID, station.operationalState)}
                                            className={`mt-2 ${
                                                station.operationalState === 'ACTIVE' ? 'bg-yellow-500' : 'bg-green-500'
                                            } text-white px-3 py-1 rounded`}
                                        >
                                            {station.operationalState === 'ACTIVE' ? 'Set Out of Service' : 'Set Active'}
                                        </button>
                                    )}

                                    <div className="grid grid-cols-2 gap-2 mt-2 max-h-[150px] overflow-y-auto">
                                        {station.docks.map((dock) => (
                                            <div
                                                key={dock.dockID}
                                                className={`p-2 border rounded ${
                                                    dock.state === "OCCUPIED" ? "bg-green-100" : "bg-gray-100"
                                                }`}
                                            >
                                                <p><strong>Dock {dock.dockID}</strong></p>
                                                <p>State: {dock.state}</p>
                                                {dock.bike ? (
                                                    <>
                                                        <p>{dock.bike.type}</p>
                                                        <p>BikeID: <strong>{dock.bike.bikeID}</strong></p>
                                                        {role === "rider" && !isReserved && !isUndocking && (
                                                            <button
                                                                onClick={() => handleReserve(dock.bike.bikeID, station.stationID, station.name)}
                                                                className="bg-blue-500 text-white px-2 py-1 mt-1 rounded text-xs"
                                                            >
                                                                Reserve
                                                            </button>
                                                        )}
                                                        {role === "operator" && isMoving && (
                                                            <button
                                                                onClick={() => handleMoveStart(dock.dockID, station.stationID, dock.bike.bikeID)}
                                                                className="bg-orange-500 text-white px-2 py-1 mt-1 rounded text-xs"
                                                            >
                                                                Move
                                                            </button>
                                                        )}
                                                    </>
                                                ) : (
                                                    <>
                                                        <p className="italic text-gray-500 text-sm">Empty</p>
                                                        {role === "rider" && isUndocking && dock.state === "EMPTY" && (
                                                            <button
                                                                onClick={() => handleReturn(dock.dockID, station.name)}
                                                                className="bg-blue-500 text-white px-2 py-1 mt-1 rounded text-xs"
                                                            >
                                                                Return
                                                            </button>
                                                        )}
                                                        {role === "operator" && movingBike && dock.state === "EMPTY" && (
                                                            <button
                                                                onClick={() => handleMoveComplete(station, dock.dockID)}
                                                                className="mt-2 bg-green-700 text-white px-3 py-1 rounded text-xs"
                                                            >
                                                                Move Here
                                                            </button>
                                                        )}
                                                    </>
                                                )}
                                            </div>
                                        ))}
                                    </div>
                                </div>
                            </Popup>
                        </Marker>
                    ))}
                </MapContainer>
            </div>
        </div>
    );
}
