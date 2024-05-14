package com.wolfcode.eventservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wolfcode.eventservice.customValidation.ValidateCategory;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.time.LocalTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateEventRequest {

    @NotBlank(message = "Please enter the name of your event")
    private String eventName;
    @NotBlank(message = "Enter some information about your event")
    @Length(min = 30, max = 1000)
    private String description;
    @JsonFormat(pattern = "dd-MM-yyyy")
    @FutureOrPresent
    private LocalDate date;
    @NotNull
    @JsonFormat(pattern = "HH:mm")
    private LocalTime time;
    @NotBlank(message = "Enter event location")
    private String location;
    @Email(message = "Enter a valid email")
    private String organizer;
    @ValidateCategory
    @NotBlank
    private EventCategory eventCategory;

}
