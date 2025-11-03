import React, { useState, useEffect } from "react";
import { MapContainer, TileLayer, Marker, Popup } from "react-leaflet";
import L from "leaflet";
import "leaflet/dist/leaflet.css";
import markerIcon2x from "leaflet/dist/images/marker-icon-2x.png";
import markerIcon from "leaflet/dist/images/marker-icon.png";
import markerShadow from "leaflet/dist/images/marker-shadow.png";
import { authService } from '../services/authService'; // Adjust path as needed
import redIconUrl from "leaflet-color-markers/img/marker-icon-red.png";
import yellowIconUrl from "leaflet-color-markers/img/marker-icon-yellow.png";
import greenIconUrl from "leaflet-color-markers/img/marker-icon-green.png";
import { useNavigate } from "react-router-dom";

function getMarkerIcon(fullness) {
    let iconUrl;
    if (fullness === 0 || fullness === 100) iconUrl = redIconUrl;
    else if (fullness < 25 || fullness > 85) iconUrl = yellowIconUrl;
    else iconUrl = greenIconUrl;

    return new L.Icon({
        iconUrl,
        shadowUrl: markerShadow,
        iconSize: [25, 41],
        iconAnchor: [12, 41],
        popupAnchor: [1, -34],
        shadowSize: [41, 41],
    });
}

// Fix Leaflet marker icons
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
    iconRetinaUrl: markerIcon2x,
    iconUrl: markerIcon,
    shadowUrl: markerShadow,
});

