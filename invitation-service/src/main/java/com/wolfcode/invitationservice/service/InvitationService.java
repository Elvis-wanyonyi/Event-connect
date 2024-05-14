package com.wolfcode.invitationservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wolfcode.invitationservice.dto.*;
import com.wolfcode.invitationservice.entity.Invitation;
import com.wolfcode.invitationservice.exception.InvitationNotFoundException;
import com.wolfcode.invitationservice.feignClients.EmailClient;
import com.wolfcode.invitationservice.repositry.InvitationRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvitationService {

    private final InvitationRepository invitationRepository;
    private final ObjectMapper objectMapper;
    private final EmailClient emailClient;


    //add a response button in the email
    @Transactional
    public void sendInvitation(InvitationRequest invitationRequest) {
        String sender = invitationRequest.getSender();
        String description = invitationRequest.getDescription();
        LocalDateTime sendAt = LocalDateTime.now();
        String inviteCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Invitation invite = Invitation.builder()
                .inviteCode(inviteCode)
                .sender(sender)
                .recipient(invitationRequest.getRecipient())
                .description(description)
                .sendAt(sendAt)
                .status(InvitationStatus.PENDING)
                .build();
        invitationRepository.save(invite);

        String subject = "Invitation to event";
        emailClient.sendEmail(new EmailRequest(sender, invite.getRecipient(), subject, description));

    }

    public void cancelInvitation(String inviteCode, @Valid CancelInvitation cancelInvitation) throws InvitationNotFoundException {
        Invitation invite = invitationRepository.findByInviteCode(inviteCode);
        if (invite != null) {
            String sender = invite.getSender();
            String to = cancelInvitation.getTo();
            emailClient.sendEmail(new EmailRequest(sender, to, cancelInvitation.getSubject(), cancelInvitation.getBody()));

            invitationRepository.deleteByRecipient(to);
        } else {
            throw new InvitationNotFoundException("Invitation not found");
        }
    }

    public InvitationResponse checkInvitationStatus(String inviteCode) throws InvitationNotFoundException, JsonProcessingException {
        Invitation invite = invitationRepository.findByInviteCode(inviteCode);
        if (invite != null) {
            String inviteJson = objectMapper.writeValueAsString(invite);
            return objectMapper.readValue(inviteJson, InvitationResponse.class);
        } else {
            throw new InvitationNotFoundException("Invitation not found");
        }
    }

    public void updateInvitation(UpdateInvitation updateInvitation, String inviteCode) throws InvitationNotFoundException {
        Invitation invitation = invitationRepository.findByInviteCode(inviteCode);
        if (invitation != null) {

            invitation.setDescription(updateInvitation.getDescription());
            invitation.setSendAt(LocalDateTime.now());
            invitation.setStatus(InvitationStatus.PENDING);

            String sender = invitation.getSender();
            String to = invitation.getRecipient();
            String subject = "Your invitation for the event has been updated";
            String body = updateInvitation.getDescription();
            emailClient.sendEmail(new EmailRequest(sender, to, subject, body));

        } else {
            throw new InvitationNotFoundException("Invitation not found");
        }
    }

    public String respondToInvitation(String inviteCode, RespondInvitation respondInvitation) throws InvitationNotFoundException {
        Invitation invite = invitationRepository.findByInviteCode(inviteCode);

        if (invite != null && invite.getStatus() == InvitationStatus.PENDING) {
            invite.setStatus(respondInvitation.getStatus());
            invite.setComment(respondInvitation.getComment());
            invitationRepository.save(invite);
        } else {
            throw new InvitationNotFoundException("Invalid code or Already Responded to invitation");
        }
        return "Thanks for responding to our invitation";
    }


    public void sendReminder(String inviteCode, ReminderRequest reminderRequest) {
        Invitation invite = invitationRepository.findByInviteCode(inviteCode);
        if (invite != null) {
            invite.setReminderDate(LocalDateTime.now());
            invite.setSentReminder(true);
            invitationRepository.save(invite);

            String to = reminderRequest.getRecipient();
            String from = reminderRequest.getSender();
            String subject = "A reminder to one of your upcoming event";
            String body = reminderRequest.getDescription();
            emailClient.sendEmail(new EmailRequest(from, to, subject, body));
        }
    }
}
