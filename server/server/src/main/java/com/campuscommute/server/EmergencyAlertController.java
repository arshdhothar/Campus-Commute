package com.campuscommute.server;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class EmergencyAlertController {
     private final EmergencyAlertRepository repo;

    public EmergencyAlertController(EmergencyAlertRepository repo) {
        this.repo = repo;
    }

    @PostMapping("/emergency-alert")
    public ResponseEntity<Void> createAlert(@RequestBody EmergencyAlertDto dto) {

        EmergencyAlert alert = new EmergencyAlert();
        alert.setUserName(dto.userName);
        alert.setUserPhone(dto.userPhone);
        alert.setRideId(dto.rideId);
        alert.setLat(dto.lat);
        alert.setLng(dto.lng);

        repo.save(alert);

        return ResponseEntity.ok().build();
    }
}
