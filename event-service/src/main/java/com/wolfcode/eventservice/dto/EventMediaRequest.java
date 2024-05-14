package com.wolfcode.eventservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventMediaRequest {

    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull(message = "Please choose a file to upload !")
    private MultipartFile file;
}
