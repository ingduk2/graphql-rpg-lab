package com.rpg.lab.quest;

import com.rpg.lab.fixture.MonsterFixture;
import com.rpg.lab.fixture.PlayerFixture;
import com.rpg.lab.fixture.QuestFixture;
import com.rpg.lab.monster.Monster;
import com.rpg.lab.monster.MonsterRepository;
import com.rpg.lab.player.Player;
import com.rpg.lab.player.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@RequiredArgsConstructor
class QuestProgressUpdaterTest {
    private final QuestProgressUpdater questProgressUpdater;
    private final PlayerQuestRepository playerQuestRepository;
    private final PlayerQuestProgressRepository playerQuestProgressRepository;
    private final PlayerRepository playerRepository;
    private final MonsterRepository monsterRepository;
    private final QuestRepository questRepository;
    private final QuestConditionRepository questConditionRepository;

    private Player player;
    private Monster monster;

    @BeforeEach
    void setUp() {
        player = playerRepository.save(PlayerFixture.create());
        monster = monsterRepository.save(MonsterFixture.createSlime());
    }

    @Nested
    @DisplayName("KILL_MONSTER")
    class KillMonster {
        private PlayerQuest playerQuest;
        private QuestCondition questCondition;

        @BeforeEach
        void setUp() {
            Quest quest = questRepository.save(QuestFixture.createKillMonster());
            questCondition = questConditionRepository.save(
                    QuestFixture.killMonsterCondition(quest, monster.getId(), 3));
            playerQuest = playerQuestRepository.save(PlayerQuest.create(player, quest));
            playerQuestProgressRepository.save(PlayerQuestProgress.create(playerQuest, questCondition));
        }

        @Test
        @DisplayName("몬스터 처치 시 진행도가 올라간다")
        void test1() {
            questProgressUpdater.updateOnBattleVictory(player.getId(), monster.getId(), 0);

            PlayerQuestProgress progress = findProgress(playerQuest.getId(), questCondition.getId());

            assertThat(progress.getCurrentCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("목표 달성 시 퀘스트가 완료된다")
        void test2() {
            int killGoal = 3;
            questProgressUpdater.updateOnBattleVictory(player.getId(), monster.getId(), 0);
            questProgressUpdater.updateOnBattleVictory(player.getId(), monster.getId(), 0);

            List<PlayerQuest> completed = questProgressUpdater.updateOnBattleVictory(player.getId(), monster.getId(), 0);

            assertThat(completed).hasSize(1);
            assertThat(completed.get(0).getStatus()).isEqualTo(QuestStatus.COMPLETED);

            PlayerQuestProgress progress = findProgress(playerQuest.getId(), questCondition.getId());
            assertThat(progress.getCurrentCount()).isEqualTo(killGoal);
        }

        @Test
        @DisplayName("다른 몬스터 처치 시 진행도가 올라가지 않는다")
        void test3() {
            Monster otherMonster = monsterRepository.save(MonsterFixture.createOrc());

            List<PlayerQuest> completed = questProgressUpdater.updateOnBattleVictory(player.getId(), otherMonster.getId(), 0);

            PlayerQuestProgress progress = findProgress(playerQuest.getId(), questCondition.getId());
            assertThat(progress.getCurrentCount()).isEqualTo(0);
            assertThat(completed).isEmpty();
        }
    }

    @Nested
    @DisplayName("LEVEL_UP")
    class LevelUp {

        private PlayerQuest playerQuest;
        private QuestCondition questCondition;
        private static final int NO_LEVEL_UP = 0;

        @BeforeEach
        void setUp() {
            Quest quest = questRepository.save(QuestFixture.createLevelUp());
            questCondition = questConditionRepository.save(
                    QuestFixture.levelUpCondition(quest, 3));
            playerQuest = playerQuestRepository.save(PlayerQuest.create(player, quest));
            playerQuestProgressRepository.save(PlayerQuestProgress.create(playerQuest, questCondition));
        }

        @Test
        @DisplayName("레벨업 시 진행도가 올라간다")
        void test1() {
            int newLevel = 1;
            questProgressUpdater.updateOnBattleVictory(player.getId(), monster.getId(), newLevel);

            PlayerQuestProgress progress = findProgress(playerQuest.getId(), questCondition.getId());
            assertThat(progress.getCurrentCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("레벨업 없으면 진행도가 올라가지 않는다")
        void test2() {
            questProgressUpdater.updateOnBattleVictory(player.getId(), monster.getId(), NO_LEVEL_UP);

            PlayerQuestProgress progress = findProgress(playerQuest.getId(), questCondition.getId());
            assertThat(progress.getCurrentCount()).isEqualTo(0);
        }
    }

    private PlayerQuestProgress findProgress(Long playerQuestId, Long questConditionId) {
        return playerQuestProgressRepository.findProgressByPlayerQuestId(playerQuestId).stream()
                .filter(p -> p.getQuestCondition().getId().equals(questConditionId))
                .findFirst()
                .orElseThrow();
    }
}