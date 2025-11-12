package com.campuscommute.server;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;

import java.util.ArrayList;
import java.util.List;

@Entity 
public class Ride {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long ID;

    private String startLocation;
    private String destination;
    private String departureTime;
    private int availableSeats;
    private double price;

    // This defines the "one ride has many requests" relationship
    @OneToMany(mappedBy = "ride", cascade = CascadeType.ALL)
    private List<RideRequest> requests;

    public Ride() {
        this.requests = new ArrayList<>();
    }

    public Ride(String startLocation, String destination, String departureTime, int availableSeats, double price) {
        this.startLocation = startLocation;
        this.destination = destination;
        this.departureTime = departureTime;
        this.availableSeats = availableSeats;
        this.price = price;
        this.requests = new ArrayList<>();
    }

    public Long getID() {
        return ID;
    }

}