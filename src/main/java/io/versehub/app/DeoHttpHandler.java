package io.versehub.app;

import com.google.inject.Inject;
import io.versehub.bef.commons.http.response.AbstractBefHttpHandler;
import io.versehub.common.aapi.handler.VhHandlerRegistry;
import io.versehub.common.aapi.handler.VhProtocol;
import io.versehub.infrastructure.config.DeoAppConfig;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import org.apache.commons.lang3.StringUtils;


public class DeoHttpHandler extends AbstractBefHttpHandler {

    private final VhHandlerRegistry handlerRegistry;
    private Router router;
    private DeoAppConfig config;

    @Inject
    public DeoHttpHandler(Router router, VhHandlerRegistry handlerRegistry, DeoAppConfig config) {
        this.router = router;
        this.handlerRegistry = handlerRegistry;
        this.config = config;
    }

    @SuppressWarnings("unchecked")
    public void handle() {
        var baseUrl = config.getHttp().getBaseUrl();
        var httpHandlers = handlerRegistry.lookup(VhProtocol.HTTP);
        httpHandlers.entrySet().forEach(entry -> {
            var key = entry.getKey();
            var method = key.method();
            var endpoint =
                StringUtils.isNotBlank(baseUrl)
                    ? baseUrl + key.endpoint()
                    : key.endpoint();
            var handler = entry.getValue();
            router.route(HttpMethod.valueOf(method), endpoint)
                .handler(BodyHandler.create())
                .handler(handlerVhRequest(handler));
        });
    }

}
