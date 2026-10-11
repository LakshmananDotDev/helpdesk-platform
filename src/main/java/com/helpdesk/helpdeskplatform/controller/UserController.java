package com.helpdesk.helpdeskplatform.controller;

import com.helpdesk.helpdeskplatform.dto.request.CreateStaffRequest;
import com.helpdesk.helpdeskplatform.dto.response.UserResponse;
import com.helpdesk.helpdeskplatform.security.AuthenticatedUser;
import com.helpdesk.helpdeskplatform.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createStaff(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody CreateStaffRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createStaff(user, request));
    }
}