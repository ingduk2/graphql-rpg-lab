package com.rpg.lab.inventory;

import com.rpg.lab.fixture.ItemFixture;
import com.rpg.lab.fixture.PlayerFixture;
import com.rpg.lab.item.Item;
import com.rpg.lab.item.ItemRepository;
import com.rpg.lab.item.ItemResponse;
import com.rpg.lab.player.Player;
import com.rpg.lab.player.PlayerRepository;
import com.rpg.lab.testsupport.IntegrationTest;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.transaction.TestTransaction;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.*;

@IntegrationTest
@RequiredArgsConstructor
class InventoryDataLoaderTest {

    private final InventoryRepository inventoryRepository;
    private final PlayerRepository playerRepository;
    private final ItemRepository itemRepository;

    @Nested
    class Load {

        @Test
        @DisplayName("여러 playerId 로 조회하면 각 player 의 inventory 를 매핑해서 반환한다")
        void test1() throws ExecutionException, InterruptedException {
            Player player1 = playerRepository.save(PlayerFixture.create());
            Player player2 = playerRepository.save(PlayerFixture.create());
            Inventory inventory1 = inventoryRepository.save(Inventory.create(player1));
            inventoryRepository.save(Inventory.create(player2));

            Item item = itemRepository.save(ItemFixture.createSwordItem());
            inventory1.addItem(item);
            inventoryRepository.save(inventory1);

            // 다른 스레드 (DataLoader 의 supplyAsync) 에서도 보이도록 실제 커밋
            TestTransaction.flagForCommit();
            TestTransaction.end();

            try {
                InventoryDataLoader sut = new InventoryDataLoader(inventoryRepository);

                Map<Long, InventoryResponse> result = sut.load(Set.of(player1.getId(), player2.getId()))
                        .toCompletableFuture()
                        .get();

                assertThat(result).containsKeys(player1.getId(), player2.getId());
                assertThat(result.get(player1.getId()).items())
                        .extracting(ItemResponse::id)
                        .contains(item.getId());
                assertThat(result.get(player2.getId()).items()).isEmpty();
            } finally {
                // 커밋된 데이터를 직접 정리 (더 이상 @Transactional 롤백 대상이 아니므로)
                TestTransaction.start();
                inventoryRepository.findByPlayerId(player1.getId()).ifPresent(inventoryRepository::delete);
                inventoryRepository.findByPlayerId(player2.getId()).ifPresent(inventoryRepository::delete);
                itemRepository.deleteById(item.getId());
                playerRepository.deleteById(player1.getId());
                playerRepository.deleteById(player2.getId());
                TestTransaction.flagForCommit();
                TestTransaction.end();
            }
        }

        @Test
        @DisplayName("Inventory 가 없는 playerId 는 결과 Map 에 포함되지 않는다")
        void test2() throws ExecutionException, InterruptedException {
            Player player = playerRepository.save(PlayerFixture.create());

            TestTransaction.flagForCommit();
            TestTransaction.end();

            try {
                InventoryDataLoader sut = new InventoryDataLoader(inventoryRepository);

                Map<Long, InventoryResponse> result = sut.load(Set.of(player.getId()))
                        .toCompletableFuture()
                        .get();

                assertThat(result).doesNotContainKey(player.getId());
            } finally {
                TestTransaction.start();
                playerRepository.deleteById(player.getId());
                TestTransaction.flagForCommit();
                TestTransaction.end();
            }
        }
    }
}