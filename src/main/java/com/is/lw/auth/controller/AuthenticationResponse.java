package com.is.lw.auth.controller;

import com.is.lw.auth.model.enums.Status;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

    private Status status;
    private String token;
    private String message;

}
