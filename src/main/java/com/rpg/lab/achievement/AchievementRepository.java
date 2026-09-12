package com.rpg.lab.achievement;

import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface AchievementRepository extends Repository<Achievement, Long> {
    Optional<Achievement> findById(Long id);
    Achievement save(Achievement achievement);
    List<Achievement> findAll();
}
