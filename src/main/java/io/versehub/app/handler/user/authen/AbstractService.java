package io.versehub.app.handler.user.authen;

import io.versehub.bef.commons.exception.BefException;
import io.versehub.bef.commons.exception.Errors;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class AbstractService {
    protected <U> CompletionStage<U> failedStage(Throwable throwable) {
        return CompletableFuture.failedStage(throwable);
    }

    protected <U> CompletionStage<U> failedStageByErrors(Errors errors) {
        return CompletableFuture.failedStage(new BefException(errors));
    }

    protected <U> CompletionStage<U> completedStage(U data){
        return CompletableFuture.completedStage(data);
    }
}
