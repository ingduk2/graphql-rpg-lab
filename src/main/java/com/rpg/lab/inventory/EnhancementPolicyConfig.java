package com.rpg.lab.inventory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class EnhancementPolicyConfig {

    @Bean
    public EnhancementPolicy enhancementPolicy() {
        return new EnhancementPolicy(
                List.of(
                        new EnhanceRate(0, 95),
                        new EnhanceRate(1, 90),
                        new EnhanceRate(2, 85),
                        new EnhanceRate(3, 80),
                        new EnhanceRate(4, 70),
                        new EnhanceRate(5, 60),
                        new EnhanceRate(6, 50),
                        new EnhanceRate(7, 40),
                        new EnhanceRate(8, 30),
                        new EnhanceRate(9, 20)
                ),
                50 // costPerLevel
        );
    }
}
