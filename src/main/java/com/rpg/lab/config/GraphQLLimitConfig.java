package com.rpg.lab.config;

import graphql.analysis.MaxQueryComplexityInstrumentation;
import graphql.analysis.MaxQueryDepthInstrumentation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GraphQLLimitConfig {

    private static final int MAX_QUERY_DEPTH = 6;
    private static final int MAX_QUERY_COMPLEXITY = 50;

    @Bean
    public MaxQueryDepthInstrumentation maxQueryDepthInstrumentation() {
        return new NonIntrospectionMaxQueryDepthInstrumentation(MAX_QUERY_DEPTH);
    }

    @Bean
    public MaxQueryComplexityInstrumentation maxQueryComplexityInstrumentation() {
        return new NonIntrospectionMaxQueryComplexityInstrumentation(MAX_QUERY_COMPLEXITY);
    }
}
