package com.rpg.lab.testsupport;

import com.rpg.lab.common.RandomProvider;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class RpgTestConfiguration {

    @Bean
    public RandomProvider randomProvider() {
        return () -> 0;
    }
}
