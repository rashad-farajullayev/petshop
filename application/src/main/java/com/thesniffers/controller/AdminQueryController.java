package com.thesniffers.controller;

import com.thesniffers.service.AdminQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminQueryController {

    private final AdminQueryService adminQueryService;

    public AdminQueryController(AdminQueryService adminQueryService) {
        this.adminQueryService = adminQueryService;
    }

    @GetMapping("/query")
    public ResponseEntity<?> queryDatabase(
            @RequestParam String entity,
            @RequestParam(required = false) String filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String sort) {

        return ResponseEntity.ok(adminQueryService.queryEntity(entity, filter, page, size, sort));
    }
}
