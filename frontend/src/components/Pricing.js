
import { useState } from "react"

export default function Pricing() {
    const [hoveredPlan, setHoveredPlan] = useState(null)

    const plans = [
        {
            name: "Base Plan",
            price: "$10",
            period: "/ Minute",
            description: "Perfect for beginners",
            features: ["Base Fee: 10$", "Additional E-Bike Fee: 20$"],
            gradient: "from-blue-500/20 to-cyan-500/20",
            borderGradient: "from-blue-500 to-cyan-500",
            buttonGradient: "from-blue-500 to-cyan-500",
            popular: false,
        },
        {
            name: "Premium plan",
            price: "$4",
            period: "/ Minute",
            description: "Best for professionals",
            features: ["Base Fee: 30$", "No additional E-Bike Fee"],
            gradient: "from-purple-500/20 to-pink-500/20",
            borderGradient: "from-purple-500 to-pink-500",
            buttonGradient: "from-purple-500 to-pink-500",
            popular: true,
        },
        {
            name: "Premium plan Pro",
            price: "$2",
            period: "/ Minute",
            description: "For large scale groups",
            features: ["Base Fee: 50$", "No additional E-Bike Fee"],
            gradient: "from-orange-500/20 to-red-500/20",
            borderGradient: "from-orange-500 to-red-500",
            buttonGradient: "from-orange-500 to-red-500",
            popular: false,
        },
    ]

    return (
        <div className="min-h-screen bg-gradient-to-br from-white via-slate-50 to-white py-20 px-4">
            <div className="max-w-7xl mx-auto">

                {/* Header */}
                <div className="text-center mb-16">
                    <h1 className="text-5xl md:text-6xl font-bold text-slate-900 mb-4 bg-gradient-to-r from-blue-600 via-purple-600 to-pink-600 bg-clip-text text-transparent">
                        Choose Your Plan
                    </h1>
                    <p className="text-xl text-slate-600 max-w-2xl mx-auto">
                        Select the perfect plan for your needs. Upgrade or downgrade at any time.
                    </p>
                </div>

                {/* Pricing Cards */}
                <div className="grid md:grid-cols-3 gap-8 max-w-6xl mx-auto">
                    {plans.map((plan, index) => (
                        <div
                            key={index}
                            className={`relative group ${plan.popular ? "md:scale-105 z-10" : ""}`}
                            onMouseEnter={() => setHoveredPlan(index)}
                            onMouseLeave={() => setHoveredPlan(null)}
                        >
                            {plan.popular && (
                                <div className="absolute -top-5 left-1/2 -translate-x-1/2 z-20">
                                    <div className="bg-gradient-to-r from-purple-500 to-pink-500 text-white px-6 py-2 rounded-full text-sm font-semibold shadow-lg">
                                        Most Popular
                                    </div>
                                </div>
                            )}

                            {/* Card */}
                            <div
                                className={`relative h-full bg-gradient-to-br ${plan.gradient} backdrop-blur-xl rounded-2xl border-2 border-transparent transition-all duration-500 overflow-hidden ${
                                    hoveredPlan === index ? "scale-105 shadow-2xl shadow-gray-300/30" : "shadow-md"
                                }`}
                            >
                                <div
                                    className={`relative bg-white/90 backdrop-blur-xl rounded-2xl p-8 h-full border border-slate-200 group-hover:border-slate-300 transition-colors duration-500`}
                                >
                                    <h3 className="text-2xl font-bold text-slate-900 mb-2">{plan.name}</h3>
                                    <p className="text-slate-600 text-sm mb-6">{plan.description}</p>

                                    <div className="mb-8">
                                        <div className="flex items-baseline">
                      <span
                          className={`text-5xl font-bold bg-gradient-to-r ${plan.borderGradient} bg-clip-text text-transparent`}
                      >
                        {plan.price}
                      </span>
                                            <span className="text-slate-500 ml-2">{plan.period}</span>
                                        </div>
                                    </div>

                                    <ul className="space-y-4 mb-8">
                                        {plan.features.map((feature, featureIndex) => (
                                            <li key={featureIndex} className="flex items-start text-slate-700 group/item">
                                                <div
                                                    className={`flex-shrink-0 w-6 h-6 rounded-full bg-gradient-to-br ${plan.borderGradient} flex items-center justify-center mr-3 mt-0.5 group-hover/item:scale-110 transition-transform duration-300`}
                                                >
                                                    <svg
                                                        className="w-4 h-4 text-white"
                                                        fill="none"
                                                        strokeLinecap="round"
                                                        strokeLinejoin="round"
                                                        strokeWidth="3"
                                                        viewBox="0 0 24 24"
                                                        stroke="currentColor"
                                                    >
                                                        <path d="M5 13l4 4L19 7" />
                                                    </svg>
                                                </div>
                                                <span className="group-hover/item:text-slate-900 transition-colors duration-300">
                          {feature}
                        </span>
                                            </li>
                                        ))}
                                    </ul>

                                    <button
                                        className={`w-full bg-gradient-to-r ${plan.buttonGradient} hover:opacity-90 text-white font-semibold py-6 rounded-xl shadow-md transition-all duration-300 hover:shadow-lg hover:-translate-y-1 border-0`}
                                    >
                                        Get Started
                                    </button>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>

                {/* Footer Note */}
                <div className="text-center mt-16">
                    <p className="text-slate-600">Payment Information required for all plans. Only credit card payment is accepted.</p>
                </div>
            </div>
        </div>
    )
}
