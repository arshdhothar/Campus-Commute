package campusCommute;

import java.util.ArrayList;
import java.util.List;

/**
 * Frontend Ride model — must match backend Ride entity exactly.
 */
public class Ride {

    // Backend JSON field names
    private Long id;
    private Long passengerId;
    private Long driverId;
    private String status;     // PENDING, APPROVED, COMPLETED
    private String date;

    private String startLocation;
    private String destination;
    private String departureTime;
    private int availableSeats;
    private double price;

    private List<RideRequest> requests;

    // Empty constructor for Gson
    public Ride() {
        this.requests = new ArrayList<>();
    }

    // Constructor used when posting a ride from frontend
    public Ride(String startLocation, String destination, String departureTime,
                int availableSeats, double price) {

        this.startLocation = startLocation;
        this.destination = destination;
        this.departureTime = departureTime;
        this.availableSeats = availableSeats;
        this.price = price;

        this.requests = new ArrayList<>();
    }

    // -------------------------
    // GETTERS
    // -------------------------

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

    // -------------------------
    // SETTERS
    // -------------------------

    public void setId(Long id) { this.id = id; }
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

    public void addRequest(RideRequest request) {
        if (this.requests == null) this.requests = new ArrayList<>();
        this.requests.add(request);
    }

    // -------------------------
    // User-friendly display text
    // -------------------------
    @Override
    public String toString() {
        return startLocation + " -> " + destination +
               " @ " + departureTime +
               " | Seats: " + availableSeats +
               " | $" + price;
    }
}
