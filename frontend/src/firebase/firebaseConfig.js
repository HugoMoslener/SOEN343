// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
import { getAuth } from "firebase/auth";
import { getFirestore } from "firebase/firestore";

// Your web app's Firebase configuration
const firebaseConfig = {
  apiKey: "AIzaSyDA3XEc3sUtIZAG8aYSVJyCWFpHeeqTf_s",
  authDomain: "topfounders-66244.firebaseapp.com",
  projectId: "topfounders-66244",
  storageBucket: "topfounders-66244.firebasestorage.app",
  messagingSenderId: "414833296425",
  appId: "1:414833296425:web:d5abde4e639e196599c838",
  measurementId: "G-3K92YB69C4"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);

// Initialize Auth and Firestore
export const auth = getAuth(app);
export const db = getFirestore(app);

export default app;