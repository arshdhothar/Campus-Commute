package campusCommute;

public class Ride {
		private String startLocation;
		private String destination;
		private String departureTime;
		private int availableSeats;
		private double price;
		
		
		public Ride()  {}
		
		public Ride (String startLocation, String destination, String departureTime, int availableSeats, double price) {
			this.startLocation = startLocation;
			this.destination = destination;
			this.departureTime = departureTime;
			this.availableSeats = availableSeats;
			this.price = price;
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
		
		@Override
		public String toString() {
			return startLocation + "->" + destination + "@" + departureTime +
					" | Seats: " + availableSeats + " | $" + price;
		}

}
