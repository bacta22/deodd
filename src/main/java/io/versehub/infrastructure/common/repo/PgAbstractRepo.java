package io.versehub.infrastructure.common.repo;

import com.ndl.common.pgpool.supplier.PgConnectionSupplier;
import io.versehub.common.sql.*;
import io.versehub.infrastructure.flip.PgFlipQueries;
import io.versehub.infrastructure.referral.referral_earning.PgReferralEarningQueries;
import io.versehub.infrastructure.referral.user_referral.PgUserReferralQueries;
import io.versehub.infrastructure.session.PgSessionQueries;
import io.versehub.infrastructure.user.PgUserQueries;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlConnection;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class PgAbstractRepo extends AbstractSqlClient {


    protected static class Table {
        private Table() {
        }
        public static final PgFlipQueries FLIP = new PgFlipQueries();
        public static final PgUserQueries USER = new PgUserQueries();
        public static final PgUserReferralQueries USER_REFERRAL = new PgUserReferralQueries();
        public static final PgReferralEarningQueries REFERRAL_EARNING = new PgReferralEarningQueries();

        public static final PgSessionQueries SESSION = new PgSessionQueries();

    }


    private final @NonNull PgConnectionSupplier pgConnectionSupplier;

    @Override
    protected CompletionStage<? extends SqlConnection> getConnection(SqlQueryMeta queryMeta) {
        return pgConnectionSupplier.getConnection(queryMeta);
    }

    protected CompletionStage<Row> executeThenGetFirst(SqlQuery query) {
        return execute(query).thenCompose(SqlSingleResult::closeThenGetFirst);
    }

    protected <T> CompletionStage<T> executeThenGetFirst(SqlQuery query, Function<Row, T> mapper) {
        return execute(query) //
                .thenCompose(SqlSingleResult::closeThenGetFirst) //
                .thenApply(mapper::apply);
    }

    protected CompletionStage<RowSet<Row>> executeThenGet(SqlQuery query) {
        return execute(query).thenCompose(SqlSingleResult::closeThenGet);
    }

    protected <T> CompletionStage<List<T>> executeThenGet(SqlQuery query, Function<Row, T> mapper) {
        return execute(query) //
                .thenCompose(SqlSingleResult::closeThenGet) //
                .thenApply(rowSet -> {
                    if (rowSet == null || rowSet.size() == 0)
                        return Collections.emptyList();

                    return StreamSupport //
                            .stream(rowSet.spliterator(), false) //
                            .map(mapper::apply) //
                            .toList();
                });
    }

    protected CompletionStage<Void> executeThenClose(SqlQuery query){
        return execute(query).thenCompose(SqlResult::close);
    }

    protected CompletionStage<Void> executeThenClose(SqlBatch query){
        return execute(query).thenCompose(SqlResult::close);
    }

    protected UUID returningUUID(Row row) {
        return row == null ? null : row.getUUID("id");
    }
}
