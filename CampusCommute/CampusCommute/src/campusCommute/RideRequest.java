package campusCommute;

public class RideRequest {
	private String passengerName;
	private boolean approved;
	
	public RideRequest(String passengerName) {
		this.passengerName = passengerName;
		this.approved = false;
	}
	
	public String getPassengerName() {
		return passengerName;
	}
	
	public boolean isApproved() {
		return approved;
	}
	
	public void approve() {
		approved = true;
	}
	
	public void decline() {
		approved = false;
	}
}
