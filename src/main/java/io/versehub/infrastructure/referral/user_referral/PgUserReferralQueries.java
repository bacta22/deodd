package io.versehub.infrastructure.referral.user_referral;

import com.google.inject.Singleton;
import io.versehub.common.sql.SqlQuery;
import io.versehub.common.sql.SqlQueryMeta;
import io.versehub.domain.referral.user_referral.model.UserReferral;


@Singleton
public class PgUserReferralQueries {
    public SqlQuery getFatherByWalletChild(String walletChild) {
        var sql = """
                SELECT * FROM user_profile
                WHERE wallet ILIKE 
                    (
                        SELECT user_wallet_father FROM user_referral
                        WHERE user_wallet_referred ILIKE $1
                    );
                """;
        return SqlQuery.of(sql).withArg(walletChild).setQueryMeta(SqlQueryMeta.READ_ONLY);
    }

    public SqlQuery save(UserReferral userReferral) {
        var sql =
                """
                INSERT INTO user_referral (user_wallet_grandfather, user_name_grandfather,
                                          user_wallet_father, user_name_father,
                                          user_wallet_referred, user_name_referred,
                                          referral_date_time)
                VALUES ($1,$2,$3,$4,$5,$6,CURRENT_TIMESTAMP)
                RETURNING *;
                """;
        return SqlQuery.of(sql) //
                .withArgs(userReferral.getUserWalletGrandfather(), // 1
                        userReferral.getUserNameGrandfather(), // 2
                        userReferral.getUserWalletFather(), // 3
                        userReferral.getUserNameFather(), // 4
                        userReferral.getUserWalletReferred(), // 5
                        userReferral.getUserNameReferred()); // 6
    }
}
