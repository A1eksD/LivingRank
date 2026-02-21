package com.livingrank.controller.admin;

import com.livingrank.dto.MessageResponse;
import com.livingrank.dto.admin.AdminMailRequest;
import com.livingrank.dto.admin.AdminMailResponse;
import com.livingrank.entity.User;
import com.livingrank.service.admin.AdminMailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/mails")
public class AdminMailController {

    private final AdminMailService adminMailService;

    public AdminMailController(AdminMailService adminMailService) {
        this.adminMailService = adminMailService;
    }

    @PostMapping
    public ResponseEntity<MessageResponse> sendMail(
            @Valid @RequestBody AdminMailRequest request,
            @AuthenticationPrincipal User admin,
            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(adminMailService.sendMail(request, admin, httpRequest.getRemoteAddr()));
    }

    @GetMapping
    public ResponseEntity<Page<AdminMailResponse>> getMails(
            @RequestParam(required = false) UUID recipientId,
            Pageable pageable) {
        return ResponseEntity.ok(adminMailService.getMails(recipientId, pageable));
    }

    @GetMapping("/{mailId}")
    public ResponseEntity<AdminMailResponse> getMail(@PathVariable Long mailId) {
        return ResponseEntity.ok(adminMailService.getMail(mailId));
    }
}
