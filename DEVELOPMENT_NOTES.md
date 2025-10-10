# SOEN 343 Project - Development Notes

## Current Status ✅
- **Backend**: Spring Boot running on port 8080
- **Frontend**: React app running on port 3001
- **Authentication**: Firebase Auth fully implemented
- **Database**: Firebase (no additional database needed for auth)

## How to Run the Project

### Backend (Spring Boot)
```bash
cd backend
./mvnw spring-boot:run
```
- Runs on: http://localhost:8080
- Test endpoint: http://localhost:8080/helloworld
- Auth endpoint: http://localhost:8080/api/auth/test

### Frontend (React)
```bash
cd frontend
npm start
```
- Runs on: http://localhost:3001
- Main app: http://localhost:3001

## Firebase Configuration
- **Project ID**: topfounders-66244
- **Auth Domain**: topfounders-66244.firebaseapp.com
- **Config File**: frontend/src/firebase/firebaseConfig.js

## Key Files Created/Modified

### Backend
- `src/main/java/com/TopFounders/BackendApplication.java` - Main app with @RestController
- `src/main/java/com/TopFounders/config/SecurityConfig.java` - Security configuration
- `src/main/java/com/TopFounders/config/FirebaseConfig.java` - Firebase Admin SDK setup
- `src/main/java/com/TopFounders/web/controller/AuthController.java` - Auth endpoints

### Frontend
- `src/App.js` - Main app with routing and auth state
- `src/components/Login.js` - Login component
- `src/components/Register.js` - Registration component
- `src/services/authService.js` - Authentication service
- `src/firebase/firebaseConfig.js` - Firebase client config

## Dependencies Added
### Backend (pom.xml)
- firebase-admin: 9.6.0

### Frontend (package.json)
- firebase: ^12.3.0
- react-router-dom: ^7.9.3
- react-scripts: 5.0.1

## Authentication Flow
1. User registers/logs in via Firebase Auth
2. Frontend receives Firebase ID token
3. Backend can verify tokens via Firebase Admin SDK
4. User state managed in React with authService

## Next Steps for Development
- Add user profile management
- Implement protected routes
- Add form validation
- Improve UI/UX
- Add password reset functionality
- Add user data storage

## Troubleshooting
- If react-scripts error: `npm install react-scripts@5.0.1`
- If Firebase errors: Check Firebase Console → Authentication → Sign-in method
- If backend won't start: Check port 8080 is free
- If frontend won't start: Check port 3001 is free

## Firebase Console Setup Required
1. Go to Firebase Console
2. Select project: topfounders-66244
3. Enable Email/Password authentication
4. Configure any additional auth providers as needed
