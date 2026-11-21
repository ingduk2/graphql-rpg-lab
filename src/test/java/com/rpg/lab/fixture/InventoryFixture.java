package com.rpg.lab.fixture;

import com.rpg.lab.inventory.Inventory;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.atomic.AtomicLong;

public class InventoryFixture {

    private static final AtomicLong ID_GENERATOR = new AtomicLong(1);

    /**
     * 영속화(flush)를 흉내 낸다: id가 없는 인벤토리 행에 id를 부여한다.
     */
    public static void assignIds(Inventory inventory) {
        inventory.getInventoryItems().stream()
                .filter(it -> it.getId() == null)
                .forEach(it -> ReflectionTestUtils.setField(it, "id", ID_GENERATOR.getAndIncrement()));
    }
}
