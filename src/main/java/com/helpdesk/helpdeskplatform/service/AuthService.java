package com.helpdesk.helpdeskplatform.service;

import com.helpdesk.helpdeskplatform.dto.request.LoginRequest;
import com.helpdesk.helpdeskplatform.dto.request.RegisterRequest;
import com.helpdesk.helpdeskplatform.dto.response.AuthResponse;
import com.helpdesk.helpdeskplatform.entity.Organization;
import com.helpdesk.helpdeskplatform.entity.User;
import com.helpdesk.helpdeskplatform.exception.ResourceNotFoundException;
import com.helpdesk.helpdeskplatform.repository.OrganizationRepository;
import com.helpdesk.helpdeskplatform.repository.UserRepository;
import com.helpdesk.helpdeskplatform.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists" + request.getEmail());
        }
        Organization organization = organizationRepository.findById(request.getOrganizationId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Organization not found: " + request.getOrganizationId()
                ));

        User  user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setOrganization(organization);

        User saved = userRepository.save(user);
        String token = jwtService.generateToken(saved);

        return new AuthResponse(token, saved.getEmail(),saved.getRole().name());
    }

    @Transactional()
    public AuthResponse login(LoginRequest request){
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if(!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user.getEmail(), user.getRole().name());
    }
}
