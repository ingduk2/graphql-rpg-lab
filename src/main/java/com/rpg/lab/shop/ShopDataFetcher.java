package com.rpg.lab.shop;

import com.netflix.graphql.dgs.*;
import com.netflix.graphql.dgs.context.DgsContext;
import com.rpg.lab.config.PlayerContext;
import com.rpg.lab.inventory.InventoryResponse;
import lombok.RequiredArgsConstructor;

import java.util.List;

@DgsComponent
@RequiredArgsConstructor
public class ShopDataFetcher {

    private final ShopService shopService;

    @DgsQuery
    public List<ShopItemResponse> shopItems() {
        return shopService.getShopItems();
    }

    @DgsMutation
    public InventoryResponse buyItem(
            @InputArgument Long shopItemId,
            DgsDataFetchingEnvironment dfe
    ) {
        PlayerContext context = DgsContext.getCustomContext(dfe);
        return shopService.buyItem(context.playerId(), shopItemId);
    }
}
