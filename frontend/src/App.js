import './App.css';
import { BrowserRouter as Router, Routes, Route, Link } from "react-router-dom";

function App() {
    return (
        <Router>
            <nav>
                {/* Links to navigate without reloading */}
                <Link to="/">Home</Link>
                <Link to="/test"></Link>
            </nav>

            <Routes>
                <Route path="/" element={<Home />} />
            </Routes>
        </Router>
    );
}

export default App;