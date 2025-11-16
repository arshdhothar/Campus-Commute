package com.campuscommute.server;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RideRequestRepository extends JpaRepository<RideRequest, Long> {

    List<RideRequest> findByRideId(Long rideId);

    List<RideRequest> findByPassengerId(Long passengerId);
}