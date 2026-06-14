package com.rpg.lab.inventory;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.InputArgument;
import lombok.RequiredArgsConstructor;

@DgsComponent
@RequiredArgsConstructor
public class InventoryDataFetcher {

    private final InventoryService inventoryService;

    @DgsMutation
    public InventoryResponse equipItem(
            @InputArgument Long playerId,
            @InputArgument Long itemId
    ) {
        return inventoryService.equipItem(playerId, itemId);
    }
}
