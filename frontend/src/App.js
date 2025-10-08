import './App.css';
//import { BrowserRouter as Router, Routes, Route, Link } from "react-router-dom";
import HealthCheck from './HealthCheck';
export default function App() {
    return (
        <div style={{padding: 24}}>
            <h1>Proxy check</h1>
            <HealthCheck/>
        </div>
    );
}
