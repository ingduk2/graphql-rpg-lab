package com.rpg.lab.quest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class QuestTest {

    @Nested
    class Create {

        @Test
        @DisplayName("퀘스트 생성 시 최소 1개 조건이 함께 만들어진다")
        void test1() {
            Quest quest = Quest.create("슬라임 사냥", "설명", QuestType.KILL_MONSTER, 3, 1L);

            assertThat(quest.getConditions()).hasSize(1);
            assertThat(quest.getRewards()).isEmpty();
        }
    }

    @Nested
    class AddCondition {

        @Test
        @DisplayName("조건을 추가로 붙이면 conditions 에 반영된다")
        void test1() {
            Quest quest = Quest.create("복합 퀘스트", "설명", QuestType.KILL_MONSTER, 3, 1L);

            quest.addCondition(QuestType.LEVEL_UP, 2, null);

            assertThat(quest.getConditions()).hasSize(2);
        }
    }

    @Nested
    class WithPrerequisite {

        @Test
        @DisplayName("선행 퀘스트를 지정하면 hasPrerequisite 가 true 를 반환한다")
        void test1() {
            Quest prerequisite = Quest.create("1단계", "설명", QuestType.KILL_MONSTER, 1, 1L);
            Quest quest = Quest.create("2단계", "설명", QuestType.KILL_MONSTER, 1, 1L)
                    .withPrerequisite(prerequisite);

            assertThat(quest.hasPrerequisite()).isTrue();
            assertThat(quest.getPrerequisiteQuest()).isEqualTo(prerequisite);
        }

        @Test
        @DisplayName("선행 퀘스트를 지정하지 않으면 hasPrerequisite 가 false 를 반환한다")
        void test2() {
            Quest quest = Quest.create("1단계", "설명", QuestType.KILL_MONSTER, 1, 1L);

            assertThat(quest.hasPrerequisite()).isFalse();
        }
    }
}