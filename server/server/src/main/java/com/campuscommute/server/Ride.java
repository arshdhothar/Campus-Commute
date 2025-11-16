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

    @OneToMany(mappedBy = "ride", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<RideRequest> requests = new ArrayList<>();

    public Ride() {}

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

    public void setPassengerId(Long passengerId) { this.passengerId = passengerId; }
    public void setDriverId(Long driverId) { this.driverId = driverId; }
    public void setStatus(String status) { this.status = status; }
    public void setDate(String date) { this.date = date; }
    public void setStartLocation(String startLocation) { this.startLocation = startLocation; }
    public void setDestination(String destination) { this.destination = destination; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }
    public void setPrice(double price) { this.price = price; }

    public void acceptRide(Long driverId, Long passengerId) {
        this.driverId = driverId;
        this.passengerId = passengerId;
        this.status = "COMPLETED";
        this.date = java.time.LocalDate.now().toString();
    }

    public void addRequest(RideRequest request) {
        requests.add(request);
        request.setRide(this);
    }
}
