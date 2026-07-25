package com.rpg.lab.quest;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Table(name = "quest_conditions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuestCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quest_id")
    private Quest quest;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private QuestType type;

    @Column(nullable = false)
    private int targetCount;

    @Column
    private Long targetMonsterId; // null이면 모든 몬스터

    static QuestCondition create(
            Quest quest,
            QuestType type,
            int targetCount,
            Long targetMonsterId
    ) {
        QuestCondition questCondition = new QuestCondition();
        questCondition.quest = Objects.requireNonNull(quest);
        questCondition.type = Objects.requireNonNull(type);
        questCondition.targetCount = targetCount;
        questCondition.targetMonsterId = targetMonsterId;
        return questCondition;
    }
}
