package com.rpg.lab.achievement;

import com.rpg.lab.fixture.PlayerFixture;
import com.rpg.lab.player.Player;
import com.rpg.lab.player.PlayerRepository;
import com.rpg.lab.testsupport.IntegrationTest;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@RequiredArgsConstructor
class AchievementServiceTest {

    private final AchievementService sut;
    private final AchievementRepository achievementRepository;
    private final PlayerAchievementRepository playerAchievementRepository;
    private final PlayerRepository playerRepository;

    private Player player;

    @BeforeEach
    void setUp() {
        player = playerRepository.save(PlayerFixture.create());
    }

    @Nested
    class GetMyAchievements {

        @Test
        @DisplayName("업적 목록과 각각의 달성 여부/진행도가 담긴다")
        void test1() {
            Achievement achievement = achievementRepository.save(
                    Achievement.create("첫 걸음", "레벨 1 달성", AchievementType.LEVEL, 1)
            );

            List<AchievementResponse> results = sut.getMyAchievements(player.getId());

            AchievementResponse result = results.stream()
                    .filter(it -> it.id().equals(achievement.getId()))
                    .findFirst()
                    .orElseThrow();

            assertThat(result.unlocked()).isFalse();
            assertThat(result.conditions())
                    .extracting(AchievementConditionResponse::currentCount)
                    .containsExactly(1);
        }

        @Test
        @DisplayName("달성한 업적은 unlocked 가 true 로 담긴다")
        void test2() {
            Achievement achievement = achievementRepository.save(
                    Achievement.create("첫 걸음", "레벨 1 달성", AchievementType.LEVEL, 1)
            );
            sut.checkAndUnlockAchievements(player.getId());

            List<AchievementResponse> results = sut.getMyAchievements(player.getId());

            assertThat(results)
                    .filteredOn(it -> it.id().equals(achievement.getId()))
                    .extracting(AchievementResponse::unlocked)
                    .containsExactly(true);
        }
    }

    @Nested
    class CheckAndUnlockAchievements {

        @Test
        @DisplayName("레벨업 조건을 충족하면 업적이 달성 처리된")
        void test1() {
            Achievement achievement = achievementRepository.save(
                    Achievement.create("첫 걸음", "레벨 1 달성", AchievementType.LEVEL, 1)
            );

            List<Achievement> results = sut.checkAndUnlockAchievements(player.getId());

            assertThat(results).contains(achievement);
            assertThat(playerAchievementRepository.findByPlayerId(player.getId())).hasSize(1);
        }

        @Test
        @DisplayName("킬 카운트 조건을 충족하면 업적이 달성 처리된")
        void test2() {
            int killCount = 100;
            Achievement achievement = achievementRepository.save(
                    Achievement.create("사냥꾼", "몬스터 100마리 처치", AchievementType.KILL_COUNT, killCount)
            );

            for (int i = 0; i < killCount; i++) {
                player.increaseKillCount();
            }

            List<Achievement> results = sut.checkAndUnlockAchievements(player.getId());

            assertThat(results).contains(achievement);
            assertThat(playerAchievementRepository.findByPlayerId(player.getId())).hasSize(1);
        }

        @Test
        @DisplayName("조건을 충족하지 않으면 달성되지 않는다")
        void test3() {
            achievementRepository.save(
                    Achievement.create("사냥꾼", "몬스터 100마리 처치", AchievementType.KILL_COUNT, 100)
            );

            List<Achievement> results = sut.checkAndUnlockAchievements(player.getId());

            assertThat(results).isEmpty();
        }

        @Test
        @DisplayName("이미 달성한 업적은 다시 기록되지 않는다")
        void test4() {
            achievementRepository.save(
                    Achievement.create("첫 걸음", "레벨 1 달성", AchievementType.LEVEL, 1)
            );

            sut.checkAndUnlockAchievements(player.getId());

            List<Achievement> result = sut.checkAndUnlockAchievements(player.getId());

            assertThat(result).isEmpty();
            assertThat(playerAchievementRepository.findByPlayerId(player.getId())).hasSize(1);
        }
    }
}