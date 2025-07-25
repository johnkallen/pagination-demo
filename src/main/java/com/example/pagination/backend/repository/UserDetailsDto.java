package com.example.pagination.backend.repository;

import java.sql.Timestamp;

public class UserDetailsDto {
    private Long userId;
    private String username;
    private Timestamp createdAt;
    private String phoneNumber;
    private String street;
    private String city;
    private String state;
    private String zipCode;

    // No-arg constructor
    public UserDetailsDto() {}

    // All-args constructor (optional)
    public UserDetailsDto(Long userId, String username, Timestamp createdAt, String phoneNumber,
                          String street, String city, String state, String zipCode) {
        this.userId = userId;
        this.username = username;
        this.createdAt = createdAt;
        this.phoneNumber = phoneNumber;
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }

    // Getters and setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }

}
