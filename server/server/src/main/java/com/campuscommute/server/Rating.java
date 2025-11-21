package com.campuscommute.server;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.Instant;

@Entity
public class Rating {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The ID of the Person (Driver) who received the rating
    private Long ratedPersonId; 
    
    // The rating value (e.g., 1.0 to 5.0)
    private double rating;
    
    // The ID of the person (Passenger) who gave the rating
    private Long raterPersonId; 
    
    private Instant createdAt = Instant.now();

    public Rating() {}

    public Long getId() { 
        return id; 
    }
    
    public void setId(Long id) { 
        this.id = id; 
    }

    public Long getRatedPersonId() { 
        return ratedPersonId; 
    }
    
    public void setRatedPersonId(Long ratedPersonId) { 
        this.ratedPersonId = ratedPersonId; 
    }

    public double getRating() { 
        return rating; 
    }
    
    public void setRating(double rating) { 
        this.rating = rating; 
    }

    public Long getRaterPersonId() { 
        return raterPersonId; 
    }
    
    public void setRaterPersonId(Long raterPersonId) { 
        this.raterPersonId = raterPersonId; 
    }

    public Instant getCreatedAt() { 
        return createdAt; 
    }
    
    public void setCreatedAt(Instant createdAt) { 
        this.createdAt = createdAt; 
    }
}