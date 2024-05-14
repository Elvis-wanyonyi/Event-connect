package com.wolfcode.eventservice.service;

import com.wolfcode.eventservice.dto.EventMediaRequest;
import com.wolfcode.eventservice.entity.EventMedia;
import com.wolfcode.eventservice.entity.Events;
import com.wolfcode.eventservice.repository.EventMediaRepository;
import com.wolfcode.eventservice.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
@RequiredArgsConstructor
public class EventMediaService {

    private static final long MAX_FILE_SIZE = 20 * 1024 * 1024; // 20MB
    private static final Set<String> SUPPORTED_CONTENT_TYPES = new HashSet<>(Arrays.asList(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "image/jpeg",
            "image/png",
            "image/gif",
            "video/mp4",
            "video/quicktime"
    ));
    private final EventMediaRepository eventMediaRepository;
    private final EventRepository eventRepository;
    @Value("${upload.location}")
    private String uploadLocation;

    public void addEventMediaFiles(EventMediaRequest eventMediaRequest, String eventCode) throws IOException {
        MultipartFile file = eventMediaRequest.getFile();

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds the maximum limit of 20MB");
        }
        if (!SUPPORTED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("Unsupported file type. Only documents, images, and short videos are allowed.");
        }

        String fileName = generateUniqueFileName(file);
        saveFileToDisk(file, fileName);
        Optional<Events> eventsOptional = eventRepository.findByEventCode(eventCode);
        if (eventsOptional.isPresent()) {
            Events event = eventsOptional.get();
            EventMedia content = EventMedia.builder()
                    .name(eventMediaRequest.getName())
                    .event(event)
                    .filePath(fileName)
                    .build();
            eventMediaRepository.save(content);
        }
    }

    private String generateUniqueFileName(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        assert originalFileName != null;
        String extension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
        return UUID.randomUUID() + "." + extension;
    }

    private void saveFileToDisk(MultipartFile file, String fileName) throws IOException {
        Path uploadPath = Paths.get(uploadLocation, fileName);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, uploadPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public EventMedia eventMediaRequest(Long id, EventMediaRequest eventMediaRequest) {
        Optional<EventMedia> eventMediaOptional = eventMediaRepository.findById(id);
        if (eventMediaOptional.isPresent()) {
            EventMedia existingEventMedia = eventMediaOptional.get();
            existingEventMedia.setName(eventMediaRequest.getName());

            eventMediaRepository.save(existingEventMedia);
            return existingEventMedia;
        }
        return null;
    }

    public ResponseEntity<Resource> downloadEventMediaById(Long id) {
        Optional<EventMedia> eventMediaOptional = eventMediaRepository.findById(id);
        if (eventMediaOptional.isPresent()) {
            EventMedia eventMedia = eventMediaOptional.get();
            Path filePath = Paths.get(uploadLocation, eventMedia.getFilePath());
            Resource resource;
            try {
                resource = new UrlResource(filePath.toUri());
            } catch (MalformedURLException e) {
                throw new RuntimeException("File not found");
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + eventMedia.getFilePath() + "\"")
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<?> getEventMediaById(Long id) {
        Optional<EventMedia> eventMediaOptional = eventMediaRepository.findById(id);
        if (eventMediaOptional.isPresent()) {
            EventMedia eventMedia = eventMediaOptional.get();
            Path filePath = Paths.get(uploadLocation, eventMedia.getFilePath());
            Resource resource;
            try {
                resource = new UrlResource(filePath.toUri());
            } catch (MalformedURLException e) {
                throw new RuntimeException("File not found");
            }

            if (isVideoOrImage(eventMedia.getFilePath())) {
                // For images and videos, return the resource to be displayed in the browser
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(getContentType(eventMedia.getFilePath())))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                        .body(resource);
            } else {
                // For documents, return the resource as a downloadable file
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + eventMedia.getFilePath() + "\"")
                        .body(resource);
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private boolean isVideoOrImage(String filePath) {
        String extension = getFileExtension(filePath);
        return extension != null && (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg")
                || extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("gif")
                || extension.equalsIgnoreCase("mp4") || extension.equalsIgnoreCase("mov"));
    }

    private String getFileExtension(String filePath) {
        int lastDotIndex = filePath.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return filePath.substring(lastDotIndex + 1).toLowerCase();
        }
        return null;
    }

    private String getContentType(String filePath) {
        String extension = getFileExtension(filePath);
        assert extension != null;
        return switch (extension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "mp4" -> "video/mp4";
            case "mov" -> "video/quicktime";
            default -> MediaType.APPLICATION_OCTET_STREAM_VALUE;
        };
    }

    public void deleteEventMedia(Long id) {
        eventMediaRepository.deleteById(id);
    }
}

