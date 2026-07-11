package com.rpg.lab.quest;

import com.rpg.lab.inventory.InventoryRepository;
import com.rpg.lab.player.Player;
import com.rpg.lab.player.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class QuestRewardProcessor {

    private final QuestRewardRepository questRewardRepository;
    private final PlayerRepository playerRepository;
    private final InventoryRepository inventoryRepository;

    @Transactional
    public void process(Player player, Long questId) {
        List<QuestReward> questRewards = questRewardRepository.findAllWithItemByQuestId(questId);

        questRewards.forEach(reward -> {
            player.gainExp(reward.getRewardExp());

            if (reward.getItem() != null) {
                inventoryRepository.findByPlayerId(player.getId())
                        .ifPresent(inventory -> {
                            inventory.addItemIfNotOwned(reward.getItem());
                            inventoryRepository.save(inventory);
                        });
            }
        });

        playerRepository.save(player);
    }
}
