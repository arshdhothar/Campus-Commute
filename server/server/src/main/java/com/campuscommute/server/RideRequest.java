package com.campuscommute.server;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
public class RideRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long passengerId;
    private String passengerName;

    private String status;

    @ManyToOne
    @JoinColumn(name = "ride_id")
    @JsonBackReference
    private Ride ride;

    public RideRequest() {}

    public RideRequest(Long passengerId, String passengerName) {
        this.passengerId = passengerId;
        this.passengerName = passengerName;
        this.status = "PENDING";
    }

    // Getters
    public Long getId() { return id; }
    public Long getPassengerId() { return passengerId; }
    public String getPassengerName() { return passengerName; }
    public String getStatus() { return status; }
    public Ride getRide() { return ride; }

    // Setters
    public void setPassengerId(Long passengerId) { this.passengerId = passengerId; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }
    public void setStatus(String status) { this.status = status; }
    public void setRide(Ride ride) { this.ride = ride; }
}
