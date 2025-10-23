import {authService} from "../services/authService.js";
import {reserveBike} from "../services/bikeAPI.jsx";

export default function useRiderActions({
                                            selectedStation,
                                            reservedBike,
                                            setReservedBike,
                                            setStations,
                                            logMessage,
                                            count,
                                            setCount
                                        }) {
    // Reserve a bike
    // ✅ useRiderActions.jsx
    const handleReserve = async (bike, stationID) => {
        if (reservedBike) {
            alert("You already have a reserved bike!");
            return;
        }

        try {
            const user = authService.getCurrentUser();
            if (!user) {
                alert("Please log in first!");
                return;
            }

            console.log("🔥 [useRiderActions] Current Firebase user object:", user); // !!!!! log for debug

            // ✅ choose correct identifier that matches your Firestore "Riders" doc ID
            const riderID = user.email || user.uid || user.displayName; // !!!!! pick best match

            const payload = {
                stationName: selectedStation.name,
                bikeID: bike.bikeID,
                riderID, // !!!!! dynamically chosen identifier
            };

            console.log("📦 [useRiderActions] Sending payload:", payload); // !!!!! verify in console

            const response = await reserveBike(payload);

            if (response === "false") {
                alert("Reservation failed — please try again.");
                return;
            }

            console.log("✅ [useRiderActions] Reservation successful for bike:", bike.bikeID);
            setReservedBike({ ...bike, stationID });
            logMessage(`Reserved bike ${bike.bikeID} at ${selectedStation.name}`);
            setCount((prev) => prev + 1); // refresh Dashboard

        } catch (error) {
            console.error("❌ [useRiderActions] Error reserving bike:", error);
            alert("Something went wrong while reserving the bike.");
        }
    };

    // Checkout the reserved bike
    const handleCheckout = () => {
        if (!reservedBike) {
            alert("No reserved bike to checkout!");
            return;
        }
        logMessage(`Checked out bike ${reservedBike.bikeID}`);
        setReservedBike((prev) => ({...prev, checkedOut: true}));
    };

    // Return the bike
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
                    ? {...s, bikesAvailable: s.bikesAvailable + 1, freeDocks: s.freeDocks - 1}
                    : s
            )
        );

        setReservedBike(null);
    };

    return {handleReserve, handleCheckout, handleReturn};
}