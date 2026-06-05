package com.rpg.lab.ping;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;

@DgsComponent
public class PingDataFetcher {

    @DgsQuery
    public String ping() {
        return "pong!";
    }
}
