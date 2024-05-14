package com.wolfcode.invitationservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitationRequest {

    @NotEmpty
    private String recipient;
    @NotBlank
    private String sender;
    @NotBlank
    @Length(min = 10, max = 1000)
    private String description;
}