function getStationColor(fullness) {
    if (fullness === 0 || fullness === 100) return "red";
    if (fullness < 25 || fullness > 85) return "yellow";
    return "green";
}
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
    const [reservationExpiry, setReservationExpiry] = useState(null);
    const [count, setCount] = useState(0);
    const [startingDock, setStartingDock] = useState("");
    const [bikesAvailable, setBikesAvailable] = useState(0);
    const [stationState, setStationState] = useState("");
    const [tripSummary,setTripSummary] = useState(null);
    const [istripSummary,setIsTripSummary] = useState(false);
    const [selectedStation, setSelectedStation] = useState(null);
    const [selectedPlan, setSelectedPlan] = useState("Base");
    const plans = ["Base", "Premium", "Premium Pro"];
    const navigate = useNavigate();

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
    useEffect( () => {
        //alert("expired");
        if (reservationExpiry === null) return; // skip if null or empty

        const interval = setInterval(() => {
            const now = new Date();

            // Extract current local time in HH:mm:ss format
            const nowTime = now.toLocaleTimeString("en-GB", { hour12: false });
            // alert("expired");
            // Parse both times into comparable values (in milliseconds since midnight)
            const [nowHours, nowMinutes, nowSeconds] = nowTime.split(":").map(Number);
            const [expHours, expMinutes, expSeconds] = reservationExpiry.replace(/ AM| PM/, "").split(":").map(Number);

            const nowMs = nowHours * 3600000 + nowMinutes * 60000 + nowSeconds * 1000;
            const expiryMs = expHours * 3600000 + expMinutes * 60000 + expSeconds * 1000;
            //logMessage(nowMs);
            //logMessage(expiryMs);
            // Compare: if current time has passed the expiry time
            if (nowMs >= expiryMs) {
                setIsReserved(false);
                setReservedBike("");
                setReservationID("");
                logMessage(`✅ Reservation cancelled successfully.`);
                setCount(c => c + 1);
                fetchStations();
                clearInterval(interval); // stop checking after expiry
            }
        }, 1000); // run every second

        return () => clearInterval(interval);
    }, [reservationExpiry]);


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
    const resetSystemState = async  () => {
        console.log("Reset button clicked"); // check if this prints

        try {
            const response = await fetch("/api/action/resetInitialSystemState");
            if (!response.ok) throw new Error("Network response not ok");
            const message = await response.text();
            console.log("Reset response:", message);
            logMessage("Reset successfully done");

            window.location.reload();
        } catch (error) {
            console.error("Error resetting system state:", error);
        }
    };

    const logMessage = (msg) => setConsoleMessages((prev) => [msg, ...prev]);

    const handleReserve = (bikeID, stationID, stationName, stationStatus, stationBikesAvailable) => {
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
                setBikesAvailable(stationBikesAvailable);
                setStationState(stationStatus);
                const now = new Date();
                now.setMinutes(now.getMinutes() + 5);
                setReservationExpiry(now.toLocaleTimeString("en-GB", { hour12: false }));
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
                setStationState(null);
                setReservationExpiry(null);
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

        let planID = "1";
        if(selectedPlan === "Base"){
            planID = "1";
        }
        else if(selectedPlan === "Premium"){
            planID = "2";
        }
        else if(selectedPlan === "Premium Pro"){
            planID = "3";
        }

        const dockingData = {
            riderID: userId,
            reservationID: reservedBike.reservationID,
            dockID: dockID,
            planID: planID,
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
        .then((trip) => {
            if (trip) {
                setIsUndocked(false);
                setIsReserved(false);
                setReservedBike(null);
                setReservationID("");
                let tripData;
                try {
                    tripData = JSON.parse(trip); // try parse as JSON
                } catch {
                    tripData = trip; // fallback to string if not JSON
                }
                logMessage(`✅ Trip complete! Bike returned to dock ${dockID}. Trip ID: ${tripData.tripID}`);
                setTripSummary(tripData);
                setIsTripSummary(true);
                setCount(c => c + 1);
                fetchStations();
            } else {
                logMessage("❌ Return failed. Check if dock is free or station is active.");
                triggerStationEvent();
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
        setStartingDock(dockID);
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
                setStartingDock("");
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

    const triggerStationEvent = () => {
        logMessage(`❌ Error: Return the bike to another station.`);
    }
    function getOccupancyPercent(station) {
        if(station === null) {return 0;}
        if(station.docks === null || station.docks === undefined) {return 0;}
        if(station.bikesAvailable === null || station.bikesAvailable === undefined) {return 0;}
        const totalDocks = station.docks.length;
        const bikesAvailable = station.bikesAvailable;
        // Round to nearest integer
        return Math.round((bikesAvailable / totalDocks) * 100);
    }

    const handleChangeBikeStatus = (bikeID, bikeStatus) => {
        const endpoint = bikeStatus === 'AVAILABLE' ? "/api/action/setABikeAsMaintenance" : "/api/action/setABikeAsAvailable";
        const action = bikeStatus === 'AVAILABLE' ? "Maintenance" : "Available";
        logMessage(`Setting bike ${bikeID} to ${action}...`);

        fetch(endpoint, {
            method: "POST",
            headers: {
                "Content-Type": "text/plain"
            },
            body: bikeID,
        })
            .then(r => r.text())
            .then(message => {
                if (message && message !== "false" && !message.includes("Error")) {
                    logMessage(`✅ Bike ${bikeID} status updated: ${message}`);
                    setCount(c => c + 1);
                    fetchStations();
                } else {
                    logMessage(`❌ Bike status update failed. ${message}`);
                }
            })
            .catch(e => logMessage(`❌ Network error: ${e.message}`));
    }

    return (
        <div className="flex flex-col gap-4 p-4">
            <h1 className="text-2xl font-bold text-center">Bike Sharing Dashboard</h1>
            { role === "rider" && (
            <div className="flex justify-center items-center mt-10 space-x-4">
                Choose a Plan =>
                {plans.map((plan, index) => (
                    <button
                        key={index}
                        onClick={() => setSelectedPlan(plan)}
                        className={`px-6 py-3 rounded-lg border font-medium transition
            ${selectedPlan === plan ? "bg-blue-500 text-white border-blue-500" : "bg-white text-gray-700 border-gray-300 hover:bg-gray-100"}`}
                    >
                        {plan}
                    </button>
                ))}
            </div>)}

            {/* User Info */}
            <div className="p-2 bg-blue-50 border rounded">
                {user ? (
                    <>
                        <p><strong>User:</strong> {user.fullName || user.username} | <strong>Role:</strong> {role}</p>
                        <p><strong>Username:</strong> {user.username}</p>
                        <p><strong>Email:</strong> {user.email}</p>
                        {selectedStation !== null && (
                            <>
                                <p><strong>Selected Station</strong></p>
                                <p>Station Name: {selectedStation.name}</p>
                                <p>Bikes Available: {selectedStation.bikesAvailable}</p>
                                <p>Free Docks: {selectedStation.freeDocks}</p>
                                <p>Capacity: {selectedStation.docks.length}</p>
                            </>
                        )}
                        {role === "operator" && ( <button
                            onClick={() => {
                                console.log("Reset button clicked");
                                resetSystemState();
                            }}
                            className="bg-red-500 text-white hover:bg-red-600 rounded-lg px-4 py-2 m-2 transition font-medium"
                        >
                            Reset System State
                        </button>)}
                    </>
                ) : (
                    <p>Please log in to use the system</p>
                )}
                {reservedBike && (
                    <p><strong>Active Reservation:</strong> Bike {reservedBike.bikeID} </p>
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
            <div className="flex space-x-6 items-center">
                {/* Category A */}
                <div className="flex items-center space-x-2">
                    <div className="w-4 h-4 rounded-full bg-red-500"></div>
                    <span className="text-gray-700 text-sm">Empty/Full stations </span>
                </div>

                {/* Category B */}
                <div className="flex items-center space-x-2">
                    <div className="w-4 h-4 rounded-full bg-yellow-500"></div>
                    <span className="text-gray-700 text-sm">Almost full (higher than 85%) or less than 25% </span>
                </div>

                {/* Category C */}
                <div className="flex items-center space-x-2">
                    <div className="w-4 h-4 rounded-full bg-green-500"></div>
                    <span className="text-gray-700 text-sm">Balanced</span>
                </div>
                <div className="flex items-center space-x-2">
                    <span className="text-gray-700 text-sm">E-Bikes are marked on the dock name as (E) </span>
                </div>
            </div>

            {/* Action Buttons */}
            <div className="flex gap-2">
                {isReserved && reservedBike && !reservedBike.checkedOut && (
                    <button
                        onClick={() => {
                            handleCancelReservation();
                            setSelectedStation(null);
                            setReservationExpiry(null);
                        }}
                        className="bg-red-500 text-white px-4 py-2 rounded"
                    >
                        Cancel Reservation
                    </button>
                )}
                {isReserved && reservedBike && !reservedBike.checkedOut && stationState === "ACTIVE" && bikesAvailable !== 0 && (
                    <button
                        onClick={handleCheckout}
                        className="bg-green-600 text-white px-4 py-2 rounded"
                    >
                        Checkout Bike
                    </button>
                )}
                {movingBike && (
                    <button
                        onClick={() => {setMovingBike(null);
                            setIsMoving(true);
                            setSelectedStation(null);}}
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
                        <Marker key={station.stationID} position={[station.latitude, station.longitude]} icon={getMarkerIcon(getOccupancyPercent(station))}>
                            <Popup className={`popup-${getStationColor(getOccupancyPercent(station))}`}>
                                <div style={{ maxHeight: "400px", overflowY: "auto", width: "300px" }}>
                                    <p><strong>Name : {station.name}</strong></p>
                                    <p>Station Status: {station.operationalState}</p>
                                    <p>Address: {station.address}</p>
                                    <p>Bikes Available: {station.bikesAvailable}</p>
                                    <p>Free Docks: {station.freeDocks}</p>
                                    <p>Capacity: {station.docks.length}</p>
                                    <p>Bikes/Capacity: {station.bikesAvailable}/{station.docks.length}= { Math.round(station.bikesAvailable/station.docks.length * 100)} %</p>
                                    <p>Reservation Hold Time: 5 minutes</p>
                                    {role === "operator" && (
                                        <button
                                            onClick={() => {handleToggleStationService(station.stationID, station.operationalState);
                                                setSelectedStation(station);}}
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
                                                    dock.state === "OCCUPIED" ? "bg-black text-white" : "bg-gray-100 text-black"
                                                }`}
                                            >
                                                <p><strong>Dock {dock.dockID} {dock?.bike?.type === "E_BIKE" && <span>( E )</span>}</strong></p>
                                                <p>State: <strong>{dock.state}</strong></p>
                                                {dock.bike ? (
                                                    <>
                                                        <p>BikeType : <strong>{dock.bike.type}</strong></p>
                                                        <p>BikeID: <strong>{dock.bike.bikeID}</strong></p>
                                                        <p>BikeStatus: <strong>{dock.bike.stateString}</strong></p>

                                                        {reservationExpiry !== null && reservedBike.bikeID === dock.bike.bikeID && ( <p>Reservation Expiry: <strong>{reservationExpiry}</strong></p>)}

                                                        {role === "rider" && !isReserved && !isUndocking && station.operationalState === "ACTIVE" && dock.bike.stateString === "AVAILABLE" && dock.state === "OCCUPIED" &&(
                                                            <button
                                                                onClick={() => {handleReserve(dock.bike.bikeID, station.stationID, station.name,station.operationalState,station.bikesAvailable);
                                                                setSelectedStation(station);}}
                                                                className="bg-blue-500 text-white px-2 py-1 mt-1 rounded text-xs"
                                                            >
                                                                Reserve
                                                            </button>
                                                        )}
                                                        {role === "operator" && isMoving && dock.state === "OCCUPIED" && (
                                                            <button
                                                                onClick={() => {handleMoveStart(dock.dockID, station.stationID, dock.bike.bikeID);
                                                                setSelectedStation(station);}}
                                                                className="bg-orange-500 text-white px-2 py-1 mt-1 rounded text-xs"
                                                            >
                                                                Move
                                                            </button>
                                                        )} <br/>
                                                        {role === "operator" && isMoving && (dock.state === "OCCUPIED" || dock.state === "OUT_OF_SERVICE") && (dock.bike.stateString === "AVAILABLE" ||dock.bike.stateString === "MAINTENANCE") && (
                                                            <button
                                                                onClick={() => {handleChangeBikeStatus(dock.bike.bikeID,dock.bike.stateString);
                                                                    setSelectedStation(station);}}
                                                                className="bg-red-500 text-white px-2 py-1 mt-1 rounded text-xs"
                                                            >
                                                                {dock.bike.stateString === "AVAILABLE" ? ("Set as Maintenance") : ("Set as Available")}
                                                            </button>
                                                        )}
                                                    </>
                                                ) : (
                                                    <>
                                                        <p className="italic text-gray-500 text-sm">Empty</p>
                                                        {role === "rider" && isUndocking && dock.state === "EMPTY" && station.freeDocks !== 0 && station.operationalState === "ACTIVE" && (
                                                            <button
                                                                onClick={() => {handleReturn(dock.dockID, station.name);
                                                                    setSelectedStation(null);}}
                                                                className="bg-blue-500 text-white px-2 py-1 mt-1 rounded text-xs"
                                                            >
                                                                Return
                                                            </button>
                                                        )}
                                                        {role === "operator" && movingBike && dock.state === "EMPTY" && startingDock !== dock.dockID && (
                                                            <button
                                                                onClick={() => {handleMoveComplete(station, dock.dockID);
                                                                setSelectedStation(null);}}
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
            {/* Trip Summary Section */}
            {istripSummary && ( <div className="mt-6 bg-white border border-gray-200 rounded-2xl shadow-md p-5 w-full max-w-4xl mx-auto">

                <h2 className="text-2xl font-semibold text-gray-800 mb-4 text-center">
                    Trip Summary
                </h2>
                {/* General Trip Info */}
                    <div className="border-t border-gray-200 pt-4 mt-4">
                        <h3 className="text-lg font-semibold text-gray-800 mb-2">Base Information</h3>
                <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4 text-gray-700 text-sm mb-6">

                    <div>
                        <p><span className="font-medium">Trip ID:</span> {tripSummary.tripID}</p>
                    </div>
                    <div>
                        <p><span className="font-medium">Start Time:</span> {tripSummary.startTime}</p>
                    </div>
                    <div>
                        <p><span className="font-medium">End Time:</span> {tripSummary.endTime}</p>
                    </div>
                    <div>
                        <p><span className="font-medium">Origin:</span> {tripSummary.origin}</p>
                    </div>
                    <div>
                        <p><span className="font-medium">Arrival:</span> {tripSummary.arrival}</p>
                    </div>
                    <div>
                        <p><span className="font-medium">Bike ID:</span> {tripSummary.reservation?.bike?.bikeID}</p>
                    </div>
                    <div>
                        <p><span className="font-medium">Bike Type:</span> {tripSummary.reservation?.bike?.type}</p>
                    </div>
                    <div>
                        <p><span className="font-medium">Reservation Date:</span> {tripSummary.reservation?.date}</p>
                    </div>
                    <div>
                        <p><span className="font-medium">Reservation State:</span> {tripSummary.reservation?.state}</p>
                    </div>
                </div>
                    </div>

                {/* Pricing Plan Section */}
                <div className="border-t border-gray-200 pt-4 mt-4">
                    <h3 className="text-lg font-semibold text-gray-800 mb-2">Pricing Plan</h3>
                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 text-gray-700 text-sm">
                        <div>
                            <p><span className="font-medium">Plan Name:</span> {tripSummary.pricingPlan?.planName|| "N/A"}</p>
                        </div>
                        <div>
                            <p><span className="font-medium">Rate per Minute:</span>
                                {tripSummary.pricingPlan?.ratePerMinute
                                    ? `$${tripSummary.pricingPlan.ratePerMinute}`
                                    : "N/A"}
                            </p>
                        </div>
                        <div>
                            <p><span className="font-medium">Base Fee:</span>
                                {tripSummary.pricingPlan?.baseFee
                                    ? `$${tripSummary.pricingPlan.baseFee}`
                                    : "N/A"}
                            </p>
                        </div>
                        <div>
                            <p><span className="font-medium">E-Bike Surcharge Fee:</span>
                                {tripSummary.pricingPlan?.planName === "Base plan"
                                    ? "20$"
                                    : "N/A"}
                            </p>
                        </div>
                    </div>
                </div>

                {/* Payment Section */}
                <div className="border-t border-gray-200 pt-4 mt-4">
                    <h3 className="text-lg font-semibold text-gray-800 mb-2">Payment Information</h3>
                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 text-gray-700 text-sm">
                        <div>
                            <p><span className="font-medium">Payment Method:</span> {tripSummary.payment?.paymentMethod || "N/A"}</p>
                        </div>
                        <div>
                            <p><span className="font-medium">Paid Date:</span> {tripSummary.payment?.paidDate || "N/A"}</p>
                        </div>
                        <div>
                            <p><span className="font-medium">Total Amount:</span>
                                {tripSummary.payment?.amount
                                    ? `$${tripSummary.payment.amount.toFixed(2)}`
                                    : "N/A"}
                            </p>
                        </div>
                    </div>
                </div>
                <br/>
                    <div className="flex justify-center">
                <button
                    onClick={() => {setIsTripSummary(false); navigate("/billing"); localStorage.setItem("IsTripSummary","true");  localStorage.setItem("TripSummary",JSON.stringify(tripSummary)); }}
                    className="bg-red-500 text-white hover:bg-red-600 rounded-lg px-4 py-2 transition font-medium">
                    Pay for the Trip
                </button>
                    </div>
            </div>)}
        </div>
    );
}
