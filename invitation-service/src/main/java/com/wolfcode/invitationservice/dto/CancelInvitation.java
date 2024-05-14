package com.wolfcode.invitationservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancelInvitation {

    @NotEmpty
    @Email
    private String to;
    @NotBlank
    private String subject;
    @NotBlank
    private String body;
}
