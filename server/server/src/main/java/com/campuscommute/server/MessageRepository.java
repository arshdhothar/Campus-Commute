package com.campuscommute.server;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    // Find all messages for a specific ride, ordered by time
    List<Message> findByRideIdOrderByIdAsc(Long rideId);
}