

import { useState,useEffect  } from "react"

export default function Billing() {
    const [openIndex, setOpenIndex] = useState(null)
    const [tripSummary,setTripSummary] = useState(true);
    const [isTripSummary,setIsTripSummary] = useState(true);
    const [showPayment, setShowPayment] = useState(true);
    const [trips, setTrips] = useState(null);
    const [paymentData, setPaymentData] = useState({
        cardNumber: "",
        expiry: "",
        cvv: "",
        name: "",});

    const fetchStations = () => {
        fetch("/api/action/getAllTripsForUser", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: localStorage.getItem("username"),
        })
            .then((r) => {
                if (!r.ok) throw new Error("Network response was not ok");
                return r.json();
            })
            .then((data) => {
                console.log("Received stations:", data);
                setTrips(JSON.parse(data));
            })
            .catch((e) => console.error("Error fetching stations:", e));
    };

    useEffect(() => {
        fetchStations();
    }, []);

    const items = [
        {
            title: "Order #1023",
            date: "2025-10-30",
            amount: "$125.00",
            status: "Delivered",
            details: [
                { name: "Product A", qty: 2, price: "$40" },
                { name: "Product B", qty: 1, price: "$45" },
            ],
        },
        {
            title: "Order #1022",
            date: "2025-10-28",
            amount: "$89.00",
            status: "Processing",
            details: [
                { name: "Product X", qty: 1, price: "$50" },
                { name: "Product Y", qty: 2, price: "$19.50" },
            ],
        },
        {
            title: "Order #1021",
            date: "2025-10-27",
            amount: "$260.00",
            status: "Cancelled",
            details: [
                { name: "Product Z", qty: 4, price: "$65" },
            ],
        },
    ]

    const toggleOpen = (index) => {
        setOpenIndex(openIndex === index ? null : index)
    }
    const handleCloseTripSummary = () => {
        setIsTripSummary(false);
    };

    const handlePaymentSubmit = (e) => {
        e.preventDefault();
        alert("Payment submitted successfully!");
        setShowPayment(false);
    };

    const handleInputChange = (e) => {
        setPaymentData({ ...paymentData, [e.target.name]: e.target.value });
    };

    return (

        <div className="p-6">
            {/* --- TRIP SUMMARY SECTION --- */}
            {isTripSummary && (
                <div className="bg-white border border-gray-200 rounded-2xl shadow-md p-5 w-full max-w-4xl mx-auto">
                    <h2 className="text-2xl font-semibold text-gray-800 mb-4 text-center">
                        Trip Summary
                    </h2>

                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 text-gray-700 text-sm mb-6">
                        <p>
                            <span className="font-medium">Trip ID:</span> T1023
                        </p>
                        <p>
                            <span className="font-medium">Origin:</span> Station A
                        </p>
                        <p>
                            <span className="font-medium">Arrival:</span> Station B
                        </p>
                        <p>
                            <span className="font-medium">Plan:</span> Premium
                        </p>
                        <p>
                            <span className="font-medium">Payment:</span> Credit Card
                        </p>
                    </div>

                    <div className="flex justify-center">
                        <button
                            onClick={handleCloseTripSummary}
                            className="bg-red-500 text-white hover:bg-red-600 rounded-lg px-4 py-2 transition font-medium"
                        >
                            Close
                        </button>
                    </div>
                </div>
            )}

            {/* --- PAYMENT SECTION --- */}
            {showPayment && (
                <div className="mt-10 bg-white border border-gray-200 rounded-2xl shadow-md p-6 w-full max-w-md mx-auto">
                    <h2 className="text-xl font-semibold text-gray-800 mb-4 text-center">
                        Credit Card Payment
                    </h2>
                    <form onSubmit={handlePaymentSubmit} className="space-y-4">
                        <div>
                            <label className="block text-gray-700 text-sm mb-1">
                                Cardholder Name
                            </label>
                            <input
                                type="text"
                                name="name"
                                value={paymentData.name}
                                onChange={handleInputChange}
                                required
                                className="w-full border border-gray-300 rounded-lg px-3 py-2"
                            />
                        </div>
                        <div>
                            <label className="block text-gray-700 text-sm mb-1">
                                Card Number
                            </label>
                            <input
                                type="text"
                                name="cardNumber"
                                value={paymentData.cardNumber}
                                onChange={handleInputChange}
                                required
                                className="w-full border border-gray-300 rounded-lg px-3 py-2"
                                maxLength="16"
                            />
                        </div>
                        <div className="flex gap-4">
                            <div className="flex-1">
                                <label className="block text-gray-700 text-sm mb-1">
                                    Expiry Date
                                </label>
                                <input
                                    type="text"
                                    name="expiry"
                                    value={paymentData.expiry}
                                    onChange={handleInputChange}
                                    placeholder="MM/YY"
                                    required
                                    className="w-full border border-gray-300 rounded-lg px-3 py-2"
                                />
                            </div>
                            <div className="flex-1">
                                <label className="block text-gray-700 text-sm mb-1">CVV</label>
                                <input
                                    type="text"
                                    name="cvv"
                                    value={paymentData.cvv}
                                    onChange={handleInputChange}
                                    required
                                    maxLength="3"
                                    className="w-full border border-gray-300 rounded-lg px-3 py-2"
                                />
                            </div>
                        </div>
                        <button
                            type="submit"
                            className="w-full bg-blue-600 text-white py-2 rounded-lg font-medium hover:bg-blue-700 transition"
                        >
                            Confirm Payment
                        </button>
                    </form>
                </div>
            )}
        </div>
    );
}
