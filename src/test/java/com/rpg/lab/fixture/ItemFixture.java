package com.rpg.lab.fixture;

import com.rpg.lab.item.Item;
import com.rpg.lab.item.ItemType;

public class ItemFixture {

    public static Item createSwordItem() {
        return Item.create("테스트검", ItemType.WEAPON, 5, 0);
    }

    public static Item createShieldItem() {
        return Item.create("테스트방패", ItemType.ARMOR, 0, 3);
    }
}
