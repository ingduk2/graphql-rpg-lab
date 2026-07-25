package com.rpg.lab.quest;

import com.rpg.lab.item.Item;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "quests")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Quest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @OneToMany(mappedBy = "quest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestCondition> conditions = new ArrayList<>();

    @OneToMany(mappedBy = "quest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestReward> rewards = new ArrayList<>();

    public static Quest create(
            String title,
            String description,
            QuestType conditionType,
            int targetCount,
            Long targetMonsterId
    ) {
        Quest quest = new Quest();
        quest.title = Objects.requireNonNull(title);
        quest.description = description;
        quest.conditions.add(QuestCondition.create(quest, conditionType, targetCount, targetMonsterId));
        return quest;
    }

    public Quest addCondition(QuestType type, int targetCount, Long targetMonsterId) {
        conditions.add(QuestCondition.create(this, type, targetCount, targetMonsterId));
        return this;
    }

    public Quest addReward(int rewardExp, Item item) {
        rewards.add(QuestReward.create(this, rewardExp, item));
        return this;
    }
}
