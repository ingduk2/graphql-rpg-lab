package com.rpg.lab.inventory;

import com.rpg.lab.exception.InvalidItemOperationException;
import com.rpg.lab.fixture.ItemFixture;
import com.rpg.lab.fixture.PlayerFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InventoryItemTest {

    @Test
    @DisplayName("장비는 수량을 늘릴 수 없다")
    void test1() {
        Inventory inventory = Inventory.create(PlayerFixture.create());
        InventoryItem sword = InventoryItem.create(inventory, ItemFixture.createSwordItemWithId());

        assertThatThrownBy(sword::increaseQuantity)
                .isInstanceOf(InvalidItemOperationException.class);
    }
}