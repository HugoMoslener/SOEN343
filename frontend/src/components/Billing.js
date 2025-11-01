

import { useState,useEffect  } from "react";
import { useNavigate } from "react-router-dom";

export default function Billing() {
    const [openIndex, setOpenIndex] = useState(null);
    const [tripSummary,setTripSummary] = useState(null);
    const [isTripSummary,setIsTripSummary] = useState(false);
    const [showPayment, setShowPayment] = useState(false);
    const navigate = useNavigate();
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

            })
            .catch((e) => console.error("Error fetching stations:", e));
    };

    useEffect(() => {
        fetchStations();
        if(localStorage.getItem("IsTripSummary") === "true"){
            setIsTripSummary(true);
            setShowPayment(true);
            setTripSummary(JSON.parse(localStorage.getItem("TripSummary")));

        }
    }, []);

    const handlePayment =  () => {

        try {
            const res =  fetch("/api/action/confirmPayment", {
                method: "POST",
                headers: {
                    "Content-Type": "text/plain", // send a plain string
                },
                body: String(tripSummary.tripID), // your string data
            });

            const text =  res.text();
            setShowPayment(false);
            setIsTripSummary(false);
            setTripSummary(null);
            localStorage.setItem("IsTripSummary","false");
            localStorage.setItem("IsTripSummary", "");
            localStorage.setItem("TripSummary", "");
        } catch (error) {
            console.error("Payment request failed:", error);
        }
    };

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

    const handlePaymentSubmit =  () => {
        localStorage.setItem("IsTripSummary","false");
        localStorage.setItem("TripSummary", "");
        handlePayment();

    };

    const handleInputChange = (e) => {
        setPaymentData({ ...paymentData, [e.target.name]: e.target.value });
    };

    return (

        <div className="p-6">
            {/* --- TRIP SUMMARY SECTION --- */}
            {isTripSummary && ( <div className="mt-6 bg-white border border-gray-200 rounded-2xl shadow-md p-5 w-full max-w-4xl mx-auto">

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
                            <p><span className="font-medium">Plan Name:</span> {tripSummary.pricingPlan?.planName || "N/A"}</p>
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
                        onClick={() => {setIsTripSummary(false); localStorage.setItem("IsTripSummary","false"); }}
                        className="bg-red-500 text-white hover:bg-red-600 rounded-lg px-4 py-2 transition font-medium">
                        Close
                    </button>
                </div>
            </div>)}

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
            <div className="flex justify-center mt-10">
                <button
                    onClick={() => navigate("/ridehistory")}
                    className="flex flex-col items-center justify-center w-64 h-32 bg-gradient-to-b from-blue-500 to-blue-700 text-white rounded-xl shadow-lg hover:scale-105 transform transition duration-300"
                >
                    <span className="text-lg font-bold">Go to Ride History</span>
                </button>
            </div>
        </div>
    );
}
