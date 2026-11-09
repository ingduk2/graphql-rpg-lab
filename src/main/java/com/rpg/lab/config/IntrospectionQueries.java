package com.rpg.lab.config;

import graphql.language.Field;
import graphql.language.OperationDefinition;

final class IntrospectionQueries {

    private IntrospectionQueries() {
    }

    /**
     * 루트 필드가 전부 introspection(__schema, __type)일 때만 true.
     * 일반 필드와 섞인 쿼리는 false (우회 방지)
     */
    static boolean isIntrospectionOnly(OperationDefinition operation) {
        return operation.getSelectionSet().getSelections().stream()
                .allMatch(selection -> selection instanceof Field field
                        && field.getName().startsWith("__"));
    }
}
