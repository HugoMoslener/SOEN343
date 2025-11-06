

import { useState,useEffect  } from "react";
import { useNavigate } from "react-router-dom";

const mockCards = [
    {
        type: "Visa",
        cardNumber: "4111111111111111",
        expiry: "12/25",
        cvv: "123",
        name: "John Doe",
    },
    {
        type: "Visa",
        cardNumber: "4111111111111112",
        expiry: "12/26",
        cvv: "321",
        name: "Jane Doe",
    },
    {
        type: "MasterCard",
        cardNumber: "5500000000000004",
        expiry: "11/26",
        cvv: "456",
        name: "Jane Smith",
    },
    {
        type: "American Express",
        cardNumber: "340000000000009",
        expiry: "10/25",
        cvv: "789",
        name: "Alice Johnson",
    },
    {
        type: "Discover",
        cardNumber: "6011000000000004",
        expiry: "09/26",
        cvv: "321",
        name: "Bob Brown",
    },
];


export default function Billing() {
    const [openIndex, setOpenIndex] = useState(null);
    const [tripSummary,setTripSummary] = useState(null);
    const [isTripSummary,setIsTripSummary] = useState(false);
    const [paymentReceipt,setPaymentReceipt] = useState(null);
    const [isReceipt,setIsReceipt] = useState(false);
    const [showPayment, setShowPayment] = useState(false);
    const navigate = useNavigate();
    const [billingSummaries, setBillingSummaries] = useState([]);
    const [showBilling, setShowBilling] = useState(false);
    const [paymentData, setPaymentData] = useState({
        cardNumber: "",
        expiry: "",
        cvv: "",
        name: "",});
    const today = new Date();

    // Get year, month, and day
    const year = today.getFullYear();
    const month = today.getMonth() + 1; // Months are 0-indexed
    const day = today.getDate();
    const fetchStations = () => {
        fetch("/api/action/getAllTripsForUser", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: localStorage.getItem("username") ,
        })
            .then((r) => {
                if (!r.ok) throw new Error("Network response was not ok");
                return r.json();
            })
            .then((data) => {
                const summaries = data.map((trip) => ({
                    tripID: trip.tripID,
                    startTime: trip.startTime,
                    endTime: trip.endTime,
                    bikeID: trip.reservation?.bike?.bikeID,
                    origin: trip.origin,
                    arrival: trip.arrival,
                    charges: {
                        baseFee: trip.pricingPlan?.baseFee,
                        ratePerMinute: trip.pricingPlan?.ratePerMinute,
                        amountPaid: trip.payment?.amount,
                        paymentDate: trip.payment?.paidDate,
                        paymentMethod: trip.payment?.paymentMethod,
                    },
                }));
                setBillingSummaries(data);
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
            setPaymentReceipt(JSON.parse(localStorage.getItem("TripSummary")));

        }
    }, []);

    const handlePayment =  async () => {


        try {
            const res =  await fetch("/api/action/confirmPayment", {
                method: "POST",
                headers: {
                    "Content-Type": "text/plain", // send a plain string
                },
                body: String(tripSummary.tripID), // your string data
            });

            const text = await res.text();
            localStorage.setItem("IsTripSummary","false");
            localStorage.setItem("TripSummary", "");
            setShowPayment(false);
            setIsTripSummary(false);
            setTripSummary(null);
            localStorage.setItem("IsTripSummary","false");
            localStorage.setItem("IsTripSummary", "");
            localStorage.setItem("TripSummary", "");
            alert("Payment successfully confirmed");
            fetchStations();

        } catch (error) {
            alert("Payment Unsuccessful");
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

    const handlePaymentSubmit =  async (e) => {
        e.preventDefault();

        const matchedCard = mockCards.find(
            (card) =>
                card.type.toLowerCase() === localStorage.getItem("paymentInformation").toLowerCase() &&
                card.cardNumber === paymentData.cardNumber.replace(/\s+/g, "") &&
                card.expiry === paymentData.expiry.replace(/\s+/g, "") &&
                card.cvv === paymentData.cvv.replace(/\s+/g, "") &&
                card.name.toLowerCase().replace(/\s+/g, "") === paymentData.name.toLowerCase().replace(/\s+/g, "")
        );

        if (!matchedCard) {
            alert("Payment failed: Invalid card information!");
            return; // Stop further processing
        }

        await handlePayment();



        setIsReceipt(true);

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
            {/* Payment Receipt */}
            {isReceipt && ( <div className="mt-6 bg-white border border-gray-200 rounded-2xl shadow-md p-5 w-full max-w-4xl mx-auto">

                <h2 className="text-2xl font-semibold text-gray-800 mb-4 text-center">
                    Payment Receipt
                </h2>
                {/* General Trip Info */}
                <div className="border-t border-gray-200 pt-4 mt-4">
                    <h3 className="text-lg font-semibold text-gray-800 mb-2">Base Information</h3>
                    <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4 text-gray-700 text-sm mb-6">

                        <div>
                            <p><span className="font-medium">Trip ID:</span> {paymentReceipt.tripID}</p>
                        </div>
                        <div>
                            <p><span className="font-medium">Start Time:</span> {paymentReceipt.startTime}</p>
                        </div>
                        <div>
                            <p><span className="font-medium">End Time:</span> {paymentReceipt.endTime}</p>
                        </div>
                        <div>
                            <p><span className="font-medium">Origin:</span> {paymentReceipt.origin}</p>
                        </div>
                        <div>
                            <p><span className="font-medium">Arrival:</span> {paymentReceipt.arrival}</p>
                        </div>
                        <div>
                            <p><span className="font-medium">Bike ID:</span> {paymentReceipt.reservation?.bike?.bikeID}</p>
                        </div>
                        <div>
                            <p><span className="font-medium">Bike Type:</span> {paymentReceipt.reservation?.bike?.type}</p>
                        </div>
                    </div>
                </div>

                {/* Pricing Plan Section */}
                <div className="border-t border-gray-200 pt-4 mt-4">
                    <h3 className="text-lg font-semibold text-gray-800 mb-2">Pricing Plan</h3>
                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 text-gray-700 text-sm">
                        <div>
                            <p><span className="font-medium">Plan Name:</span> {paymentReceipt.pricingPlan?.planName || "N/A"}</p>
                        </div>
                        <div>
                            <p><span className="font-medium">Rate per Minute:</span>
                                {paymentReceipt.pricingPlan?.ratePerMinute
                                    ? `$${paymentReceipt.pricingPlan.ratePerMinute}`
                                    : "N/A"}
                            </p>
                        </div>
                        <div>
                            <p><span className="font-medium">Base Fee:</span>
                                {paymentReceipt.pricingPlan?.baseFee
                                    ? `$${paymentReceipt.pricingPlan.baseFee}`
                                    : "N/A"}
                            </p>
                        </div>
                        <div>
                            <p><span className="font-medium">E-Bike Surcharge Fee:</span>
                                {paymentReceipt.pricingPlan?.planName === "Base plan"
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
                            <p><span className="font-medium">Name of the Client: </span>
                                {localStorage.getItem("fullName")}
                            </p>
                        </div>
                        <div>
                            <p><span className="font-medium">Billing Address: </span>
                                {localStorage.getItem("address")}
                            </p>
                        </div>

                        <div>
                            <p><span className="font-medium">Payment Method:</span> {paymentReceipt.payment?.paymentMethod || "N/A"}</p>
                        </div>
                        <div>
                            <p><span className="font-medium">Paid Date:</span> {year}-{month.toString().padStart(2, "0")}-{day.toString().padStart(2, "0")}</p>
                        </div>
                        <div>
                            <p><span className="font-medium">Total Amount:</span>
                                {paymentReceipt.payment?.amount
                                    ? `$${paymentReceipt.payment.amount.toFixed(2)}`
                                    : "N/A"}
                            </p>
                        </div>

                    </div>
                </div>
                <br/>
                <div className="flex justify-center">
                    <button
                        onClick={() => {setIsReceipt(false); setPaymentReceipt(null);}}
                        className="bg-red-500 text-white hover:bg-red-600 rounded-lg px-4 py-2 transition font-medium">
                        Close
                    </button>
                </div>
            </div>)}
            <div className="mt-10 space-y-8">
                <div className="flex flex-col items-center">
                    <h2 className="text-2xl font-semibold text-gray-800 text-center mb-4">
                        Billing Overview
                    </h2>
                    <div className="flex justify-center mt-10">
                        <button
                            onClick={() => navigate("/ridehistory")}
                            className="flex flex-col items-center justify-center w-64 h-32 bg-gradient-to-b from-blue-500 to-blue-700 text-white rounded-xl shadow-lg hover:scale-105 transform transition duration-300"
                        >
                            <span className="text-lg font-bold">Go to Ride History</span>
                        </button>
                    </div>
                    <br/>
                    <button
                        onClick={() => setShowBilling((prev) => !prev)}
                        className={`px-6 py-2 rounded-lg font-medium text-white transition ${
                            showBilling
                                ? "bg-red-500 hover:bg-red-600"
                                : "bg-blue-500 hover:bg-blue-600"
                        }`}
                    >
                        {showBilling ? "Hide Billing List" : "Show Billing List"}
                    </button>
                </div>

                {showBilling && (
                    <div className="space-y-8 mt-6">
                        {billingSummaries.length === 0 ? (
                            <p className="text-center text-gray-500 mt-6">
                                No billing summaries found.
                            </p>
                        ) : (
                            billingSummaries
                                .filter((trip) => trip.payment?.paidDate !== null)
                                .sort((a, b) => new Date(b.payment.paidDate) - new Date(a.payment.paidDate))// ✅ filter first
                                .map((trip, index) => (
                                    <div
                                        key={trip.tripID}
                                        className="bg-white border border-gray-200 rounded-2xl shadow-md p-6 w-full max-w-4xl mx-auto transition hover:shadow-lg hover:-translate-y-1 duration-300 animate-fade-in"
                                        style={{ animationDelay: `${index * 0.1}s` }}
                                    >
                                        {/* Trip Header */}
                                        <div className="flex justify-between items-center mb-4">
                                            <h3 className="text-xl font-semibold text-gray-800">
                                                Trip #{index + 1}
                                            </h3>
                                            <p className="text-sm text-gray-500">Trip ID: {trip.tripID}</p>
                                        </div>

                                        {/* Base Information */}
                                        <div className="border-t border-gray-200 pt-4 mt-4">
                                            <h3 className="text-lg font-semibold text-gray-800 mb-2">
                                                Base Information
                                            </h3>
                                            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4 text-gray-700 text-sm mb-6">
                                                <p><span className="font-medium">Start Time:</span> {trip.startTime || "N/A"}</p>
                                                <p><span className="font-medium">End Time:</span> {trip.endTime || "N/A"}</p>
                                                <p><span className="font-medium">Reservation Date:</span> {trip.reservation?.date || "N/A"}</p>
                                                <p><span className="font-medium">Origin:</span> {trip.origin || "N/A"}</p>
                                                <p><span className="font-medium">Arrival:</span> {trip.arrival || "N/A"}</p>
                                                <p><span className="font-medium">Reservation State:</span> {trip.reservation?.state || "N/A"}</p>
                                            </div>
                                        </div>

                                        {/* Bike Information */}
                                        <div className="border-t border-gray-200 pt-4 mt-4">
                                            <h3 className="text-lg font-semibold text-gray-800 mb-2">
                                                Bike Information
                                            </h3>
                                            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 text-gray-700 text-sm mb-6">
                                                <p><span className="font-medium">Bike ID:</span> {trip.reservation?.bike?.bikeID || "N/A"}</p>
                                                <p><span className="font-medium">Type:</span> {trip.reservation?.bike?.type || "N/A"}</p>
                                            </div>
                                        </div>

                                        {/* Pricing Plan */}
                                        <div className="border-t border-gray-200 pt-4 mt-4">
                                            <h3 className="text-lg font-semibold text-gray-800 mb-2">
                                                Pricing Plan
                                            </h3>
                                            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 text-gray-700 text-sm mb-6">
                                                <p><span className="font-medium">Plan Name:</span> {trip.pricingPlan?.planName || "N/A"}</p>
                                                <p><span className="font-medium">Base Fee:</span> {trip.pricingPlan?.baseFee ? `$${trip.pricingPlan.baseFee}` : "N/A"}</p>
                                                <p><span className="font-medium">Rate/Minute:</span> {trip.pricingPlan?.ratePerMinute ? `$${trip.pricingPlan.ratePerMinute}` : "N/A"}</p>
                                                <p><span className="font-medium">E-Bike Fee:</span> {trip.reservation?.bike?.type === "E_BIKE" ? "$20" : "N/A"}</p>
                                            </div>
                                        </div>

                                        {/* Payment Information */}
                                        <div className="border-t border-gray-200 pt-4 mt-4">
                                            <h3 className="text-lg font-semibold text-gray-800 mb-2">
                                                Payment Information
                                            </h3>
                                            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 text-gray-700 text-sm">
                                                <p><span className="font-medium">Payment Method:</span> {trip.payment?.paymentMethod || "N/A"}</p>
                                                <p><span className="font-medium">Paid Date:</span> {trip.payment?.paidDate || "N/A"}</p>
                                                <p><span className="font-medium">Amount:</span> {trip.payment?.amount ? `$${trip.payment.amount.toFixed(2)}` : "N/A"}</p>
                                            </div>
                                        </div>
                                    </div>
                                ))
                        )}
                    </div>
                )}

            </div>




        </div>
    );
}
