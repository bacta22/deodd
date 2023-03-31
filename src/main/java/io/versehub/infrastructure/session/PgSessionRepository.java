package io.versehub.infrastructure.session;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ndl.common.pgpool.supplier.PgConnectionSupplier;
import io.versehub.common.sql.SqlSingleResult;
import io.versehub.domain.session.SessionRepository;
import io.versehub.domain.session.model.Session;
import io.versehub.infrastructure.common.repo.PgAbstractRepo;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

@Slf4j
@Singleton
public class PgSessionRepository extends PgAbstractRepo implements SessionRepository {

    @Inject
    protected PgSessionRepository(@NonNull PgConnectionSupplier pgConnectionSupplier) {
        super(pgConnectionSupplier);
    }

    public Session toSessionBean(Row row) {
        if (row == null)
            return null;

        return Session.builder()
                .id(row.getUUID("id"))
                .createdAt(row.getOffsetDateTime("created_at"))
                .expiredAt(row.getOffsetDateTime("expired_at"))
                .info(row.getJsonObject("info"))
                .wallet(row.getString("wallet"))
                .disabled(row.getBoolean("disabled"))
                .lastActive(row.getOffsetDateTime("last_active"))
                .build();
    }

    @Override
    public CompletionStage<UUID> createSession(String wallet, JsonObject info, OffsetDateTime expiredAt, OffsetDateTime lastActive) {
        return execute(Table.SESSION.createSession(wallet, info, expiredAt, lastActive)) //
                .thenCompose(SqlSingleResult::closeThenGetFirst) //
                .thenApply(this::returningUUID);
    }

    @Override
    public CompletionStage<Session> getBySessionId(UUID id) {
        return execute(Table.SESSION.fetchById(id)) //
                .thenCompose(SqlSingleResult::closeThenGetFirst) //
                .thenApply(this::toSessionBean);
    }

    @Override
    public CompletionStage<Void> removeExpiredOrDisabledSessions() {
        return execute(Table.SESSION.removeExpiredOrDisableSession()) //
                .thenCompose(SqlSingleResult::closeThenGetFirst) //
                .thenApply(any -> null);
    }

    @Override
    public CompletionStage<Void> disableSession(UUID id) {
        return execute(Table.SESSION.disableSession(id)) //
                .thenCompose(SqlSingleResult::close) //
                .thenApply(any -> null);
    }

    @Override
    public CompletionStage<Void> updateLastActive(UUID sessionId, OffsetDateTime lastActive) {
        return execute(Table.SESSION.updateLastActive(sessionId, lastActive))
                .thenCompose(SqlSingleResult::close)
                .thenApply(any -> null);
    }
}
