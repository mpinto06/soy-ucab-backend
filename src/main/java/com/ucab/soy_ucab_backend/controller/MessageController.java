package com.ucab.soy_ucab_backend.controller;

import com.ucab.soy_ucab_backend.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/messages")
@CrossOrigin(origins = "http://localhost:4200")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/conversations")
    public ResponseEntity<?> getConversations(@RequestParam String email) {
        try {
            List<Map<String, Object>> convs = messageService.getConversations(email);
            return ResponseEntity.ok(convs);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error fetching conversations");
        }
    }

    @GetMapping("/history")
    public ResponseEntity<?> getChatHistory(@RequestParam String userEmail, @RequestParam String otherEmail) {
        try {
            List<Map<String, Object>> history = messageService.getChatHistory(userEmail, otherEmail);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error fetching history");
        }
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(
            @RequestParam("sender") String sender,
            @RequestParam("receiver") String receiver,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "files", required = false) List<MultipartFile> files) {

        try {
            messageService.sendMessage(sender, receiver, content, files);
            return ResponseEntity.ok(Collections.singletonMap("status", "SUCCESS"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error sending message");
        }
    }

    @GetMapping("/file")
    public ResponseEntity<?> getFile(@RequestParam String id, @RequestParam String filename) {
        try {
            Map<String, Object> fileData = messageService.getFile(id, filename);
            if (fileData != null) {
                byte[] content = (byte[]) fileData.get("archivo");
                String format = (String) fileData.get("formato");

                String mimeType = "application/octet-stream";
                // Simple mime type mapping
                if ("jpg".equalsIgnoreCase(format) || "jpeg".equalsIgnoreCase(format))
                    mimeType = "image/jpeg";
                else if ("png".equalsIgnoreCase(format))
                    mimeType = "image/png";
                else if ("pdf".equalsIgnoreCase(format))
                    mimeType = "application/pdf";
                else if ("mp4".equalsIgnoreCase(format))
                    mimeType = "video/mp4";

                return ResponseEntity.ok()
                        .header(org.springframework.http.HttpHeaders.CONTENT_TYPE, mimeType)
                        .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                                "inline; filename=\"" + filename + "\"")
                        .body(content);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
