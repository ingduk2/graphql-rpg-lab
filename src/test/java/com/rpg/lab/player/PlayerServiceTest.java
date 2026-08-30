package com.rpg.lab.player;

import com.rpg.lab.exception.EntityNotFoundException;
import com.rpg.lab.fixture.PlayerFixture;
import com.rpg.lab.inventory.InventoryRepository;
import com.rpg.lab.testsupport.IntegrationTest;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.rpg.lab.player.LeaderboardSortBy.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@IntegrationTest
@RequiredArgsConstructor
class PlayerServiceTest {

    private final PlayerService sut;
    private final PlayerRepository playerRepository;
    private final InventoryRepository inventoryRepository;

    @Nested
    class GetPlayers {

        @Test
        @DisplayName("전체 플레이어 목록을 조회한다")
        void test1() {
            playerRepository.save(PlayerFixture.create());

            List<PlayerResponse> results = sut.getPlayers();

            assertThat(results).hasSize(1);
        }

        @Test
        @DisplayName("플레이어가 없으면 빈 리스트를 반환한다")
        void test2() {
            List<PlayerResponse> results = sut.getPlayers();

            assertThat(results).isEmpty();
        }
    }

    @Nested
    class GetPlayer {

        @Test
        @DisplayName("id 로 특정 플레이어를 조회한다")
        void test1() {
            Player player = playerRepository.save(PlayerFixture.create());

            PlayerResponse result = sut.getPlayer(player.getId());

            assertThat(result.name()).isEqualTo(player.getName());
        }

        @Test
        @DisplayName("존재하지 않는 id 로 조회하면 EntityNotFoundException")
        void test2() {
            Long invalidId = 999L;

            assertThatThrownBy(() -> sut.getPlayer(invalidId))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    class CreatePlayer {

        @Test
        @DisplayName("플레이어를 생성하면 초기 스탯으로 응답이 만들어진다")
        void test1() {
            PlayerResponse result = sut.createPlayer("테스트유저");

            SoftAssertions.assertSoftly(softly -> {
                softly.assertThat(result.id()).isNotNull();
                softly.assertThat(result.name()).isEqualTo("테스트유저");
                softly.assertThat(result.level()).isEqualTo(PlayerDefaults.LEVEL);
                softly.assertThat(result.hp()).isEqualTo(PlayerDefaults.HP);
                softly.assertThat(result.maxHp()).isEqualTo(PlayerDefaults.MAX_HP);
                softly.assertThat(result.exp()).isEqualTo(PlayerDefaults.EXP);
                softly.assertThat(result.stats().attack()).isEqualTo(PlayerDefaults.ATTACK);
                softly.assertThat(result.stats().defense()).isEqualTo(PlayerDefaults.DEFENSE);
                softly.assertThat(result.stats().speed()).isEqualTo(PlayerDefaults.SPEED);
            });
        }

        @Test
        @DisplayName("생성 시 Inventory 도 함께 만들어진다")
        void test2() {
            PlayerResponse result = sut.createPlayer("테스트유저");

            assertThat(inventoryRepository.findByPlayerId(result.id())).isPresent();
        }
    }

    @Nested
    class GetLeaderboard {

        @Test
        @DisplayName("레벨 내림차순으로 정렬된 순위를 반환한다")
        void test1() {
            Player lowPlayer = playerRepository.save(PlayerFixture.create());
            Player midPlayer = playerRepository.save(PlayerFixture.create());
            Player highPlayer = playerRepository.save(PlayerFixture.create());
            midPlayer.gainExp(100);
            highPlayer.gainExp(1000);

            playerRepository.save(midPlayer);
            playerRepository.save(highPlayer);

            List<LeaderboardEntry> results = sut.getLeaderboard(10, LEVEL);

            assertThat(results)
                    .extracting(LeaderboardEntry::playerId)
                    .containsExactly(highPlayer.getId(), midPlayer.getId(), lowPlayer.getId());
        }

        @Test
        @DisplayName("순위가 1부터 순서대로 매겨진다")
        void test2() {
            playerRepository.save(PlayerFixture.create());
            playerRepository.save(PlayerFixture.create());

            List<LeaderboardEntry> results = sut.getLeaderboard(10, LEVEL);

            assertThat(results)
                    .extracting(LeaderboardEntry::rank)
                    .containsExactly(1, 2);
        }

        @Test
        @DisplayName("limit 보다 플레이어가 많으면 limit 만큼만 반환한다")
        void test3() {
            playerRepository.save(PlayerFixture.create());
            playerRepository.save(PlayerFixture.create());
            playerRepository.save(PlayerFixture.create());

            int limit = 2;

            List<LeaderboardEntry> results = sut.getLeaderboard(limit, LEVEL);

            assertThat(results).hasSize(limit);
        }

        @Test
        @DisplayName("플레이어가 없으면 빈 리스트를 반환한다")
        void test4() {
            List<LeaderboardEntry> results = sut.getLeaderboard(10, LEVEL);

            assertThat(results).isEmpty();
        }
    }
}