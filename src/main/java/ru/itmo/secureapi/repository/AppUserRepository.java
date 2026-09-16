package ru.itmo.secureapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import ru.itmo.secureapi.entity.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);
    boolean existsByUsername(String username);
}
