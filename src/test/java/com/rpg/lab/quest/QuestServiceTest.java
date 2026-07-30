package com.rpg.lab.quest;

import com.rpg.lab.exception.EntityNotFoundException;
import com.rpg.lab.fixture.MonsterFixture;
import com.rpg.lab.fixture.PlayerFixture;
import com.rpg.lab.fixture.QuestFixture;
import com.rpg.lab.monster.Monster;
import com.rpg.lab.monster.MonsterRepository;
import com.rpg.lab.player.Player;
import com.rpg.lab.player.PlayerRepository;
import com.rpg.lab.testsupport.IntegrationTest;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@IntegrationTest
@RequiredArgsConstructor
class QuestServiceTest {

    private final QuestService sut;

    private final QuestRepository questRepository;
    private final PlayerRepository playerRepository;
    private final PlayerQuestRepository playerQuestRepository;

    private final MonsterRepository monsterRepository;

    @Nested
    class GetQuest {

        private Quest quest;

        @BeforeEach
        void setUp() {
            Monster slime = monsterRepository.save(MonsterFixture.createSlime());
            quest = questRepository.save(QuestFixture.createKillMonster(slime.getId(), 1));

            Monster orc = monsterRepository.save(MonsterFixture.createOrc());
            questRepository.save(QuestFixture.createKillMonster(orc.getId(), 1));

            questRepository.save(QuestFixture.createLevelUp(1));
        }

        @Test
        @DisplayName("전체 퀘스트 목록을 조회한다")
        void test1() {
            List<Quest> quests = sut.getQuests();

            assertThat(quests)
                    .hasSize(3);
        }

        @Test
        @DisplayName("id로 특정 퀘스트를 조회한다")
        void test2() {
            Quest quest = sut.getQuest(this.quest.getId());

            assertThat(quest).isEqualTo(this.quest);
        }

        @Test
        @DisplayName("존재하지 않는 id로 조회하면 EntityNotFoundException이 발생한다")
        void test3() {
            Long invalidQuestId = 999L;

            assertThatThrownBy(() -> sut.getQuest(invalidQuestId))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    class AcceptQuest {

        private Player player;
        private Quest quest;

        @BeforeEach
        void setUp() {
            player = playerRepository.save(PlayerFixture.create());
            Monster monster = monsterRepository.save(MonsterFixture.createSlime());
            quest = questRepository.save(QuestFixture.createKillMonster(monster.getId(), 1));
        }

        @Test
        @DisplayName("퀘스트를 수락하면 PlayerQuest가 IN_PROGRESS 상태로 생성되고 progress도 함께 만들어진다")
        void test1() {
            PlayerQuestResponse playerQuest = sut.acceptQuest(player.getId(), quest.getId());

            SoftAssertions.assertSoftly(softly -> {
                PlayerQuest savedPlayerQuest = playerQuestRepository.findByPlayerIdAndQuestId(player.getId(), quest.getId())
                        .orElseThrow();

                softly.assertThat(playerQuest.status()).isEqualTo(QuestStatus.IN_PROGRESS);
                softly.assertThat(playerQuest.progress()).hasSize(1);
                softly.assertThat(playerQuest.questId()).isEqualTo(quest.getId());
                softly.assertThat(playerQuest.title()).isEqualTo(quest.getTitle());
                softly.assertThat(playerQuest.description()).isEqualTo(quest.getDescription());
                softly.assertThat(savedPlayerQuest.getStatus()).isEqualTo(QuestStatus.IN_PROGRESS);
            });
        }

        @Test
        @DisplayName("퀘스트 수락 시 조건 개수만큼 progress가 자동 생성된다 (PlayerQuest.start() cascade 검증)")
        void test2() {
            Monster monster = monsterRepository.save(MonsterFixture.createSlime());
            Quest compositeQuest = questRepository.save(QuestFixture.createComposite(monster.getId()));

            PlayerQuestResponse playerQuest = sut.acceptQuest(player.getId(), compositeQuest.getId());

            SoftAssertions.assertSoftly(softly -> {
                softly.assertThat(playerQuest.progress()).hasSize(2);
            });
        }

        @Test
        @DisplayName("존재하지 않는 player로 수락 시도하면 EntityNotFoundException")
        void test3() {
            Long invalidPlayerId = 999L;

            assertThatThrownBy(() -> sut.acceptQuest(invalidPlayerId, quest.getId()))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("존재하지 않는 quest로 수락 시도하면 EntityNotFoundException")
        void test4() {
             Long invalidQuestId = 999L;

            assertThatThrownBy(() -> sut.acceptQuest(player.getId(), invalidQuestId))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    class CompleteQuest {

        private Player player;
        private Quest quest;

        @BeforeEach
        void setUp() {
            player = playerRepository.save(PlayerFixture.create());
            Monster monster = monsterRepository.save(MonsterFixture.createSlime());
            quest = questRepository.save(QuestFixture.createKillMonster(monster.getId(), 1));
        }

        @Test
        @DisplayName("퀘스트를 완료 처리하면 상태가 COMPLETED로 바뀐다")
        void test1() {
            sut.acceptQuest(player.getId(), quest.getId());

            PlayerQuestResponse playerQuest = sut.completeQuest(player.getId(), quest.getId());

            assertThat(playerQuest.status()).isEqualTo(QuestStatus.COMPLETED);
        }

        @Test
        @DisplayName("존재하지 않는 PlayerQuest(수락 안 한 퀘스트)를 완료 시도하면 EntityNotFoundException")
        void test2() {
            assertThatThrownBy(() -> sut.completeQuest(player.getId(), quest.getId()))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    class GetMyQuests {

        private Player player;
        private Quest quest1;
        private Quest quest2;
        private Quest quest3;

        @BeforeEach
        void setUp() {
            player = playerRepository.save(PlayerFixture.create());

            Monster slime = monsterRepository.save(MonsterFixture.createSlime());
            quest1 = questRepository.save(QuestFixture.createKillMonster(slime.getId(), 1));

            Monster orc = monsterRepository.save(MonsterFixture.createOrc());
            quest2 = questRepository.save(QuestFixture.createKillMonster(orc.getId(), 1));

            quest3 = questRepository.save(QuestFixture.createLevelUp(1));
        }

        @Test
        @DisplayName("player가 수락한 퀘스트 목록을 조회한다")
        void test1() {
            sut.acceptQuest(player.getId(), quest1.getId());
            sut.acceptQuest(player.getId(), quest2.getId());

            List<PlayerQuestResponse> myQuests = sut.getMyQuests(player.getId());

            assertThat(myQuests)
                    .hasSize(2)
                    .extracting(PlayerQuestResponse::questId)
                    .contains(quest1.getId(), quest2.getId());
        }

        @Test
        @DisplayName("수락한 퀘스트가 없으면 빈 리스트를 반환한다")
        void test2() {
            List<PlayerQuestResponse> myQuests = sut.getMyQuests(player.getId());

            assertThat(myQuests).isEmpty();
        }
    }
}