package com.rpg.lab.fixture;

import com.rpg.lab.quest.Quest;
import com.rpg.lab.quest.QuestType;

public class QuestFixture {

    public static Quest createKillMonster(Long monsterId, int targetCount) {
        return Quest.create(
                "슬라임 사냥",
                "슬라임 3마리 처치",
                QuestType.KILL_MONSTER,
                targetCount,
                monsterId
        );
    }

    public static Quest createLevelUp(int targetCount) {
        return Quest.create(
                "레벨업 달인",
                "레벨업 달성",
                QuestType.LEVEL_UP,
                targetCount,
                null
        );
    }

    public static Quest createComposite(Long monsterId) {
        return Quest.create("진정한 영웅", "몬스터 3마리 처치 + 레벨 2번 올리기", QuestType.KILL_MONSTER, 3, monsterId)
                .addCondition(QuestType.LEVEL_UP, 2, null);
    }

    public static Quest createWithPrerequisite(Quest prerequisite, Long monsterId, int targetCount) {
        return createKillMonster(monsterId, targetCount).withPrerequisite(prerequisite);
    }
}
