package com.campuscommute.server;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    private PersonRepository personRepository;

    @PostMapping("/api/signup")
    public void signUp(@RequestBody Person person) {
        personRepository.save(person);
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

    /**
     * Handles updating a user's profile information.
     * Finds the user by ID and updates their bio, photo path, and notifications.
     */
    @PostMapping("/api/profile/update")
    public ResponseEntity<Person> updateProfile(@RequestBody Person updatedPerson) {

        Person existingPerson = personRepository.findById(updatedPerson.getID()).orElse(null);

        if (existingPerson != null) {

            // Update the fields on the *existing* person object
            existingPerson.setBio(updatedPerson.getBio());
            existingPerson.setPhotoPath(updatedPerson.getPhotoPath());

            // --- NEW LOGIC FOR US15 ---
            existingPerson.setNotifyOnConfirmation(updatedPerson.isNotifyOnConfirmation());
            existingPerson.setNotifyOnCancellation(updatedPerson.isNotifyOnCancellation());
            existingPerson.setNotifyOnReminder(updatedPerson.isNotifyOnReminder());
            // --- END OF NEW LOGIC ---

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

    @PostMapping("/api/updateEmergencyContact")
    public ResponseEntity<String> updateEmergencyContact(@RequestBody EmergencyContactRequest request) {

        // 1. Find the user by their email
        Person person = personRepository.findByEmail(request.getEmail());

        // 2. Check if they exist
        if (person == null) {
            System.out.println("FAILURE: User not found: " + request.getEmail());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        // 3. Update the person object with the new info
        person.setEmergencyContactName(request.getContactName());
        person.setEmergencyContactPhone(request.getContactPhone());

        // 4. Save the updated object back to the database
        personRepository.save(person);

        // 5. Send a "200 OK" success message back to the GUI
        System.out.println("SUCCESS: Updated emergency contact for: " + person.getEmail());
        return ResponseEntity.ok("Contact info updated successfully!");
    }
}