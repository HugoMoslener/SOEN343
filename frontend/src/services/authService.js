import {
  signInWithEmailAndPassword,
  createUserWithEmailAndPassword,
  signOut,
  onAuthStateChanged
} from 'firebase/auth';
import { auth } from '../firebase/firebaseConfig';

export const authService = {
  async register(email, password) {
    try {
      const userCredential = await createUserWithEmailAndPassword(auth, email, password);
      return {
        success: true,
        user: userCredential.user
      };
    } catch (error) {
      return {
        success: false,
        error: error.message
      };
    }
  },

  async login(email, password) {
    try {
      const userCredential = await signInWithEmailAndPassword(auth, email, password);
      return {
        success: true,
        user: userCredential.user
      };
    } catch (error) {
      return {
        success: false,
        error: error.message
      };
    }
  },

  async logout() {
    try {
      await signOut(auth);
      return { success: true };
    } catch (error) {
      return {
        success: false,
        error: error.message
      };
    }
  },

  getCurrentUser() {
    const user = auth.currentUser;
    if (!user) {
      console.warn("⚠️ [authService] No user currently logged in");
    } else {
      console.log("✅ [authService] Logged in user:", user.email || user.uid);
    }
    return user;
  },

  onAuthStateChanged(callback) {
    return onAuthStateChanged(auth, callback);
  }
};
