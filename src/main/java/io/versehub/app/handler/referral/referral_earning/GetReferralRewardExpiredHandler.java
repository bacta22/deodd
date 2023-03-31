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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.concurrent.CompletionStage;

@Slf4j
@RegisterHandler(protocol = VhProtocol.HTTP, method = VhHttpMethod.GET, endpoint = "/users/ref/expired")
public class GetReferralRewardExpiredHandler extends AbstractHandler<ReferralRewardDto> {

    @Inject
    ReferralEarningRepository referralEarningRepository;

    @Override
    protected CompletionStage<BefHttpResponse<ReferralRewardDto>> doHandle(VhHttpRequest request) {
        String wallet = request.getQueryParam("wallet");
        return referralEarningRepository.getReferralRewardExpired(wallet)
                .thenApply(BefHttpResponse::success)
                .exceptionally(e -> {
                    log.error("Error while get user top streak.", e);
                    return ExceptionUtils.rethrow(e);
                });
    }
}
