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
    
    // This is required for the new notification logic
    @Autowired
    private PersonRepository personRepository;

    // ---------------------------------------------------------
    // 1. DRIVER CREATES RIDE
    // ---------------------------------------------------------
    @PostMapping("/rides/create")
    public Ride createRide(@RequestBody Ride ride) {
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
    // 3. GET RIDES FOR SPECIFIC DRIVER
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
    // 6. DRIVER ACCEPTS REQUEST (MODIFIED)
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

        // Update the request status
        req.setStatus("APPROVED");
        rideRequestRepository.save(req);

        // Update the ride status
        ride.acceptRide(driverId, passengerId);
        Ride savedRide = rideRepository.save(ride);

        // --- NEW LOGIC FOR USER STORY 15 (AC #3) ---
        try {
            // Find the passenger who needs to be notified
            Person passenger = personRepository.findById(passengerId).orElse(null);

            // Check their notification preference
            if (passenger != null && passenger.isNotifyOnConfirmation()) {
                // This is where you would put your email/SMS sending logic
                System.out.println("NOTIFICATION: Sending 'Ride Confirmation' alert to: " + passenger.getEmail());
            } else if (passenger != null) {
                System.out.println("NOTIFICATION: User " + passenger.getEmail() + " has disabled confirmation alerts.");
            }
        } catch (Exception e) {
            // Don't let a notification failure stop the API call
            System.err.println("Error sending notification: " + e.getMessage());
        }
        // --- END OF NEW LOGIC ---

        return savedRide; // Return the ride to the driver
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