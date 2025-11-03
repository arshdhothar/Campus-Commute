package campusCommute;

import java.util.ArrayList;
import java.util.List;

public class Ride {
	private String startLocation;
	private String destination;
	private String departureTime;
	private int availableSeats;
	private double price;
	
    // List to store all ride requests associated with this ride
	private List<RideRequest> requests;
	
	//Constructor  to create an empty ride
	public Ride()  {
		this.requests = new ArrayList<>();
	}
	
	//Constructor  to create a ride with full details
	public Ride (String startLocation, String destination, String departureTime, int availableSeats, double price) {
		this.startLocation = startLocation;
		this.destination = destination;
		this.departureTime = departureTime;
		this.availableSeats = availableSeats;
		this.price = price;
		this.requests = new ArrayList<>();
		
	}

    /**
     * Gets the starting location of the ride.
     *
     * @return the start location as a String
     */
    public String getStartLocation() {
        return startLocation;
    }

    /**
     * Sets the starting location of the ride.
     *
     * @param startLocation the location where the ride begins
     */
    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    /**
     * Gets the destination of the ride.
     *
     * @return the destination as a String
     */
    public String getDestination() {
        return destination;
    }

    /**
     * Sets the destination of the ride.
     *
     * @param destination the end location of the ride
     */
    public void setDestination(String destination) {
        this.destination = destination;
    }

    /**
     * Gets the departure time for the ride.
     *
     * @return the departure time as a String
     */
    public String getDepartureTime() {
        return departureTime;
    }

    /**
     * Sets the departure time for the ride.
     *
     * @param departureTime the time the ride is scheduled to leave
     */
    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    /**
     * Gets the number of available seats for this ride.
     *
     * @return the number of available seats
     */
    public int getAvailableSeats() {
        return availableSeats;
    }

    /**
     * Sets the number of available seats for this ride.
     *
     * @param availableSeats the total seats currently available
     */
    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    /**
     * Gets the price per seat for this ride.
     *
     * @return the price of the ride
     */
    public double getPrice() {
        return price;
    }

    /**
     * Sets the price per seat for this ride.
     *
     * @param price the cost of one seat on the ride
     */
    public void setPrice(double price) {
        this.price = price;
    }
	
    // Adds a new ride request if there are seats available
	public void addRequest(RideRequest request) {
		if (availableSeats > 0) {
			requests.add(request);
			System.out.println("Request added from " + request.getPassengerName());
		} else { 
			System.out.println("No seats available for " + request.getPassengerName());
		}
	}
	
    // Approves a ride request if it exists and there are still seats left
	public void approveRequest(RideRequest request) {
		if (requests.contains(request) && availableSeats > 0) {
			request.isApproved();
			availableSeats--;
			System.out.println("Request approved for " + request.getPassengerName());
		} else {
			System.out.println("Cannot approve request for " + request.getPassengerName());
		}
	}
	
    // Declines a ride request if it exists in the list
	public void declineRequest(RideRequest request) {
		if (requests.contains(request)) {
			request.isDeclined();
			System.out.println("Request declined for " + request.getPassengerName());
		}
	}
	
    // Returns the list of all ride requests
	public List<RideRequest> getRequests() {
		return requests;
	}
	
    // Returns a readable summary of the ride details
	@Override
	public String toString() {
		return startLocation + " -> " + destination + " @ " + departureTime +
				" | Seats: " + availableSeats + " | $" + price;
	}
}
