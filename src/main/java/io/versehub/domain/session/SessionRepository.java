package io.versehub.domain.session;

import io.versehub.domain.session.model.Session;
import io.vertx.core.json.JsonObject;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

public interface SessionRepository {
    CompletionStage<UUID> createSession(String wallet, JsonObject info, OffsetDateTime expiredAt, OffsetDateTime lastActive);

    CompletionStage<Session> getBySessionId(UUID id);

    CompletionStage<Void> removeExpiredOrDisabledSessions();

    CompletionStage<Void> disableSession(UUID id);

    CompletionStage<Void> updateLastActive(UUID sessionId, OffsetDateTime lastActive);
}
