package com.rpg.lab.monster;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MonsterScalerConfig {

    private static final double SCALING_RATE_PER_LEVEL = 0.1;

    @Bean
    public MonsterScaler monsterScaler() {
        return new MonsterScaler(SCALING_RATE_PER_LEVEL);
    }
}
