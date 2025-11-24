import {useState, useEffect} from "react";
import { collection, onSnapshot, query } from "firebase/firestore";
import { db } from "../firebase/firebaseConfig"; // adjust to your Firebase config path

export default function RideHistory({user = { username: localStorage.getItem("fullName"), fullName: localStorage.getItem("fullName") }, role = "rider"}){
    const [search, setSearch] = useState("");
    const [bikeType, setBikeType] = useState("");
    const [bikeID, setBikeID] = useState("");
    const [startDate, setStartDate] = useState("");
    const [endDate, setEndDate] = useState("");
    const [filtered, setFiltered] = useState([]);
    const [selectedRide, setSelectedRide] = useState(null);
    const [trips, setTrips] = useState([]);
    // Pagination states
    const [currentPage, setCurrentPage] = useState(1);
    const ridesPerPage = 8;



    const fetchTripsForUser = () => {
        fetch("/api/action/getAllTripsForUser", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username: localStorage.getItem("username") }),
        })
            .then(r => {
                if (!r.ok) throw new Error("Failed to fetch trips");
                return r.json();
            })
            .then(data => {
                const normalized = (Array.isArray(data) ? data : []).map(t => {
                    console.log("Fetched tripId values:", t.tripId, t.tripID, t.id);

                    const rider = t?.reservation?.rider;
                    const bikeTypeRaw = t?.reservation?.bike?.type;
                    const date = t?.reservation?.date || null;

                    // Raw start/end times from Firebase
                    const startTimeRaw = t?.reservation?.rider?.startTime || t?.startTime;
                    const endTimeRaw = t?.endTime;

                    // Convert date + time strings into full ISO
                    const toIso = (d, tm) => (d && tm) ? `${d}T${String(tm).split('.')[0]}` : null;
                    const startTimeISO = toIso(date, startTimeRaw);
                    const endTimeISO = toIso(date, endTimeRaw);

                    // Compute duration (minutes)
                    let duration = null;
                    if (startTimeISO && endTimeISO) {
                        const start = new Date(startTimeISO);
                        const end = new Date(endTimeISO);
                        const diffMs = end - start;
                        duration = diffMs > 0 ? Math.round(diffMs / 60000) : 0;
                    }

                    // Build timeline string
                    const fmt = d => d ? new Date(d).toLocaleString("en-US", {
                        dateStyle: "short",
                        timeStyle: "medium"
                    }) : "—";
                    const timeline = `Checkout at ${fmt(startTimeISO)} → Returned at ${fmt(endTimeISO)}`;

                    return {
                        tripId: t.tripId ?? t.tripID ?? t.id ?? "",
                        rider: rider?.fullName ?? rider?.username ?? "",
                        startStation: t.origin ?? "",
                        endStation: t.arrival ?? "",
                        bikeType: bikeTypeRaw?.toUpperCase() === "E_BIKE" ? "E-Bike" : "Standard",
                        bikeID: t?.reservation?.bike?.bikeID ?? "",
                        cost: Number(t?.payment?.amount ?? 0),
                        startTime: startTimeISO,
                        endTime: endTimeISO,
                        duration,
                        timeline,
                        baseFee: Number(t?.pricingPlan?.baseFee ?? 0),
                        perMinuteRate: Number(t?.pricingPlan?.ratePerMinute ?? 0),
                        eBikeSurcharge:
                            bikeTypeRaw?.toUpperCase() === "E_BIKE" &&
                            t?.pricingPlan?.planName?.toLowerCase() === "base plan"
                                ? 20
                                : 0,
                    };
                });

                console.log("Normalized trips:", normalized);

                const completedTrips = normalized.filter(t => t.endTime); // only those that have ended
                const sorted = normalized.sort((a, b) => new Date(b.endTime) - new Date(a.endTime));

                setTrips(normalized);
                setFiltered(normalized);
            })
            .catch(err => console.error("Error fetching trips:", err));
    };

    useEffect(() => {
        fetchTripsForUser();
    }, []);

    // If you want the list to re-display after a new fetch even without pressing Search
    useEffect(() => {
        if (trips.length && filtered.length === 0) {
            setFiltered(trips);
        }
    }, [trips]);


