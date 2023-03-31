package io.versehub.app;

import com.google.common.primitives.Ints;
import io.versehub.bef.commons.exception.BefException;
import io.versehub.bef.commons.exception.Errors;
import io.versehub.bef.commons.http.response.BefHttpResponse;
import io.versehub.common.aapi.handler.VhHandler;
import io.versehub.common.aapi.message.http.VhHttpRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Slf4j
public abstract class AbstractHandler<R> implements VhHandler<VhHttpRequest, BefHttpResponse<R>> {


    protected static final String AUTHORIZATION_HEADER = "Authorization";
    protected static final String AUTH_HEADER_PREFIX = "Bearer";
    protected static final String AUTH_HEADER_PREFIX_LOWERCASE = "bearer";
    protected static final int AUTH_HEADER_PREFIX_LENGTH = AUTH_HEADER_PREFIX.length();
/*
    @Inject
    Authenticator authenticator;
    @Inject
    Authorizator authorizator;
    */

    @Override
    public CompletionStage<BefHttpResponse<R>> handle(VhHttpRequest request) {
        return doHandle(request);
    }

    protected abstract CompletionStage<BefHttpResponse<R>> doHandle(VhHttpRequest request);

    /*
    protected CompletionStage<User> authenticate(VhHttpRequest request) {
        var jwt = extractTokenFromRequest(request);
        return authenticator.validateAccessToken(jwt).thenCompose(token -> CompletableFuture.completedFuture(new User(token.getUUID("user_id"))));
    }

    protected CompletionStage<User> requireAdmin(VhHttpRequest request) {
        return authenticate(request).thenCompose(this::requireAdmin);
    }

    protected CompletionStage<User> requireAdmin(User user) {
        if (user == null || (authorizator != null && !authorizator.isAdmin(user.userId()))) {
            return CompletableFuture.failedFuture(new BefException(Errors.TOURNAMENT_FORBIDDEN));
        }
        return CompletableFuture.completedStage(user);
    }

     */

    protected String extractTokenFromRequest(VhHttpRequest request) {
        var token = request.getHeader(AUTHORIZATION_HEADER);
        if (token == null)
            return null;

        if (token.startsWith(AUTH_HEADER_PREFIX) || token.startsWith(AUTH_HEADER_PREFIX_LOWERCASE))
            return token.substring(AUTH_HEADER_PREFIX_LENGTH).trim();

        return token;
    }


    protected <V> V deserializeRequest(VhHttpRequest request, Class<V> type) {
        return request.getBody().toJsonObject().mapTo(type);
    }

    /*
    protected <V> CompletionStage<V> failedStage(Throwable throwable) {
        return CompletableFuture.failedStage(throwable);
    }
    */

    protected <V> CompletionStage<V> failedStageByErrors(Errors errors) {
        return CompletableFuture.failedStage(new BefException(errors));
    }

    protected <V> CompletionStage<V> completedStage(V data) {
        return CompletableFuture.completedStage(data);
    }

    /*
    protected Paging extractPaging(VhHttpRequest request) {
        try {
            int page = Optional.ofNullable(request.getQueryParam("page"))
                .map(Ints::tryParse)
                .orElse(1);
            int size = Optional.ofNullable(request.getQueryParam("size"))
                .map(Ints::tryParse)
                .orElse(0);
            return new Paging(page, size);
        } catch (Exception e) {
            log.error("error while extract paging from request:\n", e);
            return null;
        }
    }
    */
}
