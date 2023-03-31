package io.versehub.infrastructure.referral.referral_earning;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ndl.common.pgpool.supplier.PgConnectionSupplier;
import io.versehub.bef.commons.exception.BefException;
import io.versehub.bef.commons.exception.Errors;
import io.versehub.domain.referral.referral_earning.ReferralEarningRepository;
import io.versehub.domain.referral.referral_earning.model.ReferralEarning;
import io.versehub.domain.referral.referral_earning.response.ReferralRewardDto;
import io.versehub.infrastructure.common.repo.PgAbstractRepo;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Slf4j
@Singleton
public class PgReferralEarningRepository extends PgAbstractRepo implements ReferralEarningRepository {

    @Inject
    protected PgReferralEarningRepository(@NonNull PgConnectionSupplier pgConnectionSupplier) {
        super(pgConnectionSupplier);
    }

    @Override
    public CompletionStage<ReferralRewardDto> getReferralRewardAvailable(String wallet) {
        return executeThenGet(Table.REFERRAL_EARNING.getReferralEarningByWallet(wallet))
                .thenApply(this::toListReferralEarning)
                .thenCompose(referralEarningCollection -> {
                    List<ReferralEarning> referralEarningList = referralEarningCollection.stream().toList();
                    List<ReferralEarning> referralEarningAvailableRoleGrandfatherList =
                            referralEarningList.stream()
                                    .filter(refEarning -> {
                                        if (refEarning.getUserWalletGrandfather() == null) return false;
                                        else return refEarning.getUserWalletGrandfather().equalsIgnoreCase(wallet)
                                                && !refEarning.getRewardGrandfatherUnclaimed().equals(BigDecimal.ZERO)
                                                && refEarning.getExpiredDateForGrandfather().isAfter(LocalDate.now());}).toList();
                    log.info("Referral earning available role Grandfather record: " + referralEarningAvailableRoleGrandfatherList.size());
                    List<ReferralEarning> referralEarningAvailableRoleFatherList =
                            referralEarningList.stream()
                                    .filter(refEarning -> refEarning.getUserWalletFather().equalsIgnoreCase(wallet)
                                            && !refEarning.getRewardFatherUnclaimed().equals(BigDecimal.ZERO)
                                            && refEarning.getExpiredDateForFather().isAfter(LocalDate.now())).toList();
                    log.info("Referral earning available role Father record: " + referralEarningAvailableRoleFatherList.size());
                    BigDecimal claimedReward = BigDecimal.ZERO;
                    BigDecimal unclaimedReward = BigDecimal.ZERO;
                    for (ReferralEarning referralEarningDto : referralEarningList) {
                        if (referralEarningDto.getUserWalletFather().equalsIgnoreCase(wallet)) {
                            if (referralEarningDto.getRewardFatherClaimed() != null)
                                claimedReward = claimedReward.add(referralEarningDto.getRewardFatherClaimed());
                            if (!referralEarningDto.getRewardFatherUnclaimed().equals(BigDecimal.ZERO)
                                    && referralEarningDto.getExpiredDateForFather().isAfter(LocalDate.now())) {
                                unclaimedReward = unclaimedReward.add(referralEarningDto.getRewardFatherUnclaimed());
                            }
                        } else {
                            if (referralEarningDto.getRewardGrandfatherClaimed() != null)
                                claimedReward = claimedReward.add(referralEarningDto.getRewardGrandfatherClaimed());
                            if (!referralEarningDto.getRewardGrandfatherUnclaimed().equals(BigDecimal.ZERO)
                                    && referralEarningDto.getExpiredDateForGrandfather().isAfter(LocalDate.now())) {
                                unclaimedReward = unclaimedReward.add(referralEarningDto.getRewardGrandfatherUnclaimed());
                            }
                        }
                    }
                    BigDecimal totalReward = claimedReward.add(unclaimedReward);
                    ReferralRewardDto referralRewardAvailableDto = ReferralRewardDto.builder()
                            .referralEarningRoleGrandfatherList(referralEarningAvailableRoleGrandfatherList)
                            .referralEarningRoleFatherList(referralEarningAvailableRoleFatherList)
                            .totalReward(totalReward)
                            .claimedReward(claimedReward)
                            .unclaimedReward(unclaimedReward)
                            .build();
                    return CompletableFuture.completedStage(referralRewardAvailableDto);
                })
                .exceptionally(e -> {
                    log.error("Error while get flip by Id", e);
                    return ExceptionUtils.rethrow(e);
                });
    }

    @Override
    public CompletionStage<ReferralRewardDto> getReferralRewardExpired(String wallet) {
        return executeThenGet(Table.REFERRAL_EARNING.getReferralEarningByWallet(wallet))
                .thenApply(this::toListReferralEarning)
                .thenCompose(referralEarningCollection -> {
                    List<ReferralEarning> referralEarningList = referralEarningCollection.stream().toList();
                    List<ReferralEarning> referralEarningExpiredRoleGrandfatherList =
                            referralEarningList.stream()
                                    .filter(refEarning -> {
                                        if (refEarning.getUserWalletGrandfather() == null) return false;
                                        else return refEarning.getUserWalletGrandfather().equalsIgnoreCase(wallet)
                                                && !refEarning.getRewardGrandfatherUnclaimed().equals(BigDecimal.ZERO)
                                                && refEarning.getExpiredDateForGrandfather().isBefore(LocalDate.now());}).toList();
                    log.info("Referral earning expired role Grandfather record: " + referralEarningExpiredRoleGrandfatherList.size());
                    List<ReferralEarning> referralEarningExpiredRoleFatherList =
                            referralEarningList.stream()
                                    .filter(refEarning -> refEarning.getUserWalletFather().equalsIgnoreCase(wallet)
                                            && !refEarning.getRewardFatherUnclaimed().equals(BigDecimal.ZERO)
                                            && refEarning.getExpiredDateForFather().isBefore(LocalDate.now())).toList();
                    log.info("Referral earning expired role Father record: " + referralEarningExpiredRoleFatherList.size());
                    BigDecimal claimedReward = BigDecimal.ZERO;
                    BigDecimal unclaimedReward = BigDecimal.ZERO;
                    for (ReferralEarning referralEarningDto : referralEarningList) {
                        if (referralEarningDto.getUserWalletFather().equalsIgnoreCase(wallet)) {
                            if (referralEarningDto.getRewardFatherClaimed() != null)
                                claimedReward = claimedReward.add(referralEarningDto.getRewardFatherClaimed());
                            if (!referralEarningDto.getRewardFatherUnclaimed().equals(BigDecimal.ZERO)
                                    && referralEarningDto.getExpiredDateForFather().isAfter(LocalDate.now())) {
                                unclaimedReward = unclaimedReward.add(referralEarningDto.getRewardFatherUnclaimed());
                            }
                        } else {
                            if (referralEarningDto.getRewardGrandfatherClaimed() != null)
                                claimedReward = claimedReward.add(referralEarningDto.getRewardGrandfatherClaimed());
                            if (!referralEarningDto.getRewardGrandfatherUnclaimed().equals(BigDecimal.ZERO)
                                    && referralEarningDto.getExpiredDateForGrandfather().isAfter(LocalDate.now())) {
                                unclaimedReward = unclaimedReward.add(referralEarningDto.getRewardGrandfatherUnclaimed());
                            }
                        }
                    }
                    BigDecimal totalReward = claimedReward.add(unclaimedReward);
                    ReferralRewardDto referralRewardExpiredDto = ReferralRewardDto.builder()
                            .referralEarningRoleGrandfatherList(referralEarningExpiredRoleGrandfatherList)
                            .referralEarningRoleFatherList(referralEarningExpiredRoleFatherList)
                            .totalReward(totalReward)
                            .claimedReward(claimedReward)
                            .unclaimedReward(unclaimedReward)
                            .build();
                    return CompletableFuture.completedStage(referralRewardExpiredDto);
                })
                .exceptionally(e -> {
                    log.error("Error while get flip by Id", e);
                    return ExceptionUtils.rethrow(e);
                });
    }

    @Override
    public CompletionStage<Void> claimReferralReward(String wallet) {
        return executeThenClose(Table.REFERRAL_EARNING.claimReferralRewardFather(wallet))
                .exceptionally(ex -> {
                    throw new BefException(Errors.POST_CONTENT_UPDATE_FAILED);
                })
                .thenCompose(res -> executeThenClose(Table.REFERRAL_EARNING.claimReferralRewardGrandfather(wallet))
                       .exceptionally(ex -> {
                           throw new BefException(Errors.POST_CONTENT_UPDATE_FAILED);
                       }));
    }

    private ReferralEarning toReferralEarning(Row row) {
        if (row == null) {
            return null;
        }
        return ReferralEarning.builder()//
                .id(row.getInteger("id"))
                .userWalletGrandfather(row.getString("user_wallet_grandfather"))
                .userNameGrandfather(row.getString("user_name_grandfather"))
                .userWalletFather(row.getString("user_wallet_father"))
                .userNameFather(row.getString("user_name_father"))
                .userWalletReferred(row.getString("user_wallet_referred"))
                .userNameReferred(row.getString("user_name_referred"))
                .rewardGrandfatherClaimed(row.getBigDecimal("reward_grandfather_claimed"))
                .rewardGrandfatherUnclaimed(row.getBigDecimal("reward_grandfather_unclaimed"))
                .rewardFatherClaimed(row.getBigDecimal("reward_father_claimed"))
                .rewardFatherUnclaimed(row.getBigDecimal("reward_father_unclaimed"))
                .expiredDateForGrandfather(row.getLocalDate("expired_date_for_grandfather"))
                .expiredDateForFather(row.getLocalDate("expired_date_for_father"))
                .build();
    }

    private Collection<ReferralEarning> toListReferralEarning(RowSet<Row> rowSet) {
        if (rowSet == null || rowSet.size() == 0) {
            return Collections.emptyList();
        }

        var result = new ArrayList<ReferralEarning>();

        for (Row row : rowSet) {
            result.add(toReferralEarning(row));
        }
        return result;
    }
}
