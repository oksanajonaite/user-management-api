package com.example.security.event;

public record UserDeletedEvent(String username, String actor) implements UserEvent {
}
