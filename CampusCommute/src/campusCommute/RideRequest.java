package campusCommute;

public class RideRequest {
	private String passengerName;
	private boolean approved;
	
	public RideRequest(String passengerName) {
		this.passengerName = passengerName;
		this.approved = false;
	}

	/**
	 * @return the passengerName
	 */
	public String getPassengerName() {
		return passengerName;
	}

	/**
	 * @param passengerName the passengerName to set
	 */
	public void setPassengerName(String passengerName) {
		this.passengerName = passengerName;
	}

	/**
	 * @return the approved
	 */
	public boolean isApproved() {
		return approved;
	}

	/**
	 * @param approved the approved to set
	 */
	public void setApproved(boolean approved) {
		this.approved = approved;
	}
	
	public void isDeclined() {
		approved = false;
	}
}
