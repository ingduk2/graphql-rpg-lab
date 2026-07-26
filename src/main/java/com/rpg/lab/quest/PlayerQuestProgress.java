package com.rpg.lab.quest;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "player_quest_progress")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlayerQuestProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_quest_id")
    private PlayerQuest playerQuest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quest_condition_id")
    private QuestCondition questCondition;

    @Column(nullable = false)
    private int currentCount;

    static PlayerQuestProgress create(
            PlayerQuest playerQuest,
            QuestCondition questCondition
    ) {
        PlayerQuestProgress playerQuestProgress = new PlayerQuestProgress();
        playerQuestProgress.playerQuest = playerQuest;
        playerQuestProgress.questCondition = questCondition;
        playerQuestProgress.currentCount = 0;
        return playerQuestProgress;
    }

    public void increment() {
        this.currentCount++;
    }

    public boolean isCompleted() {
        return currentCount >= questCondition.getTargetCount();
    }
}
