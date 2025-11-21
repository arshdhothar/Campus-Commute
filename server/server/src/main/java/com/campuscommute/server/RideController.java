package com.campuscommute.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class RideController {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private RideRequestRepository rideRequestRepository;
    
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
        List<Ride> rides = rideRepository.findAll();
        for (Ride r : rides) {
            if (r.getDriverId() != null) {
                personRepository.findById(r.getDriverId()).ifPresent(driver -> {
                    r.setDriverRating(driver.getAverageRating());
                });
            }
        }
        return rides;
    }

    // ---------------------------------------------------------
    // 3. GET RIDES FOR SPECIFIC DRIVER
    // ---------------------------------------------------------
    @GetMapping("/rides/driver/{driverId}")
    public List<Ride> getDriverRides(@PathVariable Long driverId) {
        List<Ride> rides = rideRepository.findByDriverId(driverId);
        personRepository.findById(driverId).ifPresent(driver -> {
            double rating = driver.getAverageRating();
            for (Ride r : rides) {
                r.setDriverRating(rating);
            }
        });
        return rides;
    }

    // ---------------------------------------------------------
    // 4. SEND RIDE REQUEST
    // ---------------------------------------------------------
    @PostMapping("/rides/{rideId}/request")
    public ResponseEntity<String> sendRequest(@PathVariable Long rideId, @RequestBody RideRequest request) {
        Optional<Ride> rideOpt = rideRepository.findById(rideId);
        if (rideOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ride not found");
        }

        Ride ride = rideOpt.get();
        request.setRide(ride);
        request.setStatus("PENDING");
        
        rideRequestRepository.save(request);
        return ResponseEntity.ok("Request sent");
    }

    // ---------------------------------------------------------
    // 5. HANDLE REQUEST (Approve/Deny)
    // ---------------------------------------------------------
    @PostMapping("/requests/{reqId}/status")
    public ResponseEntity<String> handleRequestStatus(@PathVariable Long reqId, @RequestBody String status) {
        String cleanStatus = status.replace("\"", "").trim();

        Optional<RideRequest> reqOpt = rideRequestRepository.findById(reqId);
        if (reqOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Request not found");
        }

        RideRequest req = reqOpt.get();
        req.setStatus(cleanStatus);
        rideRequestRepository.save(req);

        if ("APPROVED".equalsIgnoreCase(cleanStatus)) {
            Ride ride = req.getRide();
            if (ride.getAvailableSeats() > 0) {
                ride.setAvailableSeats(ride.getAvailableSeats() - 1);
                rideRepository.save(ride);
            }
            
            // Notification placeholder
            Person passenger = personRepository.findById(req.getPassengerId()).orElse(null);
            if (passenger != null && passenger.isNotifyOnConfirmation()) {
                System.out.println("NOTIFICATION: Sending confirmation to " + passenger.getEmail());
            }
        }

        return ResponseEntity.ok("Status updated to " + cleanStatus);
    }

    // ---------------------------------------------------------
    // 6. PASSENGER HISTORY (UPDATED)
    // ---------------------------------------------------------
    @GetMapping("/history/passenger/{id}")
    public List<Ride> getPassengerHistory(@PathVariable Long id) {
        // Look up the requests the passenger has made
        List<RideRequest> requests = rideRequestRepository.findByPassengerId(id);
        List<Ride> history = new ArrayList<>();

        for (RideRequest req : requests) {
            // If the request is APPROVED, add it to history so they can rate
            if ("APPROVED".equalsIgnoreCase(req.getStatus()) || "COMPLETED".equalsIgnoreCase(req.getStatus())) {
                Ride ride = req.getRide();
                
                // Set status to match request so frontend enables button
                ride.setStatus(req.getStatus()); 

                // Add driver rating info
                if (ride.getDriverId() != null) {
                    personRepository.findById(ride.getDriverId()).ifPresent(driver -> {
                        ride.setDriverRating(driver.getAverageRating());
                    });
                }
                history.add(ride);
            }
        }
        return history;
    }

    // ---------------------------------------------------------
    // 7. DRIVER HISTORY
    // ---------------------------------------------------------
    @GetMapping("/history/driver/{id}")
    public List<Ride> getDriverHistory(@PathVariable Long id) {
        return rideRepository.findByDriverIdAndStatus(id, "COMPLETED");
    }
}