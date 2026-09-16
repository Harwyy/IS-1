package ru.itmo.secureapi.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import ru.itmo.secureapi.dto.data.DataRequest;
import ru.itmo.secureapi.dto.data.DataResponse;
import ru.itmo.secureapi.dto.data.ProfileResponse;
import ru.itmo.secureapi.service.DataService;

@RestController
@RequestMapping("/api")
public class DataController {
    private final DataService dataService;

    public DataController(DataService dataService) {
        this.dataService = dataService;
    }

    @GetMapping("/data")
    public List<DataResponse> getData() {
        return dataService.findAll();
    }

    @PostMapping("/data")
    @ResponseStatus(HttpStatus.CREATED)
    public DataResponse create(@Valid @RequestBody DataRequest request, Authentication authentication) {
        return dataService.create(request, authentication.getName());
    }

    @GetMapping("/me")
    public ProfileResponse me(Authentication authentication) {
        return dataService.profile(authentication.getName());
    }

}
