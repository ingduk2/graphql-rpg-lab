package com.rpg.lab.config;

import com.netflix.graphql.dgs.context.DgsCustomContextBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class PlayerContextBuilder implements DgsCustomContextBuilder<PlayerContext> {


    public static final String X_PLAYER_ID = "X-Player-Id";

    @Override
    public PlayerContext build() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return new PlayerContext(null);
        }

        String playerIdHeader = attrs.getRequest().getHeader(X_PLAYER_ID);
        Long playerId = playerIdHeader != null ? Long.parseLong(playerIdHeader) : null;
        return new PlayerContext(playerId);
    }
}
