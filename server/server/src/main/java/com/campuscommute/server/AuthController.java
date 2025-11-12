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

}