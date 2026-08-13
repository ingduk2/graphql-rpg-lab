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
            return CompletableFuture.completedFuture(buildResult(e, ErrorCode.NOT_FOUND, handlerParameters));
        }

        if (exception instanceof PrerequisiteQuestNotCompletedException e) {
            return CompletableFuture.completedFuture(buildResult(e, ErrorCode.PREREQUISITE_NOT_COMPLETED, handlerParameters));
        }

        return super.handleException(handlerParameters);
    }

    private DataFetcherExceptionHandlerResult buildResult(
            Throwable e,
            ErrorCode errorCode,
            DataFetcherExceptionHandlerParameters handlerParameters
    ) {
        return DataFetcherExceptionHandlerResult.newResult()
                .error(GraphqlErrorBuilder.newError()
                        .message(e.getMessage())
                        .extensions(errorCode.toExtensions())
                        .path(handlerParameters.getPath())
                        .build()
                )
                .build();
    }
}
