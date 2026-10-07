package com.example.security.event;

public record UserRegisteredEvent(String username, String actor) implements UserEvent {
}
