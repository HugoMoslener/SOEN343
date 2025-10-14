package com.TopFounders.domain.model;

public class User {
    private String username;
    private String email;
    private String fullName;
    private String address;
    private String role;

        public User() {
        }

        public User(String username, String email, String fullName, String address, String role) {
            this.username = username;
            this.email = email;
            this.fullName = fullName;
            this.address = address;
            this.role = role;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
           this.fullName = fullName;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
           this.address = address;
       }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
        public String getRole(){
         return role;
        }
        public void setRole(String role){
         this.role = role;
        }
}

