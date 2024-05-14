package com.wolfcode.invitationservice.repositry;

import com.wolfcode.invitationservice.dto.InvitationStatus;
import com.wolfcode.invitationservice.entity.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    void deleteByInviteCode(String inviteCode);

    Invitation findByInviteCode(String inviteCode);

    List<Invitation> findByStatusAndSentReminderIsFalse(InvitationStatus pending);

    void deleteByRecipient(String to);
}
