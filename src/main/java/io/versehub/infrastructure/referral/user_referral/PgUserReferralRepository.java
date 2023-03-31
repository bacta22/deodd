package io.versehub.infrastructure.referral.user_referral;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ndl.common.pgpool.supplier.PgConnectionSupplier;
import io.versehub.common.sql.SqlSingleResult;
import io.versehub.domain.referral.user_referral.model.UserReferral;
import io.versehub.domain.referral.user_referral.UserReferralRepository;
import io.versehub.domain.referral.user_referral.request.UserWithReferralLinkDto;
import io.versehub.domain.user.model.UserProfile;
import io.versehub.infrastructure.common.repo.PgAbstractRepo;
import io.vertx.sqlclient.Row;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Slf4j
@Singleton
public class PgUserReferralRepository extends PgAbstractRepo implements UserReferralRepository {

    @Inject
    protected PgUserReferralRepository(@NonNull PgConnectionSupplier pgConnectionSupplier) {
        super(pgConnectionSupplier);
    }

    @Override
    public CompletionStage<UserReferral> save(UserWithReferralLinkDto userWithReferralLink) {
        UserProfile referredUserChild = userWithReferralLink.getUser();
        String referralLinkUserFather = userWithReferralLink.getReferralLink();
        if (referredUserChild == null || referralLinkUserFather == null) {
            log.error("User or Referral link can not be null.");
            return CompletableFuture.failedStage(new Throwable("User or Referral link can not be null."));
        } else {
            return execute(Table.USER.getUserByReferralLink(referralLinkUserFather))
                    .thenCompose(SqlSingleResult::closeThenGetFirst)
                    .thenApply(this::toUserProfile)
                    .thenCompose(userFather -> {
                        if (Objects.isNull(userFather)) {
                            log.error("User father with referral link does not exist.");
                            return CompletableFuture.failedStage(new RuntimeException("User father with referral link does not exist."));
                        } else {
                            log.info("User wallet father : {} ", userFather.getWallet());
                            UserReferral newUserReferral = new UserReferral();
                            newUserReferral.setUserWalletFather(userFather.getWallet());
                            newUserReferral.setUserNameFather(userFather.getUserName());
                            newUserReferral.setUserWalletReferred(referredUserChild.getWallet());
                            newUserReferral.setUserNameReferred(referredUserChild.getUserName());
                            return execute(Table.USER_REFERRAL.getFatherByWalletChild(userFather.getWallet()))
                                    .thenCompose(SqlSingleResult::closeThenGetFirst)
                                    .thenApply(this::toUserProfile)
                                    .thenCompose(userGrandfather -> {
                                        if (Objects.isNull(userGrandfather)) {
                                            log.info("User grandfather does not exist.");
                                            newUserReferral.setUserWalletGrandfather(null);
                                            newUserReferral.setUserNameGrandfather(null);
                                        } else {
                                            log.info("User wallet grandfather : {} ", userGrandfather.getWallet());
                                            newUserReferral.setUserWalletGrandfather(userGrandfather.getWallet());
                                            newUserReferral.setUserNameGrandfather(userGrandfather.getUserName());
                                        }
                                        return executeThenGetFirst(Table.USER_REFERRAL.save(newUserReferral),
                                                this::toUserReferral)
                                                .exceptionally(e -> {
                                                    log.error("Error while save new user referral: ", e);
                                                    return ExceptionUtils.rethrow(e);
                                                });
                                    });
                        }
                    });
        }
    }

    private UserReferral toUserReferral (Row row) {
        if (row == null) {
            return null;
        }
        return UserReferral.builder()
                .id(row.getInteger("id"))
                .userWalletGrandfather(row.getString("user_wallet_grandfather"))
                .userNameGrandfather(row.getString("user_name_grandfather"))
                .userWalletFather(row.getString("user_wallet_father"))
                .userNameFather(row.getString("user_name_father"))
                .userWalletReferred(row.getString("user_wallet_referred"))
                .userNameReferred(row.getString("user_name_referred"))
                .referralDateTime(row.getLocalDateTime("referral_date_time"))
                .build();
    }

    private UserProfile toUserProfile(Row row) {
        if (row == null) {
            return null;
        }
        return UserProfile.builder()
                .wallet(row.getString("wallet"))
                .userName(row.getString("user_name"))
                .avatarId(row.getInteger("avatar_id"))
                .nonce(row.getInteger("nonce"))
                .maxStreakLength(row.getInteger("max_streak_length"))
                .streakAmount(row.getBigDecimal("streak_amount"))
                .currentStreakLength(row.getInteger("current_streak_length"))
                .currentStreakAmount(row.getBigDecimal("current_streak_amount"))
                .blockTimeStamp(row.getLong("block_timestamp"))
                .referralLink(row.getString("referral_link"))
                .signInDateTime(row.getLocalDateTime("sign_in_time"))
                .build();
    }
}
