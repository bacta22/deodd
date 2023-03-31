package io.versehub.infrastructure.referral.referral_earning;

import com.google.inject.Singleton;
import io.versehub.common.sql.SqlQuery;
import io.versehub.common.sql.SqlQueryMeta;


@Singleton
public class PgReferralEarningQueries {

    public SqlQuery getReferralEarningByWallet(String wallet) {
        var sql = """
                SELECT * FROM referral_earning
                WHERE user_wallet_grandfather ILIKE $1
                OR user_wallet_father ILIKE $1
                """;
        return SqlQuery.of(sql).withArg(wallet).setQueryMeta(SqlQueryMeta.READ_ONLY);
    }

    public SqlQuery claimReferralRewardFather(String wallet) {
        var sql = """
                WITH rows AS (
                    UPDATE referral_earning
                    SET reward_father_claimed = reward_father_claimed + reward_father_unclaimed, reward_father_unclaimed = 0
                    WHERE user_wallet_father ILIKE $1 AND expired_date_for_father > CURRENT_DATE AND reward_father_unclaimed > 0
                    RETURNING 1
                    )
                SELECT count(*) FROM rows;
                """;
        return SqlQuery.of(sql).withArgs(wallet);
    }

    public SqlQuery claimReferralRewardGrandfather(String wallet) {
        var sql = """
                WITH rows AS (
                    UPDATE referral_earning
                    SET reward_grandfather_claimed = reward_grandfather_claimed + reward_grandfather_unclaimed, reward_grandfather_unclaimed = 0
                    WHERE user_wallet_grandfather ILIKE $1 AND expired_date_for_grandfather > CURRENT_DATE AND reward_grandfather_unclaimed > 0
                    RETURNING 1
                    )
                SELECT count(*) FROM rows;
                """;
        return SqlQuery.of(sql).withArgs(wallet);
    }
}
