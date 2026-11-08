package com.rpg.lab.config;

import graphql.ExecutionResult;
import graphql.analysis.MaxQueryDepthInstrumentation;
import graphql.execution.instrumentation.InstrumentationContext;
import graphql.execution.instrumentation.InstrumentationState;
import graphql.execution.instrumentation.SimpleInstrumentationContext;
import graphql.execution.instrumentation.parameters.InstrumentationExecuteOperationParameters;
import graphql.language.Field;
import graphql.language.OperationDefinition;

public class NonIntrospectionMaxQueryDepthInstrumentation extends MaxQueryDepthInstrumentation {

    public NonIntrospectionMaxQueryDepthInstrumentation(int maxDepth) {
        super(maxDepth);
    }

    @Override
    public InstrumentationContext<ExecutionResult> beginExecuteOperation(
            InstrumentationExecuteOperationParameters parameters,
            InstrumentationState state
    ) {
        if (isIntrospectionOnly(parameters.getExecutionContext().getOperationDefinition())) {
            return SimpleInstrumentationContext.noOp();
        }
        return super.beginExecuteOperation(parameters, state);
    }

    private boolean isIntrospectionOnly(OperationDefinition operation) {
        return operation.getSelectionSet().getSelections().stream()
                .allMatch(selection -> selection instanceof Field field
                            && field.getName().startsWith("__"));
    }
}
