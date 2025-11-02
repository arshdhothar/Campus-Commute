package campusCommute;

import java.util.ArrayList;
import java.util.List;

public class Ride {
		private String startLocation;
		private String destination;
		private String departureTime;
		private int availableSeats;
		private double price;
		
		private List<RideRequest> requests;
		
		public Ride()  {
			this.requests = new ArrayList<>();
		}
		
		public Ride (String startLocation, String destination, String departureTime, int availableSeats, double price) {
			this.startLocation = startLocation;
			this.destination = destination;
			this.departureTime = departureTime;
			this.availableSeats = availableSeats;
			this.price = price;
			this.requests = new ArrayList<>();
			
		}

		
		public String getStartLocation() {
			return startLocation;
		}
		
		public void setStartLocation(String startLocation) {
			this.startLocation = startLocation;
		}
		
		public String getDestination () {
			return destination;
		}
		
		public void setDestination(String destination) {
			this.destination = destination;
		}
		
		public String getDepartureTime() {
			return departureTime;
		}
		
		public void setDepartureTime(String departureTime) {
			this.departureTime = departureTime;
		}
		
		
		public int getAvailableSeats() {
			return availableSeats;
		}

		public void setAvailableSeats(int availableSeats) {
			this.availableSeats = availableSeats;
		}
		
		public double getPrice() {
			return price;
		}

		public void setPrice(double price) {
			this.price = price;
		}
		
		public void addRequest(RideRequest request) {
			if (availableSeats > 0) {
				requests.add(request);
				System.out.println("Request added from " + request.getPassengerName());
			} else { 
				System.out.println("No seats available for " + request.getPassengerName());
			}
		}
		
		public void approveRequest(RideRequest request) {
			if (requests.contains(request) && availableSeats > 0) {
				request.approve();
				availableSeats--;
				System.out.println("Request approved for " + request.getPassengerName());
			} else {
				System.out.println("Cannot approve request for " + request.getPassengerName());
			}
		}
		
		public void declineRequest(RideRequest request) {
			if (requests.contains(request)) {
				request.decline();
				System.out.println("Request declined for " + request.getPassengerName());
			}
		}
		
		public List<RideRequest> getRequests() {
			return requests;
		}
		
		@Override
		public String toString() {
			return startLocation + "->" + destination + "@" + departureTime +
					" | Seats: " + availableSeats + " | $" + price;
		}

}
