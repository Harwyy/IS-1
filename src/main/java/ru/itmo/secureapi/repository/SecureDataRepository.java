package ru.itmo.secureapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.secureapi.entity.SecureData;

public interface SecureDataRepository extends JpaRepository<SecureData, Long> {}