// realtime updates for operators only
    useEffect(() => {
        // Only operators use real-time updates
        if (role !== "operator") return;

        // Query all trips that have ended (endTime != null)
        const tripsRef = collection(db, "trips");
        const q = query(tripsRef); // you can add filters later if desired

        const unsubscribe = onSnapshot(q, (snapshot) => {
            const updatedTrips = [];

            snapshot.forEach((doc) => {
                const t = doc.data();

                // Only include completed trips (have endTime)
                if (!t.endTime) return;

                const rider = t?.reservation?.rider;
                const bikeTypeRaw = t?.reservation?.bike?.type;
                const date = t?.reservation?.date || null;
                const startTimeRaw = t?.reservation?.rider?.startTime || t?.startTime;
                const endTimeRaw = t?.endTime;

                const toIso = (d, tm) => (d && tm) ? `${d}T${String(tm).split('.')[0]}` : null;
                const startTimeISO = toIso(date, startTimeRaw);
                const endTimeISO = toIso(date, endTimeRaw);

                let duration = null;
                if (startTimeISO && endTimeISO) {
                    const start = new Date(startTimeISO);
                    const end = new Date(endTimeISO);
                    const diffMs = end - start;
                    duration = diffMs > 0 ? Math.round(diffMs / 60000) : 0;
                }

                const fmt = d => d ? new Date(d).toLocaleString("en-US", {
                    dateStyle: "short",
                    timeStyle: "medium"
                }) : "—";
                const timeline = `Checkout at ${fmt(startTimeISO)} → Returned at ${fmt(endTimeISO)}`;

                updatedTrips.push({
                    tripId: t.tripId ?? t.tripID ?? t.id ?? "",
                    rider: rider?.fullName ?? rider?.username ?? "",
                    startStation: t.origin ?? "",
                    endStation: t.arrival ?? "",
                    bikeType: bikeTypeRaw?.toUpperCase() === "E_BIKE" ? "E-Bike" : "Standard",
                    bikeID: t?.reservation?.bike?.bikeID ?? "",
                    cost: Number(t?.payment?.amount ?? 0),
                    startTime: startTimeISO,
                    endTime: endTimeISO,
                    duration,
                    timeline,
                    baseFee: Number(t?.pricingPlan?.baseFee ?? 0),
                    perMinuteRate: Number(t?.pricingPlan?.ratePerMinute ?? 0),
                    eBikeSurcharge:
                        bikeTypeRaw?.toUpperCase() === "E_BIKE" &&
                        t?.pricingPlan?.planName?.toLowerCase() === "base plan"
                            ? 20
                            : 0,
                });
            });

            // Sort newest → oldest
            const sorted = updatedTrips.sort((a, b) => new Date(b.endTime) - new Date(a.endTime));

            setTrips(sorted);
            setFiltered(sorted);
            handleSearch(); // reapply active filters dynamically
        });

        // Cleanup listener on unmount
        return () => unsubscribe();

    }, [role]);


    const handleSearch = () => {
        // Date format regex (YYYY-MM-DD)
        const datePattern = /^\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\d|3[01])$/;

        // Validate date formats if provided
        if (startDate && !datePattern.test(startDate)) {
            alert("Invalid start date format. Please use YYYY-MM-DD.");
            return;
        }
        if (endDate && !datePattern.test(endDate)) {
            alert("Invalid end date format. Please use YYYY-MM-DD.");
            return;
        }

        // Validate only if both are filled
        if (startDate && endDate && new Date(endDate) < new Date(startDate)) {
            alert("End date cannot be before start date.");
            return;
        }

        const tripIdPattern = /^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$/;

        if (search.trim() && !tripIdPattern.test(search.trim())) {
            alert("Invalid Trip ID format. Use format like xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx.");
            return;
        }

        let results = trips;

        // Filter by rider (if applicable)
        if (role === "rider") {
            const riderName = user.fullName || user.username || localStorage.getItem("fullName");
            results = results.filter(r => r.rider === riderName);
        }

        // Search by trip ID (partial match allowed — supports both tripId and tripID)
        if (search.trim()) {
            const cleanSearch = search.trim();
            console.log("Searching for Trip ID:", cleanSearch);
            results = results.filter(r => {
                const id = r.tripID ?? r.tripId ?? "";
                const match = id.includes(cleanSearch);
                if (match) console.log("Matched trip:", id);
                return match;
            });
        }

        // Filter by bike type
        if (bikeType) {
            results = results.filter(r => r.bikeType === bikeType);
        }

        // Filter by start date only
        if (startDate && !endDate) {
            results = results.filter(r => new Date(r.startTime) >= new Date(startDate));
        }

        // Filter by end date only
        if (!startDate && endDate) {
            results = results.filter(r => new Date(r.endTime) <= new Date(endDate));
        }

        // Filter by both (range)
        if (startDate && endDate) {
            results = results.filter(r => {
                const start = new Date(startDate);
                const end = new Date(endDate);
                const tripStart = new Date(r.startTime);
                const tripEnd = new Date(r.endTime);
                return tripStart >= start && tripEnd <= end;
            });
        }

        // Sort newest → oldest
        results = results.sort((a, b) => new Date(b.endTime) - new Date(a.endTime));

        setFiltered(results);
        setCurrentPage(1);
    };

    // Pagination calculations
    const indexOfLastRide = currentPage * ridesPerPage;
    const indexOfFirstRide = indexOfLastRide - ridesPerPage;
    const currentRides = filtered.slice(indexOfFirstRide, indexOfLastRide);
    const totalPages = Math.ceil(filtered.length / ridesPerPage);

    const handlePageChange = (page) => {
        setCurrentPage(page);
    };

    return (
        <div className="max-w-6xl mx-auto bg-white p-6 rounded-2xl shadow-md mt-10">
            <h2 className="text-2xl font-bold mb-4 text-blue-600">Ride History</h2>

            {/* Filters */}
            <div className="flex flex-wrap gap-3 mb-5 items-end">
                <div className="flex-1">
                    <label className="block text-sm text-gray-600 mb-1">Search by Trip ID</label>
                    <input
                        type="text"
                        placeholder="xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"
                        value={search}
                        onChange={(e) => setSearch(e.target.value)}
                        className="w-full border rounded-lg px-3 py-2 focus:ring-2 focus:ring-blue-400"
                    />
                </div>

                <div>
                    <label className="block text-sm text-gray-600 mb-1">Start Date</label>
                    <input
                        type="date"
                        value={startDate}
                        onChange={(e) => setStartDate(e.target.value)}
                        className="border rounded-lg px-3 py-2 focus:ring-2 focus:ring-blue-400"
                    />
                </div>

                <div>
                    <label className="block text-sm text-gray-600 mb-1">End Date</label>
                    <input
                        type="date"
                        value={endDate}
                        onChange={(e) => setEndDate(e.target.value)}
                        className="border rounded-lg px-3 py-2 focus:ring-2 focus:ring-blue-400"
                    />
                </div>

                <div>
                    <label className="block text-sm text-gray-600 mb-1">Bike Type</label>
                    <select
                        value={bikeType}
                        onChange={(e) => setBikeType(e.target.value)}
                        className="border rounded-lg px-3 py-2 focus:ring-2 focus:ring-blue-400"
                    >
                        <option value="">All</option>
                        <option value="Standard">Standard</option>
                        <option value="E-Bike">E-Bike</option>
                    </select>
                </div>

                <button
                    onClick={handleSearch}
                    className="bg-blue-600 hover:bg-blue-700 text-white font-semibold px-5 py-2 rounded-lg transition"
                >
                    Search
                </button>
            </div>

            {/* Results */}
            {filtered.length > 0 ? (
                <>
                    <div className="overflow-x-auto">
                        <table className="min-w-full text-sm text-left border">
                            <thead className="bg-blue-100">
                            <tr>
                                <th className="px-4 py-2">Trip ID</th>
                                <th className="px-4 py-2">Rider</th>
                                <th className="px-4 py-2">Start → End</th>
                                <th className="px-4 py-2">Bike Type</th>
                                <th className="px-4 py-2">Cost ($)</th>
                                <th className="px-4 py-2 text-center">Action</th>
                            </tr>
                            </thead>
                            <tbody>
                            {currentRides.map((ride) => (
                                <tr
                                    key={ride.tripId}
                                    className="border-t hover:bg-blue-50 transition cursor-pointer"
                                >
                                    <td className="px-4 py-2 font-medium">{ride.tripId}</td>
                                    <td className="px-4 py-2">{ride.rider}</td>
                                    <td className="px-4 py-2">
                                        {ride.startStation} → {ride.endStation}
                                    </td>
                                    <td className="px-4 py-2">{ride.bikeType}</td>
                                    <td className="px-4 py-2">
                                        ${Number(ride.cost ?? 0).toFixed(2)}
                                    </td>
                                    <td className="px-4 py-2 text-center">
                                        <button
                                            onClick={() => setSelectedRide(ride)}
                                            className="text-blue-600 hover:underline"
                                        >
                                            View
                                        </button>
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>

                    {/* Pagination Controls */}
                    {totalPages > 1 && (
                        <div className="flex justify-center items-center gap-2 mt-4">
                            <button
                                onClick={() => handlePageChange(currentPage - 1)}
                                disabled={currentPage === 1}
                                className="px-3 py-1 rounded-lg border bg-gray-100 hover:bg-gray-200 disabled:opacity-50"
                            >
                                Prev
                            </button>

                            {[...Array(totalPages)].map((_, i) => (
                                <button
                                    key={i}
                                    onClick={() => handlePageChange(i + 1)}
                                    className={`px-3 py-1 rounded-lg border ${
                                        currentPage === i + 1
                                            ? "bg-blue-600 text-white"
                                            : "bg-gray-100 hover:bg-gray-200"
                                    }`}
                                >
                                    {i + 1}
                                </button>
                            ))}

                            <button
                                onClick={() => handlePageChange(currentPage + 1)}
                                disabled={currentPage === totalPages}
                                className="px-3 py-1 rounded-lg border bg-gray-100 hover:bg-gray-200 disabled:opacity-50"
                            >
                                Next
                            </button>
                        </div>
                    )}
                </>
            ) : (
                <p className="mt-6 text-gray-500 italic text-center">
                    No results found. Try clearing or adjusting your filters — they may be too specific.
                </p>
            )}

            {/* Modal */}
            {selectedRide && (
                <div className="fixed inset-0 bg-black/40 flex justify-center items-center">
                    <div className="bg-white p-6 rounded-2xl shadow-xl max-w-lg w-full">
                        <h3 className="text-xl font-bold mb-3 text-blue-700">
                            Trip Details — {selectedRide.tripId}
                        </h3>

                        <ul className="text-gray-700 space-y-1">
                            <li><strong>Rider:</strong> {selectedRide.rider}</li>
                            <li><strong>Start Station:</strong> {selectedRide.startStation}</li>
                            <li><strong>End Station:</strong> {selectedRide.endStation}</li>
                            <li><strong>Duration:</strong> {selectedRide.duration} min</li>
                            <li><strong>Bike Type:</strong> {selectedRide.bikeType}</li>
                            <li><strong>Bike ID:</strong> {selectedRide.bikeID || "—"}</li>
                            <li><strong>Base Fee:</strong> ${selectedRide.baseFee}</li>
                            <li><strong>Per Minute Rate:</strong> ${selectedRide.perMinuteRate}</li>
                            {selectedRide.eBikeSurcharge > 0 && (
                                <li><strong>E-Bike Surcharge:</strong> ${selectedRide.eBikeSurcharge}</li>
                            )}
                            {selectedRide?.rider?.toLowerCase()?.includes("operator") && (
                                <li className="text-green-600 font-semibold">
                                    <strong>Operator Discount:</strong>{" "}
                                    {selectedRide.perMinuteRate === 4
                                        ? "10% (Premium Plan)"
                                        : selectedRide.perMinuteRate === 2
                                            ? "10% (Premium Pro Plan)"
                                            : "5% (Base Plan)"}
                                </li>
                            )}
                            <li>
                                <strong>Timeline:</strong>{" "}
                                Checkout at {new Date(selectedRide.startTime).toLocaleString()} → Returned at{" "}
                                {new Date(selectedRide.endTime).toLocaleString()}
                            </li>
                            <li className="mt-2 font-bold text-blue-600">
                                Total: ${selectedRide.cost.toFixed(2)}
                            </li>
                        </ul>

                        <div className="mt-6 flex justify-end gap-2">
                            <button
                                onClick={() => setSelectedRide(null)}
                                className="bg-gray-300 hover:bg-gray-400 text-gray-800 px-4 py-2 rounded-lg"
                            >
                                Close
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}