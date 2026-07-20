package com.rpg.lab.quest;

import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface QuestRepository extends Repository<Quest, Long> {
    Optional<Quest> findById(Long id);
    List<Quest> findAll();
    Quest save(Quest quest);
}
