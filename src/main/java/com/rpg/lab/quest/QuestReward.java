package com.rpg.lab.quest;

import com.rpg.lab.item.Item;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Table(name = "quest_rewards")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuestReward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quest_id")
    private Quest quest;

    @Column(nullable = false)
    private int rewardExp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    static QuestReward create(
            Quest quest,
            int rewardExp,
            Item item
    ) {
        QuestReward questReward = new QuestReward();
        questReward.quest = Objects.requireNonNull(quest);
        questReward.rewardExp = rewardExp;
        questReward.item = item;
        return questReward;
    }
}
