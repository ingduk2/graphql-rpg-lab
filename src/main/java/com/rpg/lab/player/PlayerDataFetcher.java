package com.rpg.lab.player;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import lombok.RequiredArgsConstructor;

import java.util.List;

@DgsComponent
@RequiredArgsConstructor
public class PlayerDataFetcher {

    private final PlayerService playerService;

    @DgsQuery
    public List<PlayerResponse> players() {
        List<Player> players = playerService.getPlayers();

        return players.stream()
                .map(PlayerResponse::from)
                .toList();
    }

    @DgsQuery
    public PlayerResponse player(@InputArgument Long id) {
        Player player = playerService.getPlayer(id);

        return PlayerResponse.from(player);
    }
}
