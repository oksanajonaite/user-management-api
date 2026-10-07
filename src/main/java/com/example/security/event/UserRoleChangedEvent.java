package com.example.security.event;

public record UserRoleChangedEvent(String username, String newRole, String actor) implements UserEvent {
}
