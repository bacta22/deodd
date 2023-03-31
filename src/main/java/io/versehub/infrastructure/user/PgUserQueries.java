package io.versehub.infrastructure.user;

import com.google.inject.Singleton;
import io.versehub.common.sql.SqlQuery;
import io.versehub.common.sql.SqlQueryMeta;
import io.versehub.domain.user.model.UserProfile;


@Singleton
public class PgUserQueries {
    public SqlQuery getUserTopStreak() {
        var sql = """
                SELECT *,TO_timestamp(block_timestamp) AS time_stamp
                FROM user_profile ORDER BY max_streak_length DESC LIMIT 10;
                """;
        return SqlQuery.of(sql).setQueryMeta(SqlQueryMeta.READ_ONLY);
    }

    public SqlQuery getUserTopNetGains() {
        var sql = """
                SELECT rf.wallet, up.user_name, up.avatar_id,
                    SUM (
                        CASE flip_result
                            WHEN 1 THEN amount
                            ELSE -amount
                        END
                    ) as net_gains
                FROM recent_flipping rf INNER JOIN user_profile up ON rf.wallet = up.wallet
                WHERE rf.block_timestamp/86400 >
                    (
                        SELECT FLOOR (extract(epoch from now()) / 86400)
                    )
                GROUP BY rf.wallet, up.user_name, up.avatar_id
                ORDER BY net_gains DESC
                LIMIT 10;
                """;
        return SqlQuery.of(sql).setQueryMeta(SqlQueryMeta.READ_ONLY);
    }

    public SqlQuery getNonceByAddress(String address) {
        var sql = """
                SELECT nonce FROM user_profile
                WHERE wallet ILIKE $1;
                """;
        return SqlQuery.of(sql).withArg(address).setQueryMeta(SqlQueryMeta.READ_ONLY);
    }

    public SqlQuery updateNonce(UserProfile updatedUserProfile) {
        var sql = """
            UPDATE public.user_profile SET nonce = $1
            WHERE wallet ILIKE $2
            RETURNING *
            """;
        return SqlQuery.of(sql).withArgs(updatedUserProfile.getNonce(), updatedUserProfile.getWallet());
    }

    public SqlQuery getAddressByNonce(Integer nonce) {
        var sql = """
                SELECT wallet FROM user_profile
                WHERE nonce = $1;
                """;
        return SqlQuery.of(sql).withArg(nonce).setQueryMeta(SqlQueryMeta.READ_ONLY);
    }

    public SqlQuery getUserByAddress(String address) {
        var sql = """
                SELECT * FROM user_profile
                WHERE wallet ILIKE $1;
                """;
        return SqlQuery.of(sql).withArg(address).setQueryMeta(SqlQueryMeta.READ_ONLY);
    }

    public SqlQuery updateUsernameAndAvatarId(UserProfile updatedUserProfile) {
        var sql = """
                UPDATE user_profile
                SET user_name = $1, avatar_id = $2
                WHERE wallet = $3
                RETURNING *
                """;
        return SqlQuery.of(sql).withArgs(updatedUserProfile.getUserName()
                , updatedUserProfile.getAvatarId(), updatedUserProfile.getWallet());
    }

    public SqlQuery getAddressByReferralLink(String referralLink) {
        var sql = """
                SELECT wallet FROM user_profile
                WHERE referral_link = $1;
                """;
        return SqlQuery.of(sql).withArg(referralLink).setQueryMeta(SqlQueryMeta.READ_ONLY);
    }

    public SqlQuery updateReferralLink(UserProfile updatedUserProfile) {
        var sql = """
            UPDATE public.user_profile SET referral_link = $1
            WHERE wallet ILIKE $2
            RETURNING *
            """;
        return SqlQuery.of(sql).withArgs(updatedUserProfile.getReferralLink(), updatedUserProfile.getWallet());

    }

    public SqlQuery getReferralLinkByAddress(String address) {
        var sql = """
                SELECT referral_link FROM user_profile
                WHERE wallet ILIKE $1;
                """;
        return SqlQuery.of(sql).withArg(address).setQueryMeta(SqlQueryMeta.READ_ONLY);

    }

    public SqlQuery checkUserIsReferredOrNot(String address) {
        var sql = """
                SELECT * FROM user_referral
                WHERE user_wallet_referred ILIKE $1
                """;
        return SqlQuery.of(sql).withArg(address).setQueryMeta(SqlQueryMeta.READ_ONLY);

    }

    public SqlQuery checkFlipAmountOfUser(String address) {
        var sql = """
                SELECT * FROM recent_flipping WHERE wallet ILIKE $1
                """;
        return SqlQuery.of(sql).withArg(address).setQueryMeta(SqlQueryMeta.READ_ONLY);
    }

    public SqlQuery getUserByReferralLink(String referralLink) {
        var sql = """
                SELECT * FROM user_profile
                WHERE referral_link = $1;
                """;
        return SqlQuery.of(sql).withArg(referralLink).setQueryMeta(SqlQueryMeta.READ_ONLY);
    }
}

