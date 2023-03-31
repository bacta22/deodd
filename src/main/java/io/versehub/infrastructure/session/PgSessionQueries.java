package io.versehub.infrastructure.session;

import com.google.inject.Singleton;
import io.versehub.common.sql.SqlQuery;
import io.versehub.common.sql.SqlQueryMeta;
import io.vertx.core.json.JsonObject;

import java.time.OffsetDateTime;
import java.util.UUID;


@Singleton
public class PgSessionQueries {
    public SqlQuery createSession(String wallet, JsonObject info, OffsetDateTime expiredAt, OffsetDateTime lastActive) {
        String sql = """
				insert into public."session" (id, wallet, info, expired_at, last_active)
				values (uuid_generate_v1(), $1, $2, $3, $4) returning id
				""";
        return SqlQuery.of(sql).withArgs(wallet, info, expiredAt, lastActive);
    }

    public SqlQuery fetchById(UUID id) {
        String sql = """
				select * from public."session" where id = $1
				""";
        return SqlQuery.of(sql).withArgs(id);
    }

    public SqlQuery disableSession(UUID id) {
        var sql = """
				update public."session" set disabled = true where id = $1
				""";
        return SqlQuery.of(sql).withArg(id);
    }

    public SqlQuery removeExpiredOrDisableSession() {
        final var sql = """
				delete from public."session" where disabled = true or expired_at < current_timestamp; 
				""";
        return SqlQuery.of(sql);
    }

    public SqlQuery updateLastActive(UUID sessionId, OffsetDateTime lastActive) {
        var sql = """
                update public."session" set last_active = $2 where id = $1
                """;
        return SqlQuery.of(sql).withArgs(sessionId, lastActive);
    }
}
