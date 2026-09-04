package com.rpg.lab.inventory;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.InputArgument;
import com.netflix.graphql.dgs.context.DgsContext;
import com.rpg.lab.config.PlayerContext;
import lombok.RequiredArgsConstructor;

@DgsComponent
@RequiredArgsConstructor
public class InventoryDataFetcher {

    private final InventoryService inventoryService;

    @DgsMutation
    public InventoryResponse equipItem(
            @InputArgument Long itemId,
            DgsDataFetchingEnvironment dfe
    ) {
        PlayerContext context = DgsContext.getCustomContext(dfe);
        return inventoryService.equipItem(context.playerId(), itemId);
    }

    @DgsMutation
    public InventoryResponse unequipItem(
            @InputArgument Long itemId,
            DgsDataFetchingEnvironment dfe
    ) {
        PlayerContext context = DgsContext.getCustomContext(dfe);
        return inventoryService.unequipItem(context.playerId(), itemId);
    }

    @DgsMutation
    public InventoryResponse sellItem(
            @InputArgument Long itemId,
            DgsDataFetchingEnvironment dfe
    ) {
        PlayerContext context = DgsContext.getCustomContext(dfe);
        return inventoryService.sellItem(context.playerId(), itemId);
    }
}
