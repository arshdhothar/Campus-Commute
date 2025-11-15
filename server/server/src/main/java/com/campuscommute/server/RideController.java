package com.campuscommute.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class RideController {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private RideRequestRepository rideRequestRepository;

    // ---------------------------------------------------------
    // 1. DRIVER CREATES RIDE (NOW REQUIRES driverId)
    // ---------------------------------------------------------
    @PostMapping("/rides/create")
    public Ride createRide(@RequestBody Ride ride) {

        // driverId already comes inside the ride object
        ride.setStatus("PENDING");
        ride.setDate(java.time.LocalDate.now().toString());

        return rideRepository.save(ride);
    }

    // ---------------------------------------------------------
    // 2. LIST ALL RIDES
    // ---------------------------------------------------------
    @GetMapping("/rides")
    public List<Ride> getAllRides() {
        return rideRepository.findAll();
    }

    // ---------------------------------------------------------
    // 3. GET RIDES FOR SPECIFIC DRIVER (DriverGUI uses this)
    // ---------------------------------------------------------
    @GetMapping("/rides/driver/{driverId}")
    public List<Ride> getDriverRides(@PathVariable Long driverId) {
        return rideRepository.findByDriverId(driverId);
    }

    // ---------------------------------------------------------
    // 4. PASSENGER SENDS RIDE REQUEST
    // ---------------------------------------------------------
    @PostMapping("/rides/{rideId}/request")
    public RideRequest addRequest(
            @PathVariable Long rideId,
            @RequestBody RideRequest request) {

        Ride ride = rideRepository.findById(rideId).orElse(null);
        if (ride == null) return null;

        request.setRide(ride);
        request.setStatus("PENDING");

        return rideRequestRepository.save(request);
    }

    // ---------------------------------------------------------
    // 5. VIEW REQUESTS FOR RIDE
    // ---------------------------------------------------------
    @GetMapping("/rides/{rideId}/requests")
    public List<RideRequest> getRideRequests(@PathVariable Long rideId) {
        return rideRequestRepository.findByRideId(rideId);
    }


    // ---------------------------------------------------------
    // 6. DRIVER ACCEPTS REQUEST
    // ---------------------------------------------------------
    @PostMapping("/ride/{rideId}/accept")
    public Ride acceptRide(
            @PathVariable Long rideId,
            @RequestParam Long driverId,
            @RequestParam Long passengerId,
            @RequestParam Long requestId) {

        Ride ride = rideRepository.findById(rideId).orElse(null);
        if (ride == null) return null;

        RideRequest req = rideRequestRepository.findById(requestId).orElse(null);
        if (req == null) return null;

        req.setStatus("APPROVED");
        rideRequestRepository.save(req);

        ride.acceptRide(driverId, passengerId);
        return rideRepository.save(ride);
    }

    // ---------------------------------------------------------
    // 7. PASSENGER HISTORY
    // ---------------------------------------------------------
    @GetMapping("/history/passenger/{id}")
    public List<Ride> getPassengerHistory(@PathVariable Long id) {
        return rideRepository.findByPassengerIdAndStatus(id, "COMPLETED");
    }

    // ---------------------------------------------------------
    // 8. DRIVER HISTORY
    // ---------------------------------------------------------
    @GetMapping("/history/driver/{id}")
    public List<Ride> getDriverHistory(@PathVariable Long id) {
        return rideRepository.findByDriverIdAndStatus(id, "COMPLETED");
    }
}
