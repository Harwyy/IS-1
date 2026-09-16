package ru.itmo.secureapi.dto.data;

import java.time.Instant;

public record DataResponse(Long id, String title, String content, String owner, Instant createdAt) {
}
