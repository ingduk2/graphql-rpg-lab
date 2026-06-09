package com.rpg.lab.inventory;

import com.netflix.graphql.dgs.DgsDataLoader;
import org.dataloader.MappedBatchLoader;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@DgsDataLoader(name = "inventories")
public class InventoryDataLoader implements MappedBatchLoader<Long, InventoryResponse> {

    private final InventoryRepository inventoryRepository;

    public InventoryDataLoader(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public @NonNull CompletionStage<Map<Long, InventoryResponse>> load(@NonNull Set<Long> playerIds) {
        return CompletableFuture.supplyAsync(() -> {
            List<Inventory> inventories = inventoryRepository.findAllWithItemsByPlayerIdIn(
                    playerIds.stream().toList()
            );

            return inventories.stream()
                    .collect(Collectors.toMap(
                            i -> i.getPlayer().getId(),
                            InventoryResponse::from
                    ));
        });
    }
}
