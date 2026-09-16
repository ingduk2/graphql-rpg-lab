package com.rpg.lab.battle;

import com.rpg.lab.item.Item;

import java.util.List;

public class BattleReward {
    private final int levelUps;
    private final Item droppedItem;
    private final List<String> completedQuests;
    private final List<String> unlockedAchievements;

    private BattleReward(
            int levelUps,
            Item droppedItem,
            List<String> completedQuests,
            List<String> unlockedAchievements
    ) {
        this.levelUps = levelUps;
        this.droppedItem = droppedItem;
        this.completedQuests = completedQuests;
        this.unlockedAchievements = unlockedAchievements;
    }

    public static BattleReward of(
            int levelUps,
            Item droppedItem,
            List<String> completedQuests,
            List<String> unlockedAchievements
    ) {
        return new BattleReward(levelUps, droppedItem, completedQuests, unlockedAchievements);
    }

    public static BattleReward empty() {
        return new BattleReward(0, null, List.of(), List.of());
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

    public List<String> unlockedAchievements() {
        return unlockedAchievements;
    }
}
