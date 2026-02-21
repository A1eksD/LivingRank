package com.livingrank.service.admin;

import com.livingrank.dto.MessageResponse;
import com.livingrank.dto.admin.AdminUserResponse;
import com.livingrank.dto.admin.AdminUserUpdateRequest;
import com.livingrank.entity.Role;
import com.livingrank.entity.User;
import com.livingrank.entity.UserStatus;
import com.livingrank.exception.BadRequestException;
import com.livingrank.repository.ReviewRepository;
import com.livingrank.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class AdminUserService {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final AdminAuditService auditService;

    public AdminUserService(UserRepository userRepository, ReviewRepository reviewRepository,
                            AdminAuditService auditService) {
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public Page<AdminUserResponse> getUsers(String status, String search, Pageable pageable) {
        if (search != null && !search.isBlank()) {
            return userRepository.searchByEmailOrDisplayName(search, pageable)
                    .map(AdminUserResponse::fromEntity);
        }
        if (status != null && !status.isBlank()) {
            UserStatus userStatus = UserStatus.valueOf(status.toUpperCase());
            return userRepository.findByStatus(userStatus, pageable)
                    .map(AdminUserResponse::fromEntity);
        }
        return userRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(AdminUserResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public AdminUserResponse getUser(UUID userId) {
        User user = findUserOrThrow(userId);
        return AdminUserResponse.fromEntity(user);
    }

    @Transactional
    public AdminUserResponse updateUser(UUID userId, AdminUserUpdateRequest request, User admin, String ip) {
        User user = findUserOrThrow(userId);
        String oldEmail = user.getEmail();
        String oldName = user.getDisplayName();

        if (request.displayName() != null && !request.displayName().isBlank()) {
            user.setDisplayName(sanitize(request.displayName()));
        }
        if (request.email() != null && !request.email().isBlank()) {
            user.setEmail(request.email());
        }
        userRepository.save(user);

        auditService.log(admin, "USER_EDITED", "USER", userId.toString(),
                Map.of("oldEmail", oldEmail, "oldName", oldName,
                       "newEmail", user.getEmail(), "newName", user.getDisplayName()), ip);

        return AdminUserResponse.fromEntity(user);
    }

    @Transactional
    public MessageResponse suspendUser(UUID userId, String reason, User admin, String ip) {
        User user = findUserOrThrow(userId);
        preventSelfAction(admin, user);
        preventActionOnHigherRole(admin, user);

        user.setStatus(UserStatus.SUSPENDED);
        user.setSuspendedAt(LocalDateTime.now());
        user.setSuspendedReason(reason);
        userRepository.save(user);

        auditService.log(admin, "USER_SUSPENDED", "USER", userId.toString(),
                Map.of("reason", reason), ip);

        return new MessageResponse("Benutzer wurde gesperrt.");
    }

    /**
     * Overloaded for use by the scheduler (no IP available).
     */
    @Transactional
    public void suspendUser(UUID userId, String reason, User admin) {
        suspendUser(userId, reason, admin, "SYSTEM");
    }

    @Transactional
    public MessageResponse unsuspendUser(UUID userId, User admin, String ip) {
        User user = findUserOrThrow(userId);

        user.setStatus(UserStatus.ACTIVE);
        user.setSuspendedAt(null);
        user.setSuspendedReason(null);
        userRepository.save(user);

        auditService.log(admin, "USER_UNSUSPENDED", "USER", userId.toString(), null, ip);

        return new MessageResponse("Sperre wurde aufgehoben.");
    }

    @Transactional
    public MessageResponse softDeleteUser(UUID userId, User admin, String ip) {
        User user = findUserOrThrow(userId);
        preventSelfAction(admin, user);

        user.setStatus(UserStatus.DELETED);
        userRepository.save(user);

        auditService.log(admin, "USER_DELETED", "USER", userId.toString(), null, ip);

        return new MessageResponse("Benutzer wurde gelöscht.");
    }

    /**
     * Overloaded for use by the scheduler.
     */
    @Transactional
    public void softDeleteUser(UUID userId, User admin) {
        softDeleteUser(userId, admin, "SYSTEM");
    }

    @Transactional
    public MessageResponse changeRole(UUID userId, String roleName, User admin, String ip) {
        User user = findUserOrThrow(userId);
        preventSelfAction(admin, user);

        Role newRole;
        try {
            newRole = Role.valueOf(roleName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Ungültige Rolle: " + roleName);
        }

        String oldRole = user.getRole().name();
        user.setRole(newRole);
        userRepository.save(user);

        auditService.log(admin, "USER_ROLE_CHANGED", "USER", userId.toString(),
                Map.of("oldRole", oldRole, "newRole", newRole.name()), ip);

        return new MessageResponse("Rolle wurde geändert zu " + newRole.name() + ".");
    }

    @Transactional
    public void hideAllReviewsOfUser(UUID userId, User admin) {
        reviewRepository.hideAllByUserId(userId);
        auditService.log(admin, "USER_REVIEWS_HIDDEN", "USER", userId.toString(), null, "SYSTEM");
    }

    @Transactional(readOnly = true)
    public long countByStatus(UserStatus status) {
        return userRepository.countByStatus(status);
    }

    private User findUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("Benutzer nicht gefunden."));
    }

    private void preventSelfAction(User admin, User target) {
        if (admin.getId().equals(target.getId())) {
            throw new BadRequestException("Sie können diese Aktion nicht auf sich selbst anwenden.");
        }
    }

    private void preventActionOnHigherRole(User admin, User target) {
        if (target.getRole().ordinal() >= admin.getRole().ordinal()) {
            throw new BadRequestException("Sie können diese Aktion nicht auf einen Benutzer mit gleicher oder höherer Rolle anwenden.");
        }
    }

    private String sanitize(String text) {
        if (text == null) return null;
        return text.replaceAll("<[^>]*>", "").trim();
    }
}
