package com.campuscommute.server;


import org.springframework.data.jpa.repository.JpaRepository;

public interface EmergencyAlertRepository extends JpaRepository<EmergencyAlert, Long> { }