package com.livingrank.controller;

import com.livingrank.dto.StreetDetailResponse;
import com.livingrank.dto.StreetResponse;
import com.livingrank.entity.User;
import com.livingrank.service.StreetService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/streets")
public class StreetController {

    private final StreetService streetService;

    public StreetController(StreetService streetService) {
        this.streetService = streetService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<StreetResponse>> searchStreets(@RequestParam String q) {
        List<StreetResponse> results = streetService.searchStreets(q);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StreetDetailResponse> getStreetDetail(@PathVariable Long id,
                                                                  Authentication authentication) {
        User currentUser = authentication != null ? (User) authentication.getPrincipal() : null;
        StreetDetailResponse detail = streetService.getStreetDetail(id, currentUser);
        return ResponseEntity.ok(detail);
    }
}
