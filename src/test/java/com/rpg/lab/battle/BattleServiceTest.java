package com.rpg.lab.battle;

import com.rpg.lab.exception.EntityNotFoundException;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@IntegrationTest
@RequiredArgsConstructor
class BattleServiceTest {

    private final BattleService sut;
    private final PlayerRepository playerRepository;
    private final MonsterRepository monsterRepository;
    private final InventoryRepository inventoryRepository;

    private Player player;
    private Monster monster;

    @BeforeEach
    void setUp() {
        player = playerRepository.save(PlayerFixture.create());
        monster = monsterRepository.save(MonsterFixture.createSlime());
        inventoryRepository.save(Inventory.create(player));
    }

    @Nested
    class Attack {

        @Test
        @DisplayName("정상적으로 공격하면 BattleResult 가 반환된다")
        void test1() {
            BattleResult result = sut.attack(player.getId(), monster.getId(), monster.getHp());

            assertThat(result).isNotNull();
            assertThat(result.playerDamage()).isGreaterThanOrEqualTo(player.getAttack());
            assertThat(result.message()).isNotBlank();
        }

        @Test
        @DisplayName("존재하지 않는 player 로 공격하면 EntityNotFoundException 이 발생한다")
        void test2() {
            Long invalidPlayerId = 999L;

            assertThatThrownBy(() -> sut.attack(invalidPlayerId, monster.getId(), monster.getHp()))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("존재하지 않는 monster 로 공격하면 EntityNotFoundException 이 발생한다")
        void test3() {
            Long invalidMonsterId = 999L;

            assertThatThrownBy(() -> sut.attack(player.getId(), invalidMonsterId, 10))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("플레이어 레벨이 높으면 몬스터 반격 데미지가 더 크다 (스케일링 반영)")
        void test4() {
            Monster orc = monsterRepository.save(MonsterFixture.createOrc());
            int baseAttackPower = orc.getAttackPower();

            Player highLevelPlayer = playerRepository.save(PlayerFixture.create());
            highLevelPlayer.gainExp(100_000);
            playerRepository.save(highLevelPlayer);
            inventoryRepository.save(Inventory.create(highLevelPlayer));

            BattleResult result = sut.attack(highLevelPlayer.getId(), orc.getId(), Integer.MAX_VALUE);

            assertThat(result.monsterDamage()).isGreaterThan(baseAttackPower);
        }
    }
}