package com.campuscommute.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RatingController {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private PersonRepository personRepository;

    // Endpoint to submit a driver rating
    @PostMapping("/rate/driver")
    public ResponseEntity<Person> rateDriver(@RequestBody Rating rating) {
        
        // 1. Extract the ID to a local variable
        Long incomingId = rating.getRatedPersonId();

        // 2. Check if null immediately
        if (incomingId == null || rating.getRating() < 1 || rating.getRating() > 5) {
            return ResponseEntity.badRequest().build();
        }
        
        // 3. Safely unbox to primitive 'long' (this guarantees it's not null for the DB call)
        long driverId = incomingId;

        // Save the individual rating record
        ratingRepository.save(rating);

        // Fetch the driver to update their statistics
        // We use 'driverId' (primitive) so the compiler knows it's safe
        Person driver = personRepository.findById(driverId).orElse(null);
        
        if (driver != null) {
            // Calculate new average
            double currentTotalScore = driver.getAverageRating() * driver.getRatingCount();
            int newCount = driver.getRatingCount() + 1;
            double newAverage = (currentTotalScore + rating.getRating()) / newCount;

            // Update and save driver
            driver.setRatingCount(newCount);
            driver.setAverageRating(newAverage);
            personRepository.save(driver);
            
            return ResponseEntity.ok(driver);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}