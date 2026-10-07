package com.example.security.event;

import com.example.security.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserEventListener {

    private final AuditService auditService;

    // This method runs for ALL event types because they all implement UserEvent
    @EventListener
    public void onAnyUserEvent(UserEvent event) {
        log.info("EVENT: [{}] triggered by actor: {}", event.getClass().getSimpleName(), event.actor());
    }

    @EventListener
    public void onUserRegistered(UserRegisteredEvent event) {
        log.info("EVENT: New user registered - {}", event.username());
        auditService.log("SYSTEM", "REGISTER", event.username());
    }

    @EventListener
    public void onUserDeleted(UserDeletedEvent event) {
        log.info("EVENT: User deleted - {}", event.username());
        auditService.log(event.actor(), "DELETE", event.username());
    }

    @EventListener
    public void onUserRoleChanged(UserRoleChangedEvent event) {
        log.info("EVENT: User {} role changed to {}", event.username(), event.newRole());
        auditService.log(event.actor(), "ROLE_CHANGE", event.username() + " -> " + event.newRole());
    }
}
