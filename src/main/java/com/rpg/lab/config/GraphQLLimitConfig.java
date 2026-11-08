package com.rpg.lab.config;

import graphql.analysis.MaxQueryDepthInstrumentation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GraphQLLimitConfig {

    private static final int MAX_QUERY_DEPTH = 6;

    @Bean
    public MaxQueryDepthInstrumentation maxQueryDepthInstrumentation() {
        return new NonIntrospectionMaxQueryDepthInstrumentation(MAX_QUERY_DEPTH);
    }
}
