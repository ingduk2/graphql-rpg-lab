package com.rpg.lab.player;

import com.netflix.graphql.dgs.*;
import com.rpg.lab.inventory.InventoryResponse;
import lombok.RequiredArgsConstructor;
import org.dataloader.DataLoader;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@DgsComponent
@RequiredArgsConstructor
public class PlayerDataFetcher {

    private final PlayerService playerService;

    @DgsQuery
    public List<PlayerResponse> players() {
        return playerService.getPlayers();
    }

    @DgsData(parentType = "Player", field = "inventory")
    public CompletableFuture<InventoryResponse> inventory(DgsDataFetchingEnvironment dfe) {
        DataLoader<Long, InventoryResponse> dataLoader = dfe.getDataLoader("inventories");
        PlayerResponse player = dfe.getSource();
        return dataLoader.load(player.id());
    }

    @DgsQuery
    public PlayerResponse player(@InputArgument Long id) {
        return playerService.getPlayer(id);
    }

    @DgsQuery
    public List<LeaderboardEntry> leaderboard(
            @InputArgument Integer limit,
            @InputArgument LeaderboardSortBy sortBy
    ) {
        int effectiveLimit = limit != null ? limit : 10;
        LeaderboardSortBy effectiveSortBy = sortBy != null ? sortBy : LeaderboardSortBy.LEVEL;
        return playerService.getLeaderboard(effectiveLimit, effectiveSortBy);
    }

    @DgsMutation
    public PlayerResponse createPlayer(@InputArgument CreatePlayerInput input) {
        return playerService.createPlayer(input.name());
    }
}
