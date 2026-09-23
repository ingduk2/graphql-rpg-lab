package com.rpg.lab.inventory;

import java.util.List;
import java.util.Objects;

public class EnhancementPolicy {

    public static final int MAX_ENHANCE_LEVEL = 10;

    private final List<EnhanceRate> rates;
    private final int costPerLevel;

    public EnhancementPolicy(List<EnhanceRate> rates, int costPerLevel) {
        if (Objects.requireNonNull(rates).size() != MAX_ENHANCE_LEVEL) {
            throw new IllegalArgumentException("rates must have " + MAX_ENHANCE_LEVEL + " entries");
        }
        this.rates = List.copyOf(rates);
        this.costPerLevel = costPerLevel;
    }

    public int successRateFor(int currentLevel) {
        return rates.stream()
                .filter(it -> it.level() == currentLevel)
                .findFirst()
                .map(EnhanceRate::successRate)
                .orElseThrow(() -> new IllegalArgumentException("No rate defined for level: " + currentLevel));
    }

    public int costFor(int currentLevel) {
        return (currentLevel + 1) * costPerLevel;
    }
}
