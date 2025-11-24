import React, {useEffect, useState} from "react";

export default function AccountInfo() {

    const [showPaymentModal, setShowPaymentModal] = useState(false);
    const [paymentInfo, setPaymentInfo] = useState("");
    const [showOptions, setShowOptions] = useState(false);
    const [tierInfo, setTierInfo] = useState(null);
    const [tierLoading, setTierLoading] = useState(false);
    const username = localStorage.getItem("username");
    const role = localStorage.getItem("role");

    // Fetch tier info on component mount
    useEffect(() => {
        if (role === "rider") {
            fetchTierInfo();
        }
    }, [role]);

    // Fetch Flex Dollars for riders
    useEffect(() => {
        if(localStorage.getItem("role") === "rider"){
            const fetchFlexDollars = async () => {
                try {
                    const response = await fetch("/api/action/getFlexDollars", {
                        method: "POST",
                        headers: { "Content-Type": "text/plain" },
                        body: localStorage.getItem("username"),
                    });

                    const data = await response.text(); // backend returns plain string
                    if (data && data !== "false") {
                        localStorage.setItem("flexMoney", data);
                    } else {
                        localStorage.setItem("flexMoney","0");
                    }
                } catch (error) {
                    console.error("Error fetching FlexDollars:", error);
                    localStorage.setItem("flexMoney", "0");
                }
            };
            fetchFlexDollars();}
    }, []);

    const fetchTierInfo = async () => {
        try {
            setTierLoading(true);
            const response = await fetch("/api/signIn/getUserData", {
                method: "POST",
                headers: { "Content-Type": "text/plain" },
                body: username
            });

            if (response.ok) {
                const userData = await response.json();
                if (userData.tier) {
                    setTierInfo({
                        tier: userData.tier,
                        discount: getTierDiscount(userData.tier),
                        holdExtension: getTierHoldExtension(userData.tier)
                    });
                }
            }
        } catch (error) {
            console.error("Error fetching tier info:", error);
        } finally {
            setTierLoading(false);
        }
    };

    // Helper function to get discount percentage by tier
    const getTierDiscount = (tier) => {
        switch(tier) {
            case "BRONZE":
                return 5;
            case "SILVER":
                return 10;
            case "GOLD":
                return 15;
            default:
                return 0;
        }
    };

    // Helper function to get hold extension by tier
    const getTierHoldExtension = (tier) => {
        switch(tier) {
            case "SILVER":
                return 2;
            case "GOLD":
                return 5;
            default:
                return 0;
        }
    };

    // Get tier badge color and styling
    const getTierBadgeStyle = (tier) => {
        switch(tier) {
            case "GOLD":
                return "bg-yellow-100 text-yellow-800 border-yellow-300";
            case "SILVER":
                return "bg-gray-100 text-gray-800 border-gray-300";
            case "BRONZE":
                return "bg-orange-100 text-orange-800 border-orange-300";
            default:
                return "bg-blue-100 text-blue-800 border-blue-300";
        }
    };

    const handleSwitchToRider = async () => {
        const response = await fetch(`/api/operator/${username}/rider`);
        const data = await response.json();

        if (data === null) {
            // operator has NO rider → ask for payment
            setShowPaymentModal(true);
        } else {
            // operator DOES have rider → switch immediately
            switchToRider(data);
        }
    };

    const createRider = async () => {
        if (!paymentInfo) {
            alert("Please select a payment method.");
            return;
        }

        const response = await fetch(`/api/operator/${username}/createRider`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ paymentInformation: paymentInfo })
        });

        const newRider = await response.json();

        setShowPaymentModal(false);
        switchToRider(newRider);
    };

    const switchToRider = async (rider) => {
        // Update local storage
        localStorage.setItem("username", rider.username);
        localStorage.setItem("role", "rider");
        localStorage.setItem("email", rider.email);
        localStorage.setItem("fullName", rider.fullName);
        localStorage.setItem("address", rider.address);
        localStorage.setItem("flexMoney", rider.flexMoney);

        // IMPORTANT: Tell backend about new user
        await fetch("/api/signIn/getUserData", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(rider.username)
        });

        // Redirect
        window.location.href = "/account";
    };

    const handleSwitchToOperator = async () => {
        const username = localStorage.getItem("username");

        if (!username.includes("operator")) {
            alert("This rider cannot switch to an operator.");
            return;
        }

        try {
            const response = await fetch("/api/operator/switchToOperator", {
                method: "POST",
                headers: { "Content-Type": "text/plain" },
                body: username
            });

            if (!response.ok) {
                alert("Could not switch to operator.");
                return;
            }

            const operator = await response.json();

            // Update local storage
            localStorage.setItem("username", operator.username);
            localStorage.setItem("role", "operator");
            localStorage.setItem("email", operator.email);
            localStorage.setItem("fullName", operator.fullName);
            localStorage.setItem("address", operator.address);

            // Sync backend
            await fetch("/api/signIn/getUserData", {
                method: "POST",
                headers: { "Content-Type": "text/plain" },
                body: operator.username
            });

            // Redirect to operator dashboard
            window.location.href = "/account";
        } catch (err) {
            console.error(err);
            alert("Error switching back to operator.");
        }
    };

    return (
        <div className="min-h-screen w-full bg-gradient-to-br from-blue-50 to-indigo-100 flex flex-col">

            {/* Header */}
            <header className="bg-white shadow-sm py-4 px-8 flex justify-between items-center">
                <h1 className="text-xl font-semibold text-gray-800">
                    Account Information
                </h1>

                <p className="text-sm text-gray-500">
                    Logged in as{" "}
                    <span className="font-medium text-indigo-600">
                        {localStorage.getItem("username")}
                    </span>
                </p>
            </header>

            {/* Main Content */}
            <main className="flex-grow flex flex-col items-center justify-start text-center px-6 py-10 space-y-6">

                <div className="w-full max-w-xl bg-white shadow-lg rounded-xl p-6 border border-gray-200">
                    <h2 className="text-2xl font-semibold text-gray-800 mb-4">
                        Your Account Details
                    </h2>

                    {/* This is where you'll insert specific account data later */}
                    <div className="text-left space-y-3">
                        <div>
                            <span className="font-semibold text-gray-800">Username:</span>
                            <span className="ml-2 text-gray-700">{localStorage.getItem("username")}</span>
                        </div>

                        <div>
                            <span className="font-semibold text-gray-800">Email:</span>
                            <span className="ml-2 text-gray-700">{localStorage.getItem("email")}</span>
                        </div>

                        <div>
                            <span className="font-semibold text-gray-800">Full Name:</span>
                            <span className="ml-2 text-gray-700">{localStorage.getItem("fullName")}</span>
                        </div>

                        <div>
                            <span className="font-semibold text-gray-800">Address:</span>
                            <span className="ml-2 text-gray-700">{localStorage.getItem("address")}</span>
                        </div>

                        <div>
                            <span className="font-semibold text-gray-800">Role:</span>
                            <span className="ml-2 text-gray-700">{localStorage.getItem("role")}</span>
                        </div>

                        {localStorage.getItem("role") === "rider" && (
                            <div>
                                <span className="font-semibold text-gray-800">Flex Dollars:</span>
                                <span className="ml-2 text-gray-700">{localStorage.getItem("flexMoney")}</span>
                            </div>
                        )}
                    </div>
                </div>

                {/* Tier Information Card - Only show for riders */}
                {localStorage.getItem("role") === "rider" && (
                    <div className="w-full max-w-xl bg-white shadow-lg rounded-xl p-6 border border-gray-200">
                        <h2 className="text-2xl font-semibold text-gray-800 mb-4">
                            Loyalty Program
                        </h2>

                        {tierLoading ? (
                            <p className="text-gray-600">Loading tier information...</p>
                        ) : tierInfo ? (
                            <div className="space-y-4">
                                {/* Tier Badge */}
                                <div className="flex items-center justify-center">
                                    <span className={`px-6 py-2 rounded-full border-2 font-bold text-lg ${getTierBadgeStyle(tierInfo.tier)}`}>
                                        {tierInfo.tier}
                                    </span>
                                </div>

                                {/* Tier Benefits */}
                                <div className="bg-blue-50 rounded-lg p-4 space-y-2">
                                    <h3 className="font-semibold text-gray-800 mb-3">Current Benefits:</h3>

                                    <div className="flex items-center space-x-2">
                                        <span className="text-green-600">✓</span>
                                        <span className="text-gray-700">
                                            <strong>Discount:</strong> {tierInfo.discount}% off each ride
                                        </span>
                                    </div>

                                    <div className="flex items-center space-x-2">
                                        <span className="text-green-600">✓</span>
                                        <span className="text-gray-700">
                                            <strong>Reservation Hold:</strong> {tierInfo.holdExtension > 0 ? `${tierInfo.holdExtension} extra minutes` : "Standard 5 minutes"}
                                        </span>
                                    </div>
                                </div>

                                {/* Tier Requirements Info */}
                                <div className="bg-gray-50 rounded-lg p-4">
                                    <h3 className="font-semibold text-gray-800 mb-2">How to progress:</h3>
                                    <ul className="text-sm text-gray-600 space-y-1 list-disc list-inside">
                                        <li><strong>BRONZE:</strong> 10+ trips, no cancellations, return all bikes</li>
                                        <li><strong>SILVER:</strong> BRONZE + 5 confirmed reservations + 5+ trips/month</li>
                                        <li><strong>GOLD:</strong> SILVER + 5+ trips every week</li>
                                    </ul>
                                </div>

                                {/* Refresh Button */}
                                <button
                                    onClick={fetchTierInfo}
                                    className="w-full mt-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition font-medium"
                                >
                                    Refresh Tier Info
                                </button>
                            </div>
                        ) : (
                            <p className="text-gray-600">Unable to load tier information</p>
                        )}
                    </div>
                )}

                <div className="w-full max-w-xl bg-white shadow-lg rounded-xl p-6 border border-gray-200 mt-4 space-y-4">
                    <h2 className="text-xl font-semibold text-gray-800">Switch Role</h2>

                    {/* Switch to Rider */}
                    {localStorage.getItem("role") === "operator" && (
                        <button
                            className="w-full py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition"
                            onClick={handleSwitchToRider}
                        >
                            Switch to Rider
                        </button>
                    )}

                    {/* Switch to Operator */}
                    {localStorage.getItem("role") === "rider" &&
                        localStorage.getItem("username").includes("operator") && (
                            <button
                                onClick={handleSwitchToOperator}
                                className="w-full py-2 bg-gray-700 text-white rounded-lg hover:bg-gray-800 transition"
                            >
                                Switch to Operator
                            </button>
                        )}
                </div>

                {showPaymentModal && (
                    <div>
                        <h3>Enter Payment Info</h3>

                        <button onClick={() => setShowOptions(!showOptions)}>
                            {paymentInfo || "Select card type"}
                        </button>

                        {showOptions && (
                            <div>
                                {["Visa", "MasterCard", "American Express", "Discover"].map((option) => (
                                    <div
                                        key={option}
                                        onClick={() => {
                                            setPaymentInfo(option);
                                            setShowOptions(false);
                                        }}
                                    >
                                        {option}
                                    </div>
                                ))}
                            </div>
                        )}

                        <button onClick={createRider}>Confirm</button>
                        <button onClick={() => setShowPaymentModal(false)}>Cancel</button>
                    </div>
                )}

            </main>

            {/* Footer */}
            <footer className="bg-white border-t border-gray-200 py-4 text-center text-sm text-gray-500">
                © {new Date().getFullYear()} TopFounders. All rights reserved.
            </footer>

        </div>
    );
}