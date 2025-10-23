export async function reserveBike(payload) {
    const res = await fetch("/api/action/reserveBike", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
    });
    if (!res.ok) throw new Error("Bike reservation failed");
    return res.json();
}

export async function checkoutBike(payload){
    const res = await fetch("/api/action/undockBike", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
    });
    if (!res.ok) throw new Error("Bike undocking failed");
    return res.json();
}