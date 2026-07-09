package com.rpg.lab.quest;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    public static QuestCondition create(
            Quest quest,
            QuestType type,
            int targetCount
    ) {
        QuestCondition questCondition = new QuestCondition();
        questCondition.quest = quest;
        questCondition.type = type;
        questCondition.targetCount = targetCount;
        return questCondition;
    }
}
