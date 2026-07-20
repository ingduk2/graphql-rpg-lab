package com.rpg.lab.fixture;

import com.rpg.lab.quest.Quest;
import com.rpg.lab.quest.QuestCondition;
import com.rpg.lab.quest.QuestType;

public class QuestFixture {

    public static Quest createKillMonster() {
        return Quest.create("슬라임 사냥", "슬라임 3마리 처치");
    }

    public static Quest createLevelUp() {
        return Quest.create("레벨업 달인", "레벨 3번 올리기");
    }

    public static QuestCondition killMonsterCondition(
            Quest quest,
            Long monsterId,
            int targetCount
    ) {
        return QuestCondition.create(quest, QuestType.KILL_MONSTER, targetCount, monsterId);
    }

    public static QuestCondition levelUpCondition(
            Quest quest,
            int targetCount
    ) {
        return QuestCondition.create(quest, QuestType.LEVEL_UP, targetCount, null);
    }
}
