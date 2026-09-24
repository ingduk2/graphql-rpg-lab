package com.rpg.lab.testsupport;

import com.rpg.lab.common.RandomProvider;
import com.rpg.lab.inventory.EnhanceRate;
import com.rpg.lab.inventory.EnhancementPolicy;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.List;

@TestConfiguration
public class RpgTestConfiguration {

    @Bean
    public RandomProvider randomProvider() {
        return new RandomProvider() {
            @Override
            public double nextDouble() {
                return 0;
            }

            @Override
            public int nextInt(int bound) {
                return 0;
            }
        };
    }

    @Bean
    @Primary
    public EnhancementPolicy testEnhancementPolicy() {
        return new EnhancementPolicy(
                List.of(
                        new EnhanceRate(0, 100),
                        new EnhanceRate(1, 100),
                        new EnhanceRate(2, 100),
                        new EnhanceRate(3, 100),
                        new EnhanceRate(4, 100),
                        new EnhanceRate(5, 100),
                        new EnhanceRate(6, 100),
                        new EnhanceRate(7, 100),
                        new EnhanceRate(8, 100),
                        new EnhanceRate(9, 100)
                ),
                50
        );
    }
}
