package com.TopFounders.ui.controller;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initialize() {
        try {
            ClassPathResource resource = new ClassPathResource("firebase.json");
            System.out.println("🔍 Resource exists? " + resource.exists());
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(resource.getInputStream()))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) { // ✅ Prevent duplicate init and timing issue
                FirebaseApp.initializeApp(options);
                System.out.println("✅ Firebase has been initialized successfully!");
            } else {
                System.out.println("⚠️ Firebase already initialized, skipping...");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Bean
    public FirebaseAuth firebaseAuth() {
        return FirebaseAuth.getInstance();
    }
}