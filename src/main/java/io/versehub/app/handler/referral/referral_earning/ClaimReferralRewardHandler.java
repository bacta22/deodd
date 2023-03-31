package io.versehub.app.handler.referral.referral_earning;

import com.google.inject.Inject;
import io.versehub.app.AbstractHandler;
import io.versehub.bef.commons.http.response.BefHttpResponse;
import io.versehub.common.aapi.annotation.RegisterHandler;
import io.versehub.common.aapi.handler.VhProtocol;
import io.versehub.common.aapi.message.VhHttpMethod;
import io.versehub.common.aapi.message.http.VhHttpRequest;
import io.versehub.domain.referral.referral_earning.ReferralEarningRepository;
import io.versehub.domain.referral.referral_earning.response.ReferralRewardDto;
import io.versehub.domain.user.model.UserProfile;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.concurrent.CompletionStage;

@Slf4j
@RegisterHandler(protocol = VhProtocol.HTTP, method = VhHttpMethod.PUT, endpoint = "/users/ref/claim")
public class ClaimReferralRewardHandler extends AbstractHandler<Void> {

    @Inject
    ReferralEarningRepository referralEarningRepository;

    @Override
    protected CompletionStage<BefHttpResponse<Void>> doHandle(VhHttpRequest request) {
        var user = request.getBody().toJsonObject().mapTo(UserProfile.class);
        String wallet = user.getWallet();
        return referralEarningRepository.claimReferralReward(wallet)
                .thenApply(BefHttpResponse::success)
                .exceptionally(e -> {
                    log.error("Error while get user top streak.", e);
                    return ExceptionUtils.rethrow(e);
                });
    }
}
