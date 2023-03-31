package io.versehub.app.handler.user.authen;

import com.google.inject.Inject;
import io.versehub.app.handler.user.authen.support.AccountTokenHelper;
import io.versehub.bef.commons.exception.BefException;
import io.versehub.bef.commons.exception.Errors;
import io.versehub.common.token.Token;
import io.versehub.domain.session.SessionRepository;
import io.versehub.domain.session.model.Session;
import lombok.AccessLevel;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class AbstractAuthenticationService extends AbstractService {

    private static final Integer TIME_OUT_SESSION = 180;
    private static final Integer NOT_ONLINE_EXPIRATION = 30;

    @Inject
    @Getter(value = AccessLevel.PROTECTED)
    private SessionRepository sessionRepository;

    @Inject
    @Getter(value = AccessLevel.PROTECTED)
    private AccountTokenHelper accountTokenHelper;


    protected CompletionStage<Session> getSessionBean(String sessionId) {
        return sessionRepository.getBySessionId(UUID.fromString(sessionId)) //
                .thenCompose(session -> {
                    if (session == null || session.isDisabled() || isExpired(session)) {
                        return CompletableFuture.failedStage(new BefException(Errors.INVALID_SESSION));
                    }
                    return CompletableFuture.completedStage(session);
                });
    }

    protected CompletionStage<Session> getSessionByRefreshToken(String refreshToken) {
        return accountTokenHelper.validateRefreshToken(refreshToken)
                .exceptionally(any -> null)
                .thenCompose(token -> {
                    if (token == null)
                        throw new BefException(io.versehub.bef.commons.exception.Errors.INVALID_REFRESH_TOKEN);
                    return getSessionBean(getSessionIdFromPayload(token));
                });
    }

    protected String getSessionIdFromPayload(Token token) {
        return token.getClaim("session_id").asString();
    }


    private boolean isExpired(Session session) {
        return session.getLastActive()
                .plusDays(NOT_ONLINE_EXPIRATION)
                .isBefore(OffsetDateTime.now());
    }

    protected Integer getTimeOutSession() {
        return TIME_OUT_SESSION;
    }

    protected CompletionStage<Void> updateLastActive(UUID sessionId) {
        return sessionRepository.updateLastActive(sessionId, OffsetDateTime.now());
    }
}
