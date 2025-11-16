package campusCommute;

public class RideRequest {

    private Long id;
    private Long passengerId;
    private String passengerName;
    private String status;

    // This is all frontend needs
    private Long rideId;

    public RideRequest() {}

    public RideRequest(Long passengerId, String passengerName) {
        this.passengerId = passengerId;
        this.passengerName = passengerName;
        this.status = "PENDING";
    }

    // -------------------------
    // GETTERS
    // -------------------------
    public Long getId() {
        return id;
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public String getStatus() {
        return status;
    }

    public Long getRideId() {
        return rideId;
    }

    // -------------------------
    // SETTERS
    // -------------------------
    public void setId(Long id) {
        this.id = id;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }
}
