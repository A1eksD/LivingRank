package com.livingrank.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "admin_mails")
public class AdminMail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @Column(nullable = false, length = 255)
    private String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    @Column(name = "has_deadline", nullable = false)
    private boolean hasDeadline = false;

    @Column(name = "deadline_action", length = 30)
    private String deadlineAction;

    @Column(name = "sent_at", nullable = false, updatable = false)
    private LocalDateTime sentAt = LocalDateTime.now();

    public AdminMail() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getAdmin() { return admin; }
    public void setAdmin(User admin) { this.admin = admin; }

    public User getRecipient() { return recipient; }
    public void setRecipient(User recipient) { this.recipient = recipient; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public boolean isHasDeadline() { return hasDeadline; }
    public void setHasDeadline(boolean hasDeadline) { this.hasDeadline = hasDeadline; }

    public String getDeadlineAction() { return deadlineAction; }
    public void setDeadlineAction(String deadlineAction) { this.deadlineAction = deadlineAction; }

    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
}
