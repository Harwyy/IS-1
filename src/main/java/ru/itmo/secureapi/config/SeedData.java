package ru.itmo.secureapi.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.itmo.secureapi.entity.AppUser;
import ru.itmo.secureapi.entity.SecureData;
import ru.itmo.secureapi.repository.AppUserRepository;
import ru.itmo.secureapi.repository.SecureDataRepository;

@Configuration
public class SeedData {
    @Bean
    CommandLineRunner seed(AppUserRepository users, SecureDataRepository data, PasswordEncoder encoder) {
        return args -> {
            if (!users.existsByUsername("demo")) {
                users.save(new AppUser("demo", encoder.encode("ChangeMe123!")));
            }
            if (data.count() == 0) {
                data.save(new SecureData("Welcome", "Protected data for authenticated users", "demo"));
            }
        };
    }
}
