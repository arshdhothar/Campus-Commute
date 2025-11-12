package com.campuscommute.server;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
public class RideRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	private Long id; 

	private String passengerName;
	private boolean approved;


	@ManyToOne
	@JoinColumn(name = "ride_id")
	private Ride ride;

	public RideRequest() {
	}

	public RideRequest(String passengerName) {
		this.passengerName = passengerName;
		this.approved = false;
	}

	public Long getID() {
		return id;
	}

	public Ride getRide() {
		return ride;
	}

	public void setRide(Ride ride) {
		this.ride = ride;
	}
}