package com.wolfcode.invitationservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wolfcode.invitationservice.dto.*;
import com.wolfcode.invitationservice.exception.InvitationNotFoundException;
import com.wolfcode.invitationservice.service.InvitationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/V1/invite/")
public class InvitationController {

    private final InvitationService invitationService;

    @PostMapping("send-invitation")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> sendInvitation(@Valid @RequestBody InvitationRequest invitationRequest) {
        invitationService.sendInvitation(invitationRequest);
        return ResponseEntity.ok("Invitation has been sent");
    }

    @PostMapping("respond-invitation/{inviteCode}")
    @ResponseStatus(HttpStatus.OK)
    public String respondToInvitation(@PathVariable String inviteCode,
                                      @RequestBody RespondInvitation respondInvitation) throws InvitationNotFoundException {
        return invitationService.respondToInvitation(inviteCode, respondInvitation);
    }

    @DeleteMapping("cancel-invitation/{inviteCode}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> cancelInvitation(@PathVariable String inviteCode, @Valid CancelInvitation cancelInvitation)
            throws InvitationNotFoundException {
        invitationService.cancelInvitation(inviteCode, cancelInvitation);
        return ResponseEntity.ok("Invitation has been revoked");
    }

    @PatchMapping("update/{inviteCode}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> updateInvitation(@RequestBody UpdateInvitation updateInvitation,
                                                   @PathVariable String inviteCode) throws InvitationNotFoundException {
        invitationService.updateInvitation(updateInvitation, inviteCode);
        return ResponseEntity.ok("Invitation details updated successfully");
    }

    @GetMapping("check-status/{inviteCode}")
    @ResponseStatus(HttpStatus.OK)
    public InvitationResponse checkInvitationStatus(@PathVariable String inviteCode) throws InvitationNotFoundException, JsonProcessingException {
        return invitationService.checkInvitationStatus(inviteCode);
    }

    @PostMapping("send-reminder/{inviteCode}")
    @ResponseStatus(HttpStatus.OK)
    public String sendReminder(@PathVariable String inviteCode, @RequestBody ReminderRequest reminderRequest) {
        invitationService.sendReminder(inviteCode, reminderRequest);
        return "A reminder has been sent";
    }

}
