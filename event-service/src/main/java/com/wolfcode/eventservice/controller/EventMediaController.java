package com.wolfcode.eventservice.controller;

import com.wolfcode.eventservice.dto.EventMediaRequest;
import com.wolfcode.eventservice.entity.EventMedia;
import com.wolfcode.eventservice.service.EventMediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/events")
public class EventMediaController {

    private final EventMediaService eventMediaService;

    @PostMapping("{eventCode}/event-files")
    public ResponseEntity<String> addEventMediaFiles(@PathVariable String eventCode,
                                                     @RequestParam("file") MultipartFile file,
                                                     @RequestParam("name") String name,
                                                     @RequestParam("description") String description) {
        try {
            EventMediaRequest eventMediaRequest = new EventMediaRequest(name, description, file);
            eventMediaService.addEventMediaFiles(eventMediaRequest, eventCode);
            return ResponseEntity.ok("File uploaded successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file.");
        }
    }

    @PutMapping("/event-files/{id}")
    public ResponseEntity<EventMedia> updateEventMedia(@PathVariable("id") Long id,
                                                       @RequestBody EventMediaRequest eventMediaRequest) {
        return ResponseEntity.ok(eventMediaService.eventMediaRequest(id, eventMediaRequest));
    }

    @GetMapping("/event-files/{id}")
    public ResponseEntity<?> getEventMediaById(@PathVariable("id") Long id) {
        return eventMediaService.getEventMediaById(id);
    }

    @DeleteMapping("/event-files/{id}")
    public void deleteEventMedia(@PathVariable("id") Long id) {
        eventMediaService.deleteEventMedia(id);
    }

    @GetMapping("/event-files/{id}/download")
    public ResponseEntity<Resource> downloadEventMediaById(@PathVariable("id") Long id) {
        return eventMediaService.downloadEventMediaById(id);
    }
}
