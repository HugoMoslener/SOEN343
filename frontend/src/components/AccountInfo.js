import React, { useState } from "react";

export default function AccountInfo() {

    const [showPaymentModal, setShowPaymentModal] = useState(false);
    const [paymentInfo, setPaymentInfo] = useState("");
    const [showOptions, setShowOptions] = useState(false);
    const username = localStorage.getItem("username");

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