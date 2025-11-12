package com.campuscommute.server;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {

    // This will let you search for rides
    List<Ride> findByStartLocationContaining(String query);
    List<Ride> findByDestinationContaining(String query);

}