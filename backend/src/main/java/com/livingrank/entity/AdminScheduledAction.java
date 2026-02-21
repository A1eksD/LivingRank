package com.livingrank.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "admin_scheduled_actions")
public class AdminScheduledAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id", nullable = false)
    private User targetUser;

    @Column(name = "action_type", nullable = false, length = 30)
    private String actionType;

    @Column(length = 500)
    private String reason;

    @Column(nullable = false)
    private LocalDateTime deadline;

    @Column(nullable = false)
    private boolean executed = false;

    @Column(name = "executed_at")
    private LocalDateTime executedAt;

    @Column(nullable = false)
    private boolean cancelled = false;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_mail_id")
    private AdminMail relatedMail;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public AdminScheduledAction() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getAdmin() { return admin; }
    public void setAdmin(User admin) { this.admin = admin; }

    public User getTargetUser() { return targetUser; }
    public void setTargetUser(User targetUser) { this.targetUser = targetUser; }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }

    public boolean isExecuted() { return executed; }
    public void setExecuted(boolean executed) { this.executed = executed; }

    public LocalDateTime getExecutedAt() { return executedAt; }
    public void setExecutedAt(LocalDateTime executedAt) { this.executedAt = executedAt; }

    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }

    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; }

    public AdminMail getRelatedMail() { return relatedMail; }
    public void setRelatedMail(AdminMail relatedMail) { this.relatedMail = relatedMail; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
