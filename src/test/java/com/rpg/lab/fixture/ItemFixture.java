package com.rpg.lab.fixture;

import com.rpg.lab.item.Item;
import com.rpg.lab.item.ItemSetType;
import com.rpg.lab.item.ItemType;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.atomic.AtomicLong;

public class ItemFixture {

    private static final AtomicLong ID_GENERATOR = new AtomicLong(1);

    // 저장 목적 - id 세팅 없음
    public static Item createSwordItem() {
        return Item.create("테스트검", ItemType.WEAPON, 5, 0);
    }

    public static Item createShieldItem() {
        return Item.create("테스트방패", ItemType.ARMOR, 0, 3);
    }

    // 순사 단위 테스트 목적 - id 를 직접 세팅
    public static Item createSwordItemWithId() {
        return withId("테스트검", ItemType.WEAPON, 5, 0);
    }

    public static Item createAxeItemWithId() {
        return withId("테스트도끼", ItemType.WEAPON, 8, 0);
    }

    public static Item createShieldItemWithId() {
        return withId("테스트방패", ItemType.ARMOR, 0, 3);
    }

    public static Item createArmorItemWithId() {
        return withId("테스트갑옷", ItemType.ARMOR, 0, 5);
    }

    public static Item createRingItemWithId() {
        return withId("테스트반지", ItemType.ACCESSORY, 2, 2);
    }

    public static Item createCustomItemWithId(String name, ItemType type, int attackBonus, int defenseBonus) {
        return withId(name, type, attackBonus, defenseBonus);
    }

    public static Item createGoblinSetWeaponWithId() {
        return withId("고블린 단검", ItemType.WEAPON, 3, 0, ItemSetType.GOBLIN_SET);
    }

    public static Item createGoblinSetArmorWithId() {
        return withId("고블린 갑옷", ItemType.ARMOR, 0, 3, ItemSetType.GOBLIN_SET);
    }

    private static Item withId(
            String name,
            ItemType type,
            int attackBonus,
            int defenceBonus
    ) {
        return withId(name, type, attackBonus, defenceBonus, ItemSetType.NONE);
    }

    private static Item withId(
            String name,
            ItemType type,
            int attackBonus,
            int defenceBonus,
            ItemSetType setType
    ) {
        Item item = Item.create(name, type, attackBonus, defenceBonus, setType);
        ReflectionTestUtils.setField(item, "id", ID_GENERATOR.getAndIncrement());
        return item;
    }
}
