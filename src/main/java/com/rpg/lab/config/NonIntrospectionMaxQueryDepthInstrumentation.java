package com.rpg.lab.config;

import graphql.ExecutionResult;
import graphql.analysis.MaxQueryDepthInstrumentation;
import graphql.execution.instrumentation.InstrumentationContext;
import graphql.execution.instrumentation.InstrumentationState;
import graphql.execution.instrumentation.SimpleInstrumentationContext;
import graphql.execution.instrumentation.parameters.InstrumentationExecuteOperationParameters;

public class NonIntrospectionMaxQueryDepthInstrumentation extends MaxQueryDepthInstrumentation {

    public NonIntrospectionMaxQueryDepthInstrumentation(int maxDepth) {
        super(maxDepth);
    }

    @Override
    public InstrumentationContext<ExecutionResult> beginExecuteOperation(
            InstrumentationExecuteOperationParameters parameters,
            InstrumentationState state
    ) {
        if (IntrospectionQueries.isIntrospectionOnly(parameters.getExecutionContext().getOperationDefinition())) {
            return SimpleInstrumentationContext.noOp();
        }
        return super.beginExecuteOperation(parameters, state);
    }
}
