package com.rpg.lab.quest;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestService {

    private final QuestRepository questRepository;

    public List<Quest> getQuests() {
        return questRepository.findAll();
    }

    public Quest getQuest(Long id) {
        return questRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quest not found: " + id));
    }
}
