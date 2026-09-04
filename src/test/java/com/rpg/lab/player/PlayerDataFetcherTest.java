package com.rpg.lab.player;

import com.jayway.jsonpath.TypeRef;
import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.rpg.lab.fixture.PlayerFixture;
import com.rpg.lab.inventory.InventoryRepository;
import com.rpg.lab.testsupport.IntegrationTest;
import graphql.ExecutionResult;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@RequiredArgsConstructor
class PlayerDataFetcherTest {

    private final DgsQueryExecutor dgsQueryExecutor;
    private final PlayerRepository playerRepository;
    private final InventoryRepository inventoryRepository;

    @Nested
    class Players {

        @Test
        @DisplayName("전체 플레이어 목록을 조회한다")
        void test1() {
            playerRepository.save(PlayerFixture.create());

            List<String> results = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                            """
                            { players { name } }
                            """,
                    "data.players[*].name",
                    new TypeRef<>() {}
            );

            assertThat(results).hasSize(1);
        }

        @Test
        @DisplayName("플레이어가 없으면 빈 리스트를 반환한다")
        void test2() {
            List<String> results = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                            """
                            { players { name }}
                            """,
                    "data.players[*].name",
                    new TypeRef<>() {}
            );

            assertThat(results).isEmpty();
        }
    }

    @Nested
    class GetPlayer {

        @Test
        @DisplayName("id 로 특정 플레이어를 조회하면 이름/레벨/스탯이 담겨있다")
        void test1() {
            Player player = playerRepository.save(PlayerFixture.create());

            String result = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                            """
                            { player(id: "%d") { name level stats { attack defense speed } } }
                            """.formatted(player.getId()),
                    "data.player.name",
                    String.class
            );

            assertThat(result).isEqualTo(player.getName());
        }

        @Test
        @DisplayName("존재하지 않는 id 로 조회하면 GraphQL 에러가 발생한다")
        void test2() {
            ExecutionResult result = dgsQueryExecutor.execute(
                            """
                            { player(id: "999") { name }}
                            """
            );

            assertThat(result.getErrors()).isNotEmpty();
        }

        @Test
        @DisplayName("플레이어 조회 시 gold 가 응답에 담긴다")
        void test3() {
            Player player = playerRepository.save(PlayerFixture.create());

            Integer gold = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    """
                            { player(id: %d) { gold } }
                            """.formatted(player.getId()),
                    "data.player.gold",
                    Integer.class
            );

            assertThat(gold).isEqualTo(0);
        }
    }

    @Nested
    class CreatePlayer {

        @Test
        @DisplayName("플레이어를 생성하면 초기 레벨/hp/exp 가 응답에 담긴다")
        void test1() {
            Integer level = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    """
                            mutation {
                                createPlayer(input: { name: "테스트유저" }) { level exp }
                            }
                            """,
                    "data.createPlayer.level",
                    Integer.class
            );

            assertThat(level).isEqualTo(1);
        }

        @Test
        @DisplayName("생성 시 Inventory 도 함께 만들어진다")
        void test2() {
            String id = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    """
                            mutation {
                                createPlayer(input: { name: "테스트유저" }) { id }
                            }
                            """,
                    "data.createPlayer.id",
                    String.class
            );

            assertThat(inventoryRepository.findByPlayerId(Long.parseLong(id))).isPresent();
        }
    }

    @Nested
    class Leaderboard {

        @Test
        @DisplayName("리더보드를 조회하면 레벨 순으로 정렬된 결과가 온다")
        void test1() {
            Player low = playerRepository.save(PlayerFixture.create());
            Player high = playerRepository.save(PlayerFixture.create());
            high.gainExp(1000);
            playerRepository.save(high);

            List<String> results = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    """
                    { leaderboard(limit: 10) { rank playerName level } }
                    """,
                    "data.leaderboard[*].playerName",
                    new TypeRef<>() {}
            );

            assertThat(results).containsExactly(high.getName(), low.getName());
        }

        @Test
        @DisplayName("limit 을 생략하면 기본값으로 조회된다")
        void test2() {
            for (int i = 0; i < 15; i++) {
                playerRepository.save(PlayerFixture.create());
            }

            List<Integer> results = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    """
                    { leaderboard { rank } }
                    """,
                    "data.leaderboard[*].rank",
                    new TypeRef<>() {}
            );

            assertThat(results).hasSize(10);
        }
    }
}