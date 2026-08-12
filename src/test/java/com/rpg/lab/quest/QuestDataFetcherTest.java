package com.rpg.lab.quest;

import com.jayway.jsonpath.TypeRef;
import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.rpg.lab.config.PlayerContextBuilder;
import com.rpg.lab.fixture.MonsterFixture;
import com.rpg.lab.fixture.PlayerFixture;
import com.rpg.lab.fixture.QuestFixture;
import com.rpg.lab.monster.Monster;
import com.rpg.lab.monster.MonsterRepository;
import com.rpg.lab.player.Player;
import com.rpg.lab.player.PlayerRepository;
import com.rpg.lab.testsupport.IntegrationTest;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@RequiredArgsConstructor
class QuestDataFetcherTest {

    private final DgsQueryExecutor dgsQueryExecutor;
    private final QuestRepository questRepository;
    private final PlayerRepository playerRepository;
    private final MonsterRepository monsterRepository;

    private Player player;
    private HttpHeaders headers;

    @BeforeEach
    void setUp() {
        player = playerRepository.save(PlayerFixture.create());
        headers = new HttpHeaders();
        headers.add(PlayerContextBuilder.X_PLAYER_ID, String.valueOf(player.getId()));
    }

    @Nested
    class Quests {

        @Test
        @DisplayName("전체 퀘스트 목록을 조회한다 (헤더 불필요)")
        void test1() {
            Monster monster = monsterRepository.save(MonsterFixture.createSlime());
            questRepository.save(QuestFixture.createKillMonster(monster.getId(), 1));

            List<String> results = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    """
                            { quests { title } }
                            """,
                    "data.quests[*].title",
                    new TypeRef<>() {}
            );

            assertThat(results).hasSize(1);
        }

        @Test
        @DisplayName("선행 퀘스트가 있으면 응답에 prerequisiteQuest 가 포함된다")
        void test2() {
            Monster monster = monsterRepository.save(MonsterFixture.createSlime());
            Quest prerequisite = questRepository.save(QuestFixture.createKillMonster(monster.getId(), 1));
            questRepository.save(QuestFixture.createWithPrerequisite(prerequisite, monster.getId(), 1));

            List<String> prerequisiteTitles = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    """
                            { quests { title prerequisiteQuest { title } } }
                            """,
                    "data.quests[*].prerequisiteQuest.title",
                    new TypeRef<>() {}
            );

            assertThat(prerequisiteTitles).contains(prerequisite.getTitle());
        }
    }

    @Nested
    class AcceptQuest {

        @Test
        @DisplayName("퀘스트를 수락하면 IN_PROGRESS 상태로 응답된다")
        void test1() {
            Monster monster = monsterRepository.save(MonsterFixture.createSlime());
            Quest quest = questRepository.save(QuestFixture.createKillMonster(monster.getId(), 1));

            String status = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                            """
                            mutation {
                                acceptQuest(questId: "%d") { status }
                            }
                            """.formatted(quest.getId()),
                    "data.acceptQuest.status",
                    Collections.emptyMap(),
                    new TypeRef<>() {},
                    headers
            );

            assertThat(status).isEqualTo("IN_PROGRESS");
        }
    }

    @Nested
    class CompleteQuest {

        @Test
        @DisplayName("퀘스트를 완료 처리하면 COMPLETED 상태로 응답된다")
        void test1() {
            Monster monster = monsterRepository.save(MonsterFixture.createSlime());
            Quest quest = questRepository.save(QuestFixture.createKillMonster(monster.getId(), 1));

            dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    """
                            mutation {
                                acceptQuest(questId: "%d") { status }
                            }
                            """.formatted(quest.getId()),
                    "data.acceptQuest.status",
                    Collections.emptyMap(),
                    new TypeRef<String>() {},
                    headers
            );

            String status = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    """
                            mutation {
                                completeQuest(questId: "%d") { status }
                            }
                            """.formatted(quest.getId()),
                    "data.completeQuest.status",
                    Collections.emptyMap(),
                    new TypeRef<String>() {},
                    headers
            );

            assertThat(status).isEqualTo("COMPLETED");
        }
    }

    @Nested
    class MyQuests {

        @Test
        @DisplayName("내가 수락한 퀘스트 목록을 조회한다")
        void test1() {
            Monster monster = monsterRepository.save(MonsterFixture.createSlime());
            Quest quest = questRepository.save(QuestFixture.createKillMonster(monster.getId(), 1));

            dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    """
                            mutation {
                                acceptQuest(questId: "%d") { status }
                            }
                            """.formatted(quest.getId()),
                    "data.acceptQuest.status",
                    Collections.emptyMap(),
                    new TypeRef<>() {},
                    headers
            );

            List<String> titles = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    """
                            { myQuests { title } }
                            """,
                    "data.myQuests[*].title",
                    Collections.emptyMap(),
                    new TypeRef<>() {},
                    headers
            );

            assertThat(titles).hasSize(1);
        }

        @Test
        @DisplayName("수락한 퀘스트가 없으면 빈 리스트를 반환한다")
        void test2() {
            List<String> results = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    """
                            { myQuests { title } }
                            """,
                    "data.myQuests[*].title",
                    Collections.emptyMap(),
                    new TypeRef<>() {},
                    headers
            );

            assertThat(results).isEmpty();
        }
    }
}