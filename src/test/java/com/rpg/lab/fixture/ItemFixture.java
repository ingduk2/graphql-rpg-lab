package com.rpg.lab.fixture;

import com.rpg.lab.item.Item;
import com.rpg.lab.item.ItemType;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.atomic.AtomicLong;

public class ItemFixture {

    private static final AtomicLong ID_GENERATOR = new AtomicLong(1);

    public static Item createSwordItem() {
        return create("테스트검", ItemType.WEAPON, 5, 0);
    }

    public static Item createAxeItem() {
        return create("테스트도끼", ItemType.WEAPON, 8, 0);
    }

    public static Item createShieldItem() {
        return create("테스트방패", ItemType.ARMOR, 0, 3);
    }

    public static Item createArmorItem() {
        return create("테스트갑옷", ItemType.ARMOR, 0, 5);
    }

    private static Item create(
            String name,
            ItemType type,
            int attackBonus,
            int defenceBonus
    ) {
        Item item = Item.create(name, type, attackBonus, defenceBonus);
        ReflectionTestUtils.setField(item, "id", ID_GENERATOR.getAndIncrement());
        return item;
    }
}
