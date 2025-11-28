package com.campuscommute.server;

public class PaymentStatusRequest {

    private Long rideId;
    private Long passengerId;

    public Long getRideId() {
        return rideId;
    }

    public Long getPassengerId() {
        return passengerId;
    }
}
