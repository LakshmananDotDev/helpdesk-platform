package com.helpdesk.helpdeskplatform.service;

import com.helpdesk.helpdeskplatform.dto.request.CreateStaffRequest;
import com.helpdesk.helpdeskplatform.dto.response.UserResponse;
import com.helpdesk.helpdeskplatform.entity.Organization;
import com.helpdesk.helpdeskplatform.entity.User;
import com.helpdesk.helpdeskplatform.repository.OrganizationRepository;
import com.helpdesk.helpdeskplatform.repository.UserRepository;
import com.helpdesk.helpdeskplatform.security.AuthenticatedUser;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse createStaff(AuthenticatedUser actor, CreateStaffRequest request) {
        if(request.getRole() == User.Role.CUSTOMER){
            throw new IllegalArgumentException("Staff must be AGENT or ADMIN. Customers register themselves");
        }
        if(userRepository.existsByEmail(request.getEmail())){
            throw new IllegalArgumentException("Email already in use");
        }

        Organization organization = organizationRepository.findById(actor.organizationId())
                .orElseThrow(() -> new IllegalArgumentException("Organization id not found"));

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setOrganization(organization);

        User saved = userRepository.save(user);

        return new UserResponse(saved.getId(), saved.getEmail(), saved.getRole().name(), organization.getId());
    }

}
