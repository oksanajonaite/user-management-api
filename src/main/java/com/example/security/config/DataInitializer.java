package com.example.security.config;

import com.example.security.model.Role;
import com.example.security.model.User;
import com.example.security.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class DataInitializer {

    @Value("${app.seed-password:demo}")
    private String seedPassword;

    @Bean
    CommandLineRunner init(UserRepository repository, PasswordEncoder encoder, ObjectMapper objectMapper) {
        return args -> {

            saveIfNotExists(repository, encoder, "user", seedPassword, Role.USER, "user@email.com", "+37060000001");
            saveIfNotExists(repository, encoder, "admin", seedPassword, Role.ADMIN, "admin@email.com", "+37060000002");

            ClassPathResource resource = new ClassPathResource("users.json");
            List<User> users = objectMapper.readValue(
                    resource.getInputStream(),
                    new TypeReference<List<User>>() {}
            );

            for (User user : users) {
                if (repository.findByUsername(user.getUsername()).isEmpty()) {
                    user.setPassword(encoder.encode(user.getPassword()));
                    repository.save(user);
                }
            }
        };
    }

    private void saveIfNotExists(UserRepository repository, PasswordEncoder encoder,
                                  String username, String password, Role role,
                                  String email, String phoneNumber) {
        if (repository.findByUsername(username).isEmpty()) {
            repository.save(User.builder()
                    .username(username)
                    .password(encoder.encode(password))
                    .role(role)
                    .email(email)
                    .phoneNumber(phoneNumber)
                    .build());
        }
    }
}
