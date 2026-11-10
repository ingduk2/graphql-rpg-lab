package com.rpg.lab.config;

import com.rpg.lab.exception.ErrorCode;
import graphql.ErrorType;
import graphql.ExecutionResult;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.analysis.MaxQueryComplexityInstrumentation;
import graphql.execution.AbortExecutionException;
import graphql.execution.instrumentation.InstrumentationContext;
import graphql.execution.instrumentation.InstrumentationState;
import graphql.execution.instrumentation.SimpleInstrumentationContext;
import graphql.execution.instrumentation.parameters.InstrumentationExecuteOperationParameters;

import java.util.List;

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

    @Override
    protected AbortExecutionException mkAbortException(int totalComplexity, int maxComplexity) {
        GraphQLError error = GraphqlErrorBuilder.newError()
                .message("maximum query complexity exceeded %d > %d".formatted(totalComplexity, maxComplexity))
                .errorType(ErrorType.ExecutionAborted)
                .extensions(ErrorCode.QUERY_TOO_COMPLEX.toExtensions())
                .build();
        return new AbortExecutionException(List.of(error));
    }
}
