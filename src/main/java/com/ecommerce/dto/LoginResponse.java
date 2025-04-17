package com.ecommerce.dto;

public class LoginResponse {
    private String username;
    private boolean isAdmin;

    public LoginResponse(String username, boolean isAdmin) {
        this.username = username;
        this.isAdmin = isAdmin;
    }

    // Getters y setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
} 