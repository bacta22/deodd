package io.versehub.domain.referral.referral_earning;

import io.versehub.domain.referral.referral_earning.model.ReferralEarning;
import io.versehub.domain.referral.referral_earning.response.ReferralRewardDto;

import java.util.Collection;
import java.util.concurrent.CompletionStage;

public interface ReferralEarningRepository {

    CompletionStage<ReferralRewardDto> getReferralRewardAvailable(String wallet);

    CompletionStage<ReferralRewardDto> getReferralRewardExpired(String wallet);

    CompletionStage<Void> claimReferralReward(String wallet);
}

