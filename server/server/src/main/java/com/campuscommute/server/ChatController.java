package com.campuscommute.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private MessageRepository messageRepository;

    // 1. Send a message
    @PostMapping("/send")
    public Message sendMessage(@RequestBody Message message) {
        message.setTimestamp(java.time.LocalDateTime.now().toString());
        return messageRepository.save(message);
    }

    // 2. Get chat history for a ride
    @GetMapping("/{rideId}")
    public List<Message> getMessages(@PathVariable Long rideId) {
        return messageRepository.findByRideIdOrderByIdAsc(rideId);
    }
}