package com.livingrank.service.admin;

import com.livingrank.dto.MessageResponse;
import com.livingrank.dto.admin.AdminMailRequest;
import com.livingrank.dto.admin.AdminMailResponse;
import com.livingrank.entity.AdminMail;
import com.livingrank.entity.AdminScheduledAction;
import com.livingrank.entity.User;
import com.livingrank.exception.BadRequestException;
import com.livingrank.repository.AdminMailRepository;
import com.livingrank.repository.AdminScheduledActionRepository;
import com.livingrank.repository.UserRepository;
import com.livingrank.service.EmailService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class AdminMailService {

    private final AdminMailRepository mailRepository;
    private final AdminScheduledActionRepository scheduledActionRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final AdminAuditService auditService;

    public AdminMailService(AdminMailRepository mailRepository,
                            AdminScheduledActionRepository scheduledActionRepository,
                            UserRepository userRepository,
                            EmailService emailService,
                            AdminAuditService auditService) {
        this.mailRepository = mailRepository;
        this.scheduledActionRepository = scheduledActionRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.auditService = auditService;
    }

    @Transactional
    public MessageResponse sendMail(AdminMailRequest request, User admin, String ip) {
        User recipient = userRepository.findById(request.recipientId())
                .orElseThrow(() -> new BadRequestException("Empfänger nicht gefunden."));

        boolean hasDeadline = request.deadline() != null && request.deadlineAction() != null;

        if (hasDeadline && request.deadline().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Frist muss in der Zukunft liegen.");
        }

        // Save mail record
        AdminMail mail = new AdminMail();
        mail.setAdmin(admin);
        mail.setRecipient(recipient);
        mail.setSubject(request.subject());
        mail.setBody(request.body());
        mail.setHasDeadline(hasDeadline);
        mail.setDeadlineAction(hasDeadline ? request.deadlineAction() : null);
        mailRepository.save(mail);

        // Send actual email
        emailService.sendAdminMail(recipient.getEmail(), request.subject(), request.body());

        // Create scheduled action if deadline is set
        if (hasDeadline) {
            AdminScheduledAction action = new AdminScheduledAction();
            action.setAdmin(admin);
            action.setTargetUser(recipient);
            action.setActionType(request.deadlineAction());
            action.setReason("Automatische Aktion nach Fristablauf. Mail-ID: " + mail.getId());
            action.setDeadline(request.deadline());
            action.setRelatedMail(mail);
            scheduledActionRepository.save(action);

            auditService.log(admin, "MAIL_SENT_WITH_DEADLINE", "USER", recipient.getId().toString(),
                    Map.of("subject", request.subject(), "deadline", request.deadline().toString(),
                           "deadlineAction", request.deadlineAction(), "mailId", mail.getId()), ip);
        } else {
            auditService.log(admin, "MAIL_SENT", "USER", recipient.getId().toString(),
                    Map.of("subject", request.subject(), "mailId", mail.getId()), ip);
        }

        return new MessageResponse("E-Mail wurde gesendet." +
                (hasDeadline ? " Frist-Aktion wurde angelegt." : ""));
    }

    @Transactional(readOnly = true)
    public Page<AdminMailResponse> getMails(UUID recipientId, Pageable pageable) {
        if (recipientId != null) {
            return mailRepository.findByRecipientIdOrderBySentAtDesc(recipientId, pageable)
                    .map(AdminMailResponse::fromEntity);
        }
        return mailRepository.findAllByOrderBySentAtDesc(pageable)
                .map(AdminMailResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public AdminMailResponse getMail(Long mailId) {
        AdminMail mail = mailRepository.findById(mailId)
                .orElseThrow(() -> new BadRequestException("Mail nicht gefunden."));
        return AdminMailResponse.fromEntity(mail);
    }
}
