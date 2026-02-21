package com.livingrank.scheduler;

import com.livingrank.entity.AdminScheduledAction;
import com.livingrank.repository.AdminScheduledActionRepository;
import com.livingrank.service.admin.AdminAuditService;
import com.livingrank.service.admin.AdminUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
public class AdminDeadlineScheduler {

    private static final Logger log = LoggerFactory.getLogger(AdminDeadlineScheduler.class);

    private final AdminScheduledActionRepository actionRepository;
    private final AdminUserService adminUserService;
    private final AdminAuditService auditService;

    public AdminDeadlineScheduler(AdminScheduledActionRepository actionRepository,
                                   AdminUserService adminUserService,
                                   AdminAuditService auditService) {
        this.actionRepository = actionRepository;
        this.adminUserService = adminUserService;
        this.auditService = auditService;
    }

    @Scheduled(fixedDelayString = "${admin.scheduler.check-interval-ms:60000}")
    @Transactional
    public void executeOverdueActions() {
        List<AdminScheduledAction> overdueActions = actionRepository
                .findByExecutedFalseAndCancelledFalseAndDeadlineBefore(LocalDateTime.now());

        for (AdminScheduledAction action : overdueActions) {
            try {
                executeAction(action);
                action.setExecuted(true);
                action.setExecutedAt(LocalDateTime.now());
                actionRepository.save(action);

                auditService.log(action.getAdmin(), "SCHEDULED_ACTION_EXECUTED", "USER",
                        action.getTargetUser().getId().toString(),
                        Map.of("actionType", action.getActionType(),
                               "actionId", action.getId(),
                               "deadline", action.getDeadline().toString()), "SYSTEM");

                log.info("Scheduled action {} executed: {} on user {}",
                        action.getId(), action.getActionType(), action.getTargetUser().getId());

            } catch (Exception e) {
                log.error("Failed to execute scheduled action {}: {}", action.getId(), e.getMessage(), e);
            }
        }
    }

    private void executeAction(AdminScheduledAction action) {
        String actionType = action.getActionType().toUpperCase();
        switch (actionType) {
            case "SUSPEND" -> adminUserService.suspendUser(
                    action.getTargetUser().getId(),
                    "Automatische Sperrung nach Fristablauf. " + (action.getReason() != null ? action.getReason() : ""),
                    action.getAdmin());
            case "DELETE" -> adminUserService.softDeleteUser(
                    action.getTargetUser().getId(),
                    action.getAdmin());
            case "HIDE_REVIEWS" -> adminUserService.hideAllReviewsOfUser(
                    action.getTargetUser().getId(),
                    action.getAdmin());
            default -> log.warn("Unknown scheduled action type: {}", actionType);
        }
    }
}
