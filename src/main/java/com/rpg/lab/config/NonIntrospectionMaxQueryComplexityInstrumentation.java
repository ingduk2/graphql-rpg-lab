package com.rpg.lab.config;

import graphql.ExecutionResult;
import graphql.analysis.MaxQueryComplexityInstrumentation;
import graphql.execution.instrumentation.InstrumentationContext;
import graphql.execution.instrumentation.InstrumentationState;
import graphql.execution.instrumentation.SimpleInstrumentationContext;
import graphql.execution.instrumentation.parameters.InstrumentationExecuteOperationParameters;

public class NonIntrospectionMaxQueryComplexityInstrumentation extends MaxQueryComplexityInstrumentation {

    public NonIntrospectionMaxQueryComplexityInstrumentation(int maxComplexity) {
        super(maxComplexity);
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
