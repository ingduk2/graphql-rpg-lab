package com.rpg.lab.battle;

import com.jayway.jsonpath.TypeRef;
import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.rpg.lab.config.PlayerContextBuilder;
import com.rpg.lab.fixture.MonsterFixture;
import com.rpg.lab.fixture.PlayerFixture;
import com.rpg.lab.inventory.Inventory;
import com.rpg.lab.inventory.InventoryRepository;
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

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@RequiredArgsConstructor
class BattleDataFetcherTest {

    private final DgsQueryExecutor dgsQueryExecutor;
    private final PlayerRepository playerRepository;
    private final MonsterRepository monsterRepository;
    private final InventoryRepository inventoryRepository;

    private Player player;
    private Monster monster;
    private HttpHeaders headers;

    @BeforeEach
    void setUp() {
        player = playerRepository.save(PlayerFixture.create());
        inventoryRepository.save(Inventory.create(player));
        monster = monsterRepository.save(MonsterFixture.createSlime());
        headers = new HttpHeaders();
        headers.add(PlayerContextBuilder.X_PLAYER_ID, String.valueOf(player.getId()));
    }

    @Nested
    class Attack {

        @Test
        @DisplayName("공격하면 BattleResult 가 응답된다")
        void test1() {
            String result = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    """
                            mutation {
                                attack(monsterId: "%d", currentMonsterHp: %d) { message }
                            }
                            """.formatted(monster.getId(), monster.getHp()),
                    "data.attack.message",
                    Collections.emptyMap(),
                    new TypeRef<>() {},
                    headers
            );

            assertThat(result).isNotBlank();
        }
    }

    @Nested
    class Flee {

        @Test
        @DisplayName("도망치면 데미지 없이 BattleResult 가 응답된다")
        void test1() {
            Integer playerDamage = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                    """
                            mutation {
                                flee(monsterId: "%d") { playerDamage monsterDefeated }
                            }
                            """.formatted(monster.getId()),
                    "data.flee.playerDamage",
                    Collections.emptyMap(),
                    new TypeRef<>() {},
                    headers
            );

            assertThat(playerDamage).isEqualTo(0);
        }
    }
}