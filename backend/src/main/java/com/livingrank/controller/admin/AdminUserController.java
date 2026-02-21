package com.livingrank.controller.admin;

import com.livingrank.dto.MessageResponse;
import com.livingrank.dto.admin.*;
import com.livingrank.entity.User;
import com.livingrank.service.admin.AdminUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping
    public ResponseEntity<Page<AdminUserResponse>> getUsers(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            Pageable pageable) {
        return ResponseEntity.ok(adminUserService.getUsers(status, search, pageable));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<AdminUserResponse> getUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(adminUserService.getUser(userId));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<AdminUserResponse> updateUser(
            @PathVariable UUID userId,
            @Valid @RequestBody AdminUserUpdateRequest request,
            @AuthenticationPrincipal User admin,
            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(adminUserService.updateUser(userId, request, admin, httpRequest.getRemoteAddr()));
    }

    @PostMapping("/{userId}/suspend")
    public ResponseEntity<MessageResponse> suspendUser(
            @PathVariable UUID userId,
            @Valid @RequestBody SuspendUserRequest request,
            @AuthenticationPrincipal User admin,
            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(adminUserService.suspendUser(userId, request.reason(), admin, httpRequest.getRemoteAddr()));
    }

    @PostMapping("/{userId}/unsuspend")
    public ResponseEntity<MessageResponse> unsuspendUser(
            @PathVariable UUID userId,
            @AuthenticationPrincipal User admin,
            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(adminUserService.unsuspendUser(userId, admin, httpRequest.getRemoteAddr()));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<MessageResponse> deleteUser(
            @PathVariable UUID userId,
            @AuthenticationPrincipal User admin,
            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(adminUserService.softDeleteUser(userId, admin, httpRequest.getRemoteAddr()));
    }

    @PutMapping("/{userId}/role")
    public ResponseEntity<MessageResponse> changeRole(
            @PathVariable UUID userId,
            @Valid @RequestBody ChangeRoleRequest request,
            @AuthenticationPrincipal User admin,
            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(adminUserService.changeRole(userId, request.role(), admin, httpRequest.getRemoteAddr()));
    }
}
