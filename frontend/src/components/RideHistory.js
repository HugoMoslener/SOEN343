import { useState,useEffect  } from "react";

const mockData = [
    {
        tripId: "T007",
        rider: "John Doe",
        startStation: "Downtown",
        endStation: "Uptown",
        bikeType: "E-Bike",
        cost: 15.75,
        startTime: "2024-10-15T08:00",
        endTime: "2024-10-15T08:45",
        duration: 45,
        baseFee: 5,
        perMinuteRate: 0.25,
        eBikeSurcharge: 5,
    },
    {
        tripId: "T008",
        rider: "John Doe",
        startStation: "Lakeside",
        endStation: "Central Park",
        bikeType: "Standard",
        cost: 8.5,
        startTime: "2024-10-16T09:10",
        endTime: "2024-10-16T09:45",
        duration: 35,
        baseFee: 3,
        perMinuteRate: 0.15,
        eBikeSurcharge: 0,
    },
    {
        tripId: "T009",
        rider: "John Doe",
        startStation: "Downtown",
        endStation: "Uptown",
        bikeType: "E-Bike",
        cost: 15.75,
        startTime: "2024-10-15T08:00",
        endTime: "2024-10-15T08:45",
        duration: 45,
        baseFee: 5,
        perMinuteRate: 0.25,
        eBikeSurcharge: 5,
    },
    {
        tripId: "T010",
        rider: "John Doe",
        startStation: "Lakeside",
        endStation: "Central Park",
        bikeType: "Standard",
        cost: 8.5,
        startTime: "2024-10-16T09:10",
        endTime: "2024-10-16T09:45",
        duration: 35,
        baseFee: 3,
        perMinuteRate: 0.15,
        eBikeSurcharge: 0,
    },{
        tripId: "T001",
        rider: "John Doe",
        startStation: "Downtown",
        endStation: "Uptown",
        bikeType: "E-Bike",
        cost: 15.75,
        startTime: "2025-10-15T08:00",
        endTime: "2025-10-15T08:45",
        duration: 45,
        baseFee: 5,
        perMinuteRate: 0.25,
        eBikeSurcharge: 5,
    },
    {
        tripId: "T002",
        rider: "John Doe",
        startStation: "Lakeside",
        endStation: "Central Park",
        bikeType: "Standard",
        cost: 8.5,
        startTime: "2025-10-16T09:10",
        endTime: "2025-10-16T09:45",
        duration: 35,
        baseFee: 3,
        perMinuteRate: 0.15,
        eBikeSurcharge: 0,
    },
    {
        tripId: "T003",
        rider: "John Doe",
        startStation: "West Station",
        endStation: "East Point",
        bikeType: "Standard",
        cost: 10.25,
        startTime: "2025-10-20T14:00",
        endTime: "2025-10-20T14:40",
        duration: 40,
        baseFee: 3,
        perMinuteRate: 0.18,
        eBikeSurcharge: 0,
    },
    {
        tripId: "T004",
        rider: "John Doe",
        startStation: "Airport",
        endStation: "Downtown",
        bikeType: "E-Bike",
        cost: 20.0,
        startTime: "2025-10-25T10:00",
        endTime: "2025-10-25T10:50",
        duration: 50,
        baseFee: 5,
        perMinuteRate: 0.25,
        eBikeSurcharge: 5,
    },
    {
        tripId: "T005",
        rider: "John Doe",
        startStation: "Harbor",
        endStation: "Old Town",
        bikeType: "Standard",
        cost: 12.0,
        startTime: "2025-10-28T11:00",
        endTime: "2025-10-28T11:40",
        duration: 40,
        baseFee: 3,
        perMinuteRate: 0.18,
        eBikeSurcharge: 0,
    },
    {
        tripId: "T006",
        rider: "John Doe",
        startStation: "East Point",
        endStation: "University",
        bikeType: "Standard",
        cost: 13.0,
        startTime: "2025-10-30T12:00",
        endTime: "2025-10-30T12:45",
        duration: 45,
        baseFee: 3,
        perMinuteRate: 0.2,
        eBikeSurcharge: 0,
    },
];

export default function RideHistory({ user = { fullName: "John Doe" }, role = "rider" }) {
    const [search, setSearch] = useState("");
    const [bikeType, setBikeType] = useState("");
    const [startDate, setStartDate] = useState("");
    const [endDate, setEndDate] = useState("");
    const [filtered, setFiltered] = useState([]);
    const [selectedRide, setSelectedRide] = useState(null);
    const [trips, setTrips] = useState([]);
    // Pagination states
    const [currentPage, setCurrentPage] = useState(1);
    const ridesPerPage = 8;

    const fetchTripsForUser = () => {
        fetch("api/action/getAllTripsForUser", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: localStorage.getItem("username"), // sending the username in request body
        })
            .then((response) => {
                if (!response.ok) {
                    throw new Error("Failed to fetch trips");
                }
                return response.json(); // convert response to JSON
            })
            .then((data) => {
                console.log("Trips fetched:", data);
                setTrips(data); // store the list of trips
            })
            .catch((error) => {
                console.error("Error fetching trips:", error);
            });
    };

    useEffect(() => {
        fetchTripsForUser();
    }, []); // dependency ensures it refetches if username changes


    const handleSearch = () => {
        if (startDate && endDate && new Date(endDate) < new Date(startDate)) {
            alert("End date cannot be before start date.");
            return;
        }

        const tripIdPattern = /^T\d{3,}$/;
        if (search.trim() && !tripIdPattern.test(search.trim())) {
            alert("Invalid Trip ID format. Use format like T001.");
            return;
        }

        let results = mockData;

        if (role === "rider") {
            results = results.filter((r) => r.rider === user.fullName);
        }

        if (search.trim()) {
            results = results.filter((r) =>
                r.tripId.toLowerCase().includes(search.trim().toLowerCase())
            );
        }

        if (bikeType) {
            results = results.filter((r) => r.bikeType === bikeType);
        }

        if (startDate) {
            results = results.filter((r) => new Date(r.startTime) >= new Date(startDate));
        }
        if (endDate) {
            results = results.filter((r) => new Date(r.endTime) <= new Date(endDate));
        }

        results = results.sort((a, b) => new Date(b.endTime) - new Date(a.endTime));
        setFiltered(results);
        setCurrentPage(1); // Reset to first page on new search
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
                        placeholder="e.g. T001"
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
                                    <td className="px-4 py-2">${ride.cost.toFixed(2)}</td>
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
                    No results found. Clear filters or check the Trip ID.
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
                            <li><strong>Base Fee:</strong> ${selectedRide.baseFee}</li>
                            <li><strong>Per Minute Rate:</strong> ${selectedRide.perMinuteRate}</li>
                            {selectedRide.eBikeSurcharge > 0 && (
                                <li><strong>E-Bike Surcharge:</strong> ${selectedRide.eBikeSurcharge}</li>
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
