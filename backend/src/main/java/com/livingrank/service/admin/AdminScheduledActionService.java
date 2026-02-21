package com.livingrank.service.admin;

import com.livingrank.dto.MessageResponse;
import com.livingrank.dto.admin.ScheduledActionResponse;
import com.livingrank.entity.AdminScheduledAction;
import com.livingrank.entity.User;
import com.livingrank.exception.BadRequestException;
import com.livingrank.repository.AdminScheduledActionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class AdminScheduledActionService {

    private final AdminScheduledActionRepository actionRepository;
    private final AdminAuditService auditService;

    public AdminScheduledActionService(AdminScheduledActionRepository actionRepository,
                                        AdminAuditService auditService) {
        this.actionRepository = actionRepository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public Page<ScheduledActionResponse> getActions(boolean pendingOnly, Pageable pageable) {
        if (pendingOnly) {
            return actionRepository.findByExecutedFalseAndCancelledFalseOrderByDeadlineAsc(pageable)
                    .map(ScheduledActionResponse::fromEntity);
        }
        return actionRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(ScheduledActionResponse::fromEntity);
    }

    @Transactional
    public MessageResponse cancelAction(Long actionId, User admin, String ip) {
        AdminScheduledAction action = actionRepository.findById(actionId)
                .orElseThrow(() -> new BadRequestException("Aktion nicht gefunden."));

        if (action.isExecuted()) {
            throw new BadRequestException("Aktion wurde bereits ausgeführt.");
        }
        if (action.isCancelled()) {
            throw new BadRequestException("Aktion wurde bereits abgebrochen.");
        }

        action.setCancelled(true);
        action.setCancelledAt(LocalDateTime.now());
        actionRepository.save(action);

        auditService.log(admin, "SCHEDULED_ACTION_CANCELLED", "SCHEDULED_ACTION", actionId.toString(),
                Map.of("actionType", action.getActionType(),
                       "targetUserId", action.getTargetUser().getId().toString()), ip);

        return new MessageResponse("Frist-Aktion wurde abgebrochen.");
    }

    @Transactional
    public MessageResponse extendDeadline(Long actionId, LocalDateTime newDeadline, User admin, String ip) {
        AdminScheduledAction action = actionRepository.findById(actionId)
                .orElseThrow(() -> new BadRequestException("Aktion nicht gefunden."));

        if (action.isExecuted()) {
            throw new BadRequestException("Aktion wurde bereits ausgeführt.");
        }
        if (action.isCancelled()) {
            throw new BadRequestException("Aktion wurde bereits abgebrochen.");
        }

        String oldDeadline = action.getDeadline().toString();
        action.setDeadline(newDeadline);
        actionRepository.save(action);

        auditService.log(admin, "SCHEDULED_ACTION_EXTENDED", "SCHEDULED_ACTION", actionId.toString(),
                Map.of("oldDeadline", oldDeadline, "newDeadline", newDeadline.toString()), ip);

        return new MessageResponse("Frist wurde verlängert.");
    }

    @Transactional(readOnly = true)
    public long countPending() {
        return actionRepository.countByExecutedFalseAndCancelledFalse();
    }
}
