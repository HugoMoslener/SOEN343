import React from "react";
import "./App.css";

function App() {
    return (
        <div className="App" style={{ textAlign: "center", marginTop: "100px" }}>
            <h1>Welcome to My React App</h1>
            <p>This is the default homepage.</p>
            <button
                onClick={() => alert("Hello from React!")}
                style={{
                    padding: "10px 20px",
                    fontSize: "16px",
                    borderRadius: "8px",
                    cursor: "pointer",
                }}
            >
                Click Me
            </button>
        </div>
    );
}

export default App;