package ru.itmo.secureapi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.secureapi.security.Sanitizer;
import ru.itmo.secureapi.dto.data.DataRequest;
import ru.itmo.secureapi.dto.data.DataResponse;
import ru.itmo.secureapi.dto.data.ProfileResponse;
import ru.itmo.secureapi.entity.SecureData;
import ru.itmo.secureapi.repository.SecureDataRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class DataService {
    private final SecureDataRepository dataRepository;
    private final Sanitizer sanitizer;

    public DataService(SecureDataRepository dataRepository, Sanitizer sanitizer) {
        this.dataRepository = dataRepository;
        this.sanitizer = sanitizer;
    }

    public List<DataResponse> findAll() {
        return dataRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public DataResponse create(DataRequest request, String username) {
        SecureData data = new SecureData(sanitizer.clean(request.title()),
                sanitizer.clean(request.content()), sanitizer.clean(username));
        return toResponse(dataRepository.save(data));
    }

    public ProfileResponse profile(String username) {
        return new ProfileResponse(sanitizer.clean(username), "USER");
    }

    private DataResponse toResponse(SecureData data) {
        return new DataResponse(data.getId(), sanitizer.clean(data.getTitle()),
                sanitizer.clean(data.getContent()), sanitizer.clean(data.getOwner()), data.getCreatedAt());
    }
}
