package com.rpg.lab.battle;

import com.rpg.lab.item.Item;

import java.util.List;

public class BattleReward {
    private final int levelUps;
    private final Item droppedItem;
    private final List<String> completedQuests;

    private BattleReward(
            int levelUps,
            Item droppedItem,
            List<String> completedQuests
    ) {
        this.levelUps = levelUps;
        this.droppedItem = droppedItem;
        this.completedQuests = completedQuests;
    }

    public static BattleReward of(
            int levelUps,
            Item droppedItem,
            List<String> completedQuests
    ) {
        return new BattleReward(levelUps, droppedItem, completedQuests);
    }

    public static BattleReward empty() {
        return new BattleReward(0, null, List.of());
    }

    public int levelUps() {
        return levelUps;
    }

    public Item droppedItem() {
        return droppedItem;
    }

    public List<String> completedQuests() {
        return completedQuests;
    }
}
