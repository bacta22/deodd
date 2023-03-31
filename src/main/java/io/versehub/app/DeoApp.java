package io.versehub.app;

import com.google.inject.Inject;
import io.versehub.common.component.AutoStopLifeCycle;
import io.vertx.core.http.HttpServer;

public class DeoApp extends AutoStopLifeCycle {

    @Inject
    private HttpServer httpServer;

    @Override
    protected void doStart() throws Exception {
        this.httpServer.listen().toCompletionStage().toCompletableFuture().get();
    }
}
