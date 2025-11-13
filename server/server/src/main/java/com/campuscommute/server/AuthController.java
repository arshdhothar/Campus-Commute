package com.campuscommute.server;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController // <-- Tells Spring this class is an API controller
public class AuthController {

    // Spring will automatically give us the repository we made
    @Autowired
    private PersonRepository personRepository;

    // This method will listen for HTTP POST requests at
    // "http://localhost:8080/api/signup"
    @PostMapping("/api/signup")
    public void signUp(@RequestBody Person person) {
        // Spring takes the JSON from the request and turns it into a Person object.
        // Then, we just save it to the database.
        personRepository.save(person);

        // This log will prove it worked!
        System.out.println("SUCCESS: New user saved to database: " + person.getEmail());
    }

    @PostMapping("/api/login")
    public ResponseEntity<Person> login(@RequestBody LoginRequest loginRequest) {
        Person person = personRepository.findByEmail(loginRequest.getEmail());

        if (person != null && person.getPassword().equals(loginRequest.getPassword())) {
            System.out.println("SUCCESS: User logged in: " + person.getEmail());
            return ResponseEntity.ok(person);
        } else {
            System.out.println("FAILURE: Failing login attempt for: " + loginRequest.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // --- THIS IS THE NEW METHOD YOU ASKED FOR ---
    
    /**
     * Handles updating a user's profile information.
     * Finds the user by ID and updates their bio and photo path.
     */
    @PostMapping("/api/profile/update")
    public ResponseEntity<Person> updateProfile(@RequestBody Person updatedPerson) {
        
        // Find the *existing* person from the database using the ID from the request
        Person existingPerson = personRepository.findById(updatedPerson.getID()).orElse(null);

        // Check if the user actually exists
        if (existingPerson != null) {
            
            // Update the fields on the *existing* person object
            existingPerson.setBio(updatedPerson.getBio());
            existingPerson.setPhotoPath(updatedPerson.getPhotoPath());
            // In the future, you can add:
            // existingPerson.setEmergencyContactName(updatedPerson.getEmergencyContactName());
            // existingPerson.setEmergencyContactPhone(updatedPerson.getEmergencyContactPhone());

            // Save the updated object back to the database
            Person savedPerson = personRepository.save(existingPerson);
            
            System.out.println("SUCCESS: Profile updated for: " + savedPerson.getEmail());
            
            // Send the fully updated person back to the frontend
            return ResponseEntity.ok(savedPerson); 
        } else {
            // Couldn't find a user with the ID provided
            System.out.println("FAILURE: Could not find user to update with ID: " + updatedPerson.getID());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}