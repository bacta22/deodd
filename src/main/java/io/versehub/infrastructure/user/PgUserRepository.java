package io.versehub.infrastructure.user;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ndl.common.pgpool.supplier.PgConnectionSupplier;
import io.versehub.bef.commons.exception.BefException;
import io.versehub.bef.commons.exception.Errors;
import io.versehub.common.sql.SqlSingleResult;
import io.versehub.domain.user.UserRepository;
import io.versehub.domain.user.model.UserProfile;
import io.versehub.domain.user.model.UserTopNetGains;
import io.versehub.domain.user.model.UserTopStreak;
import io.versehub.infrastructure.common.repo.PgAbstractRepo;
import io.versehub.infrastructure.common.utils.GenerateRandomString;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Singleton
public class PgUserRepository extends PgAbstractRepo implements UserRepository {

    @Inject
    protected PgUserRepository(@NonNull PgConnectionSupplier pgConnectionSupplier) {
        super(pgConnectionSupplier);
    }

    @Override
    public CompletionStage<Collection<UserTopStreak>> getUserTopStreak() {
        return executeThenGet(Table.USER.getUserTopStreak())
                .thenApply(this::toListUserTopStreak)
                .exceptionally(e -> {
                    log.error("Error while get user top streak", e);
                    return ExceptionUtils.rethrow(e);
                });
    }

    @Override
    public CompletionStage<Collection<UserTopNetGains>> getUserTopNetGains() {
        return executeThenGet(Table.USER.getUserTopNetGains())
                .thenApply(this::toListUserTopNetGains)
                .exceptionally(e -> {
                    log.error("Error while get user net gains", e);
                    return ExceptionUtils.rethrow(e);
                });
    }

    @Override
    public CompletionStage<Integer> getNonceByAddress(String address) {
        return executeThenGetFirst(Table.USER.getNonceByAddress(address),
                row -> row == null ? null : row.getInteger("nonce"))
                .exceptionally(e -> {
                    log.error("Error while get nonce by address.", e);
                    return ExceptionUtils.rethrow(e);
                });
    }

    @Override
    public CompletionStage<UserProfile> generateNonceByAddress(UserProfile updatedUserProfile) {
        Integer nonce = (int) Math.floor(Math.random() * 10000);
        updatedUserProfile.setNonce(nonce);

        return execute(Table.USER.updateNonce(updatedUserProfile))
                .exceptionally(ex -> {
                    throw new BefException(Errors.POST_CONTENT_UPDATE_FAILED);
                })
                .thenCompose(SqlSingleResult::closeThenGetFirst)
                .thenApply(this::toUserProfile);


    }

    @Override
    public CompletionStage<String> getAddressByNonce(Integer nonce) {
        return executeThenGetFirst(Table.USER.getAddressByNonce(nonce),
                row -> row == null ? null : row.getString("wallet"))
                .exceptionally(e -> {
                    log.error("Error while get nonce by address.", e);
                    return ExceptionUtils.rethrow(e);
                });
    }

    @Override
    public CompletionStage<UserProfile> getUserByAddress(String address) {
        return executeThenGetFirst(Table.USER.getUserByAddress(address),this::toUserProfileTemp)
                .exceptionally(e -> {
                    log.error("Error while get nonce by address.", e);
                    return ExceptionUtils.rethrow(e);
                });
    }

    @Override
    public CompletionStage<UserProfile> updateUsernameAndAvatarId(UserProfile updatedUserProfile) {
        return execute(Table.USER.updateUsernameAndAvatarId(updatedUserProfile))
                .exceptionally(ex -> {
                    throw new BefException(Errors.POST_CONTENT_UPDATE_FAILED);
                })
                .thenCompose(SqlSingleResult::closeThenGetFirst)
                .thenApply(this::toUserProfile);
    }

    @Override
    public CompletionStage<UserProfile> generateReferralLink(UserProfile updatedUserProfile) {
        AtomicReference<String> newReferralLink = new AtomicReference<>(GenerateRandomString.randomString(8));
        updatedUserProfile.setReferralLink(newReferralLink.get());
        return execute(Table.USER.getAddressByReferralLink(updatedUserProfile.getReferralLink()))
                .thenCompose(SqlSingleResult::closeThenGetFirst)
                .thenCompose(checkRow -> {
                    if (!Objects.isNull(checkRow)) {
                        log.info("Duplicate referral link : {}", newReferralLink.get());
                        newReferralLink.set(GenerateRandomString.randomString(8));
                        log.info("New referral link : {}", newReferralLink.get());
                        updatedUserProfile.setReferralLink(newReferralLink.get());
                    }
                    return execute(Table.USER.updateReferralLink(updatedUserProfile))
                            .exceptionally(ex -> {
                                throw new BefException(Errors.POST_CONTENT_UPDATE_FAILED);
                            })
                            .thenCompose(SqlSingleResult::closeThenGetFirst)
                            .thenApply(this::toUserProfile);
                });
    }

    @Override
    public CompletionStage<String> getAddressByReferralLink(String referralLink) {
        return executeThenGetFirst(Table.USER.getAddressByReferralLink(referralLink),
                row -> row == null ? null : row.getString("wallet"))
                .exceptionally(e -> {
                    log.error("Error while get nonce by address.", e);
                    return ExceptionUtils.rethrow(e);
                });
    }

    @Override
    public CompletionStage<String> getReferralLinkByAddress(String address) {
        return executeThenGetFirst(Table.USER.getReferralLinkByAddress(address),
                row -> row == null ? null : row.getString("referral_link"))
                .exceptionally(e -> {
                    log.error("Error while get nonce by address.", e);
                    return ExceptionUtils.rethrow(e);
                });
    }

    @Override
    public CompletionStage<Boolean> checkUserValidForReferral(String address) {
        return execute(Table.USER.checkUserIsReferredOrNot(address))
                .thenCompose(SqlSingleResult::closeThenGetFirst)
                .thenCompose(checkRow -> {
                    if (!Objects.isNull(checkRow)) {
                        log.info("User {} is referred by others.", address);
                        return CompletableFuture.completedFuture(Boolean.FALSE);
                    }
                    return execute(Table.USER.checkFlipAmountOfUser(address))
                            .thenCompose(SqlSingleResult::closeThenGetFirst)
                            .thenCompose(checkRow2 -> {
                                if (!Objects.isNull(checkRow2)) {
                                    log.info("User {} have flipped before.", address);
                                    return CompletableFuture.completedFuture(Boolean.FALSE);
                                }
                                log.info("User {} is valid for referral.", address);
                                return CompletableFuture.completedFuture(Boolean.TRUE);
                            });

                });
    }

    private UserTopStreak toUserTopStreak(Row row) {
        if (row == null) {
            return null;
        }
        return UserTopStreak.builder()
                .wallet(row.getString("wallet"))
                .userName(row.getString("user_name"))
                .avatarId(row.getInteger("avatar_id"))
                .maxStreakLength(row.getInteger("max_streak_length"))
                .streakAmount(row.getBigDecimal("streak_amount"))
                .time(row.getLong("block_timestamp"))
                .build();
    }

    private Collection<UserTopStreak> toListUserTopStreak(RowSet<Row> rowSet) {
        if (rowSet == null || rowSet.size() == 0) {
            return Collections.emptyList();
        }

        var result = new ArrayList<UserTopStreak>();

        for (Row row : rowSet) {
            result.add(toUserTopStreak(row));
        }
        return result;
    }

    private UserTopNetGains toUserNetGains(Row row) {
        if (row == null) {
            return null;
        }
        return UserTopNetGains.builder()
                .wallet(row.getString("wallet"))
                .userName(row.getString("user_name"))
                .avatarId(row.getInteger("avatar_id"))
                .netGains(row.getBigDecimal("net_gains"))
                .build();
    }

    private Collection<UserTopNetGains> toListUserTopNetGains(RowSet<Row> rowSet) {
        if (rowSet == null || rowSet.size() == 0) {
            return Collections.emptyList();
        }

        var result = new ArrayList<UserTopNetGains>();

        for (Row row : rowSet) {
            result.add(toUserNetGains(row));
        }
        return result;
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

    private UserProfile toUserProfileTemp(Row row) {
        if (row == null) {
            return null;
        }
        return UserProfile.builder()
                .wallet(row.getString("wallet"))
                .userName(row.getString("user_name"))
                .avatarId(row.getInteger("avatar_id"))
                .maxStreakLength(row.getInteger("max_streak_length"))
                .streakAmount(row.getBigDecimal("streak_amount"))
                .currentStreakLength(row.getInteger("current_streak_length"))
                .currentStreakAmount(row.getBigDecimal("current_streak_amount"))
                .blockTimeStamp(row.getLong("block_timestamp"))
                .build();
    }
}
