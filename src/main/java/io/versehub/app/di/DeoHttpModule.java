package io.versehub.app.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.versehub.app.DeoHttpHandler;
import io.versehub.infrastructure.config.DeoAppConfig;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;

public class DeoHttpModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(DeoHttpHandler.class);
    }

    @Provides
    @Singleton
    Router router(Vertx vertx) {
        return Router.router(vertx);
    }

    @Provides
    @Singleton
    HttpServer httpServer(DeoAppConfig config, Router router, Vertx vertx,
                          DeoHttpHandler httpHandler) {
        var options = new HttpServerOptions() //
                .setHost(config.getHttp().getHost()) //
                .setPort(config.getHttp().getPort());
        var httpServer = vertx.createHttpServer(options);
        httpServer.requestHandler(router);
        httpHandler.handle();
        return httpServer;
    }
}
