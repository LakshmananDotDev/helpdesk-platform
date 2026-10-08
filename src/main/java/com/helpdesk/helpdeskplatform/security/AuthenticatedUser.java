package com.helpdesk.helpdeskplatform.security;

public record AuthenticatedUser(Long userId, String email, Long organizationId, String role) {
    public boolean isCustomer(){
        return "CUSTOMER".equals(role);
    }
}
