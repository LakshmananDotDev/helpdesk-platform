package com.helpdesk.helpdeskplatform.config;

import com.helpdesk.helpdeskplatform.security.AuthenticatedUser;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.Session;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class TenantAwareJpaTransactionManager extends JpaTransactionManager {

    public TenantAwareJpaTransactionManager(EntityManagerFactory emf){
        super(emf);
    }

    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition){
        super.doBegin(transaction, definition);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null && auth.getPrincipal() instanceof AuthenticatedUser user){
            EntityManagerHolder holder = (EntityManagerHolder)
                    TransactionSynchronizationManager.getResource(obtainEntityManagerFactory());
            if(holder != null){
                holder.getEntityManager()
                        .unwrap(Session.class)
                        .enableFilter("tenantFilter")
                        .setParameter("organizationId", user.organizationId());
            }
        }
    }

}
