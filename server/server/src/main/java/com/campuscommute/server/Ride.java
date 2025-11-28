package com.campuscommute.server;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long passengerId;
    private Long driverId;

    private String status;
    private String date;

    private String startLocation;
    private String destination;
    private String departureTime;
    private int availableSeats;
    private double price;

    // Transient field to send the driver's rating to frontend without saving to DB
    @Transient
    private double driverRating;

    // Transient fields for payment info (per passenger)
    @Transient
    private Double splitFare;

    @Transient
    private String paymentStatus;


    @OneToMany(mappedBy = "ride", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<RideRequest> requests = new ArrayList<>();

    public Ride() {}

    // Getters
    public Long getId() { return id; }
    public Long getPassengerId() { return passengerId; }
    public Long getDriverId() { return driverId; }
    public String getStatus() { return status; }
    public String getDate() { return date; }

    public String getStartLocation() { return startLocation; }
    public String getDestination() { return destination; }
    public String getDepartureTime() { return departureTime; }
    public int getAvailableSeats() { return availableSeats; }
    public double getPrice() { return price; }
    public List<RideRequest> getRequests() { return requests; }
    
    public double getDriverRating() { return driverRating; }

    public Double getSplitFare() { return splitFare; }
    public String getPaymentStatus() { return paymentStatus; }


    // Setters
    public void setPassengerId(Long passengerId) { this.passengerId = passengerId; }
    public void setDriverId(Long driverId) { this.driverId = driverId; }
    public void setStatus(String status) { this.status = status; }
    public void setDate(String date) { this.date = date; }
    public void setStartLocation(String startLocation) { this.startLocation = startLocation; }
    public void setDestination(String destination) { this.destination = destination; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }
    public void setPrice(double price) { this.price = price; }
    public void setRequests(List<RideRequest> requests) { this.requests = requests; }
    
    public void setDriverRating(double driverRating) { this.driverRating = driverRating; }

    public void setSplitFare(Double splitFare) { this.splitFare = splitFare; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

}