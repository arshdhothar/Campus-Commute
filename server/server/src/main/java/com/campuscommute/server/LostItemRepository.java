package com.campuscommute.server;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LostItemRepository extends JpaRepository<LostItemReport, Long> {

    // This allows you to find all reports made by a specific user
    List<LostItemReport> findByUserEmail(String userEmail);

    // This allows you to find all "Open" reports (useful for an admin view later)
    List<LostItemReport> findByStatus(String status);
}