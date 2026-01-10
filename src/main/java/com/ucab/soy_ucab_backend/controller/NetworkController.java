package com.ucab.soy_ucab_backend.controller;

import com.ucab.soy_ucab_backend.service.NetworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/network")
@CrossOrigin(origins = "http://localhost:4200") // Allow Angular frontend
public class NetworkController {

    @Autowired
    private NetworkService networkService;

    @GetMapping("/data")
    public ResponseEntity<Map<String, Object>> getNetworkData(@RequestParam String email) {
        try {
            Map<String, Object> data = networkService.getNetworkData(email);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/manage-request")
    public ResponseEntity<?> manageRequest(@RequestBody Map<String, Object> payload) {
        System.out.println("Received manage-request payload: " + payload);
        String userEmail = (String) payload.get("userEmail");
        String targetEmail = (String) payload.get("targetEmail");
        String action = (String) payload.get("action");
        boolean force = payload.containsKey("force") ? (boolean) payload.get("force") : false;

        try {
            String status = networkService.manageFriendRequest(userEmail, targetEmail, action, force);
            return ResponseEntity.ok(Collections.singletonMap("status", status));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/manage-follow")
    public ResponseEntity<Void> manageFollow(@RequestBody Map<String, String> payload) {
        try {
            System.out.println("Received manage-follow payload: " + payload);
            String followerEmail = payload.get("followerEmail");
            String followedEmail = payload.get("followedEmail");
            String action = payload.get("action");
            networkService.manageFollow(followerEmail, followedEmail, action);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
