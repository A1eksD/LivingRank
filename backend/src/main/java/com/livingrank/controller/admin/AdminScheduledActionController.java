package com.livingrank.controller.admin;

import com.livingrank.dto.MessageResponse;
import com.livingrank.dto.admin.ExtendDeadlineRequest;
import com.livingrank.dto.admin.ScheduledActionResponse;
import com.livingrank.entity.User;
import com.livingrank.service.admin.AdminScheduledActionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/scheduled-actions")
public class AdminScheduledActionController {

    private final AdminScheduledActionService scheduledActionService;

    public AdminScheduledActionController(AdminScheduledActionService scheduledActionService) {
        this.scheduledActionService = scheduledActionService;
    }

    @GetMapping
    public ResponseEntity<Page<ScheduledActionResponse>> getActions(
            @RequestParam(defaultValue = "false") boolean pendingOnly,
            Pageable pageable) {
        return ResponseEntity.ok(scheduledActionService.getActions(pendingOnly, pageable));
    }

    @PostMapping("/{actionId}/cancel")
    public ResponseEntity<MessageResponse> cancelAction(
            @PathVariable Long actionId,
            @AuthenticationPrincipal User admin,
            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(scheduledActionService.cancelAction(actionId, admin, httpRequest.getRemoteAddr()));
    }

    @PutMapping("/{actionId}/extend")
    public ResponseEntity<MessageResponse> extendDeadline(
            @PathVariable Long actionId,
            @Valid @RequestBody ExtendDeadlineRequest request,
            @AuthenticationPrincipal User admin,
            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(scheduledActionService.extendDeadline(actionId, request.newDeadline(), admin, httpRequest.getRemoteAddr()));
    }
}
