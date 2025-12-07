package com.surnekev.ticketing.config;

import com.surnekev.ticketing.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ApplicationBootstrap {

    private final AdminUserService adminUserService;

    @Bean
    public ApplicationRunner bootstrapRunner() {
        return args -> adminUserService.ensureDefaultAdmin();
    }
}

