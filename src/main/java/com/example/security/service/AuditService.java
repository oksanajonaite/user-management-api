package com.example.security.service;

import com.example.security.dto.AuditLogResponse;
import com.example.security.model.AuditLog;
import com.example.security.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public void log(String actor, String action, String target) {
        var auditLog = AuditLog.builder()
                .actor(actor)
                .action(action)
                .target(target)
                .timestamp(LocalDateTime.now())
                .build();
        auditLogRepository.save(auditLog);
        log.info("AUDIT: {} {} {}", actor, action, target);
    }

    public List<AuditLogResponse> findAll() {
        return auditLogRepository.findAll().stream()
                .map(log -> new AuditLogResponse(
                        log.getId(),
                        log.getActor(),
                        log.getAction(),
                        log.getTarget(),
                        log.getTimestamp()
                )).toList();
    }
}
