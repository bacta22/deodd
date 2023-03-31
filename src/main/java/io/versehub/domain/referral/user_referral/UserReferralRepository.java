package io.versehub.domain.referral.user_referral;

import io.versehub.domain.referral.user_referral.model.UserReferral;
import io.versehub.domain.referral.user_referral.request.UserWithReferralLinkDto;

import java.util.concurrent.CompletionStage;

public interface UserReferralRepository {

    CompletionStage<UserReferral> save (UserWithReferralLinkDto userWithReferralLink);
}

