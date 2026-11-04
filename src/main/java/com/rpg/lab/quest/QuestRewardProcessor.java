package com.rpg.lab.quest;

import com.rpg.lab.inventory.InventoryRepository;
import com.rpg.lab.item.Item;
import com.rpg.lab.player.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class QuestRewardProcessor {

    private final QuestRewardRepository questRewardRepository;
    private final InventoryRepository inventoryRepository;

    @Transactional
    public void process(Player player, Long inventoryId, Long questId) {
        List<QuestReward> questRewards = questRewardRepository.findAllWithItemByQuestId(questId);

        questRewards.forEach(reward -> player.gainExp(reward.getRewardExp()));

        List<Item> rewardItems = questRewards.stream()
                .map(QuestReward::getItem)
                .filter(Objects::nonNull)
                .toList();

        if (rewardItems.isEmpty()) {
            return;
        }

        inventoryRepository.findById(inventoryId)
                .ifPresent(inventory -> {
                    rewardItems.forEach(inventory::addItemIfNotOwned);
                    inventoryRepository.save(inventory);
                });
    }
}
