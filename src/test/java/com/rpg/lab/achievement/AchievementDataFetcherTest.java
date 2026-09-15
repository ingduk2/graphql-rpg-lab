package com.rpg.lab.achievement;

import com.jayway.jsonpath.TypeRef;
import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.rpg.lab.config.PlayerContextBuilder;
import com.rpg.lab.fixture.PlayerFixture;
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
class AchievementDataFetcherTest {

    private final DgsQueryExecutor dgsQueryExecutor;
    private final AchievementRepository achievementRepository;
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
    class MyAchievements {

        @Test
        @DisplayName("업적 목록과 진행도가 응답에 담긴다")
        void test1() {
            Achievement achievement = achievementRepository.save(
                    Achievement.create("첫 걸음", "레벨 1 달성", AchievementType.LEVEL, 1)
            );

            List<String> results = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    """
                            { myAchievements { title unlocked currentCount } }
                            """,
                    "data.myAchievements[*].title",
                    Collections.emptyMap(),
                    new TypeRef<>() {
                    },
                    headers
            );

            assertThat(results).contains(achievement.getTitle());
        }
    }
}