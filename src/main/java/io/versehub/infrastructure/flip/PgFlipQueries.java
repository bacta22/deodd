package io.versehub.infrastructure.flip;

import com.google.inject.Singleton;
import io.versehub.common.sql.SqlQuery;
import io.versehub.common.sql.SqlQueryMeta;


@Singleton
public class PgFlipQueries {
    public SqlQuery getById(Integer flipId) {
        var sql = """
                SELECT * FROM recent_flipping rf
                INNER JOIN user_profile up ON up.wallet = rf.wallet
                WHERE flip_id=$1
                """;
        return SqlQuery.of(sql).withArg(flipId).setQueryMeta(SqlQueryMeta.READ_ONLY);
    }

    public SqlQuery getFlipRecent() {
        var sql = """
                SELECT *, TO_timestamp(rf.block_timestamp) AS time_stamp
                FROM recent_flipping rf
                INNER JOIN user_profile up ON up.wallet = rf.wallet
                ORDER BY time_stamp DESC
                LIMIT 10;
                """;
        return SqlQuery.of(sql).setQueryMeta(SqlQueryMeta.READ_ONLY);
    }
}
