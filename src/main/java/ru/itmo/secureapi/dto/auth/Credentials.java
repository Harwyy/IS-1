package ru.itmo.secureapi.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record Credentials(@NotBlank @Size(max = 64) String username,
                          @NotBlank @Size(min = 8, max = 128) String password) {
}
