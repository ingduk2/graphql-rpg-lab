package com.rpg.lab.monster;

import com.jayway.jsonpath.TypeRef;
import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.rpg.lab.config.PlayerContextBuilder;
import com.rpg.lab.fixture.MonsterFixture;
import com.rpg.lab.fixture.PlayerFixture;
import com.rpg.lab.player.Player;
import com.rpg.lab.player.PlayerRepository;
import com.rpg.lab.testsupport.IntegrationTest;
import graphql.ExecutionResult;
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
class MonsterDataFetcherTest {

    private final DgsQueryExecutor dgsQueryExecutor;
    private final MonsterRepository monsterRepository;
    private final PlayerRepository playerRepository;

    private Player player;
    private HttpHeaders headers;

    @BeforeEach
    void setUp() {
        player = playerRepository.save(PlayerFixture.create());
        headers = new HttpHeaders();
        headers.add(PlayerContextBuilder.X_PLAYER_ID, String.valueOf(player.getId()));
    }

    @Nested
    class Monsters {

        @Test
        @DisplayName("전체 몬스터 목록을 조회한다")
        void test1() {
            monsterRepository.save(MonsterFixture.createSlime());

            List<String> results = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    """
                            { monsters { name }}
                            """,
                    "data.monsters[*].name",
                    new TypeRef<>() {}
            );

            assertThat(results).hasSize(1);
        }

        @Test
        @DisplayName("몬스터가 없으면 빈 리스트를 반환한다")
        void test2() {
            List<String> results = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    """
                            { monsters { name }}
                            """,
                    "data.monsters[*].name",
                    new TypeRef<>() {}
            );

            assertThat(results).isEmpty();
        }
    }

    @Nested
    class GetMonster {

        @Test
        @DisplayName("id 로 특정 몬스터를 조회하면 스탯이 담겨있다")
        void test1() {
            Monster monster = monsterRepository.save(MonsterFixture.createOrc());

            String result = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    """
                            { monster(id: "%d") { name hp attackPower } }
                            """.formatted(monster.getId()),
                    "data.monster.name",
                    Collections.emptyMap(),
                    String.class,
                    headers
            );

            assertThat(result).isEqualTo(monster.getName());
        }

        @Test
        @DisplayName("존재하지 않는 id 로 조회하면 GraphQL 에러가 발생한다")
        void test2() {
            ExecutionResult result = dgsQueryExecutor.execute(
                    """
                            { monster(id: "999") { name } }
                            """
            );

            assertThat(result.getErrors()).isNotEmpty();
        }
    }
}