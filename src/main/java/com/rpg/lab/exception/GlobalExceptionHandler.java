package com.rpg.lab.exception;

import com.netflix.graphql.dgs.exceptions.DefaultDataFetcherExceptionHandler;
import graphql.GraphqlErrorBuilder;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class GlobalExceptionHandler extends DefaultDataFetcherExceptionHandler {

    @Override
    public @NonNull CompletableFuture<DataFetcherExceptionHandlerResult> handleException(@NonNull DataFetcherExceptionHandlerParameters handlerParameters) {
        Throwable exception = handlerParameters.getException();

        if (exception instanceof EntityNotFoundException e) {
            DataFetcherExceptionHandlerResult result = DataFetcherExceptionHandlerResult.newResult()
                    .error(GraphqlErrorBuilder.newError()
                            .message(e.getMessage())
                            .extensions(ErrorCode.NOT_FOUND.toExtensions())
                            .path(handlerParameters.getPath())
                            .build()
                    )
                    .build();

            return CompletableFuture.completedFuture(result);
        }

        return super.handleException(handlerParameters);
    }
}
