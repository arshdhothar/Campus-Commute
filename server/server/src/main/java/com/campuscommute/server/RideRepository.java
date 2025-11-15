package com.campuscommute.server;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RideRepository extends JpaRepository<Ride, Long> {
    List<Ride> findByDriverId(Long driverId);
    List<Ride> findByPassengerIdAndStatus(Long passengerId, String status);
    List<Ride> findByDriverIdAndStatus(Long driverId, String status);
    List<Ride> findByStatus(String status);
}
