package ru.itmo.secureapi.dto.data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DataRequest(@NotBlank @Size(max = 120) String title,
                          @NotBlank @Size(max = 4000) String content) {
}
