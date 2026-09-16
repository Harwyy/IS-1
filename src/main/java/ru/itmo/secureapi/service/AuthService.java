package ru.itmo.secureapi.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.itmo.secureapi.security.JwtService;
import ru.itmo.secureapi.dto.auth.Credentials;
import ru.itmo.secureapi.dto.auth.Message;
import ru.itmo.secureapi.dto.auth.TokenResponse;
import ru.itmo.secureapi.entity.AppUser;
import ru.itmo.secureapi.repository.AppUserRepository;

import java.util.Locale;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final AppUserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager, AppUserRepository users,
                       PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public Message register(Credentials request) {
        String username = normalizeUsername(request.username());
        if (users.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }
        try {
            users.save(new AppUser(username, passwordEncoder.encode(request.password())));
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists", exception);
        }
        return new Message("User registered");
    }

    public TokenResponse login(Credentials request) {
        String username = normalizeUsername(request.username());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, request.password()));
        return new TokenResponse(jwtService.generateToken((UserDetails) authentication.getPrincipal()));
    }

    private String normalizeUsername(String username) {
        return username.trim().toLowerCase(Locale.ROOT);
    }
}
