package com.livingrank.controller.admin;

import com.livingrank.dto.admin.AdminDashboardResponse;
import com.livingrank.entity.UserStatus;
import com.livingrank.repository.StreetRepository;
import com.livingrank.service.admin.AdminReviewService;
import com.livingrank.service.admin.AdminScheduledActionService;
import com.livingrank.service.admin.AdminUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    private final AdminUserService adminUserService;
    private final AdminReviewService adminReviewService;
    private final AdminScheduledActionService scheduledActionService;
    private final StreetRepository streetRepository;

    public AdminDashboardController(AdminUserService adminUserService,
                                     AdminReviewService adminReviewService,
                                     AdminScheduledActionService scheduledActionService,
                                     StreetRepository streetRepository) {
        this.adminUserService = adminUserService;
        this.adminReviewService = adminReviewService;
        this.scheduledActionService = scheduledActionService;
        this.streetRepository = streetRepository;
    }

    @GetMapping
    public ResponseEntity<AdminDashboardResponse> getDashboard() {
        long totalUsers = adminUserService.countByStatus(UserStatus.ACTIVE)
                + adminUserService.countByStatus(UserStatus.SUSPENDED)
                + adminUserService.countByStatus(UserStatus.DELETED);
        long activeUsers = adminUserService.countByStatus(UserStatus.ACTIVE);
        long suspendedUsers = adminUserService.countByStatus(UserStatus.SUSPENDED);
        long totalReviews = adminReviewService.countTotalReviews();
        long hiddenReviews = adminReviewService.countHiddenReviews();
        long pendingActions = scheduledActionService.countPending();
        long totalStreets = streetRepository.count();

        return ResponseEntity.ok(new AdminDashboardResponse(
                totalUsers, activeUsers, suspendedUsers,
                totalReviews, hiddenReviews, pendingActions, totalStreets
        ));
    }
}
