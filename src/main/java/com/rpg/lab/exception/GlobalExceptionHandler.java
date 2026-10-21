package com.rpg.lab.exception;

import com.netflix.graphql.dgs.exceptions.DefaultDataFetcherExceptionHandler;
import graphql.GraphqlErrorBuilder;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class GlobalExceptionHandler extends DefaultDataFetcherExceptionHandler {

    @Override
    public @NonNull CompletableFuture<DataFetcherExceptionHandlerResult> handleException(@NonNull DataFetcherExceptionHandlerParameters handlerParameters) {
        Throwable exception = handlerParameters.getException();
        log.error("Unhandled exception in data fetcher: {}", handlerParameters.getPath(), exception);

        if (exception instanceof EntityNotFoundException e) {
            return CompletableFuture.completedFuture(buildResult(e, ErrorCode.NOT_FOUND, handlerParameters));
        }

        if (exception instanceof PrerequisiteQuestNotCompletedException e) {
            return CompletableFuture.completedFuture(buildResult(e, ErrorCode.PREREQUISITE_NOT_COMPLETED, handlerParameters));
        }

        if (exception instanceof InsufficientGoldException e) {
            return CompletableFuture.completedFuture(buildResult(e, ErrorCode.INSUFFICIENT_GOLD, handlerParameters));
        }

        if (exception instanceof ItemAlreadyOwnedException e) {
            return CompletableFuture.completedFuture(buildResult(e, ErrorCode.ITEM_ALREADY_OWNED, handlerParameters));
        }

        if (exception instanceof HpAlreadyFullException e) {
            return CompletableFuture.completedFuture(buildResult(e, ErrorCode.HP_ALREADY_FULL, handlerParameters));
        }

        return CompletableFuture.completedFuture(buildResult(exception, ErrorCode.INTERNAL_ERROR, handlerParameters));
    }

    private DataFetcherExceptionHandlerResult buildResult(
            Throwable e,
            ErrorCode errorCode,
            DataFetcherExceptionHandlerParameters handlerParameters
    ) {
        String message = errorCode == ErrorCode.INTERNAL_ERROR ? errorCode.getUserMessage() : e.getMessage();

        return DataFetcherExceptionHandlerResult.newResult()
                .error(GraphqlErrorBuilder.newError()
                        .message(message)
                        .extensions(errorCode.toExtensions())
                        .path(handlerParameters.getPath())
                        .build()
                )
                .build();
    }
}
