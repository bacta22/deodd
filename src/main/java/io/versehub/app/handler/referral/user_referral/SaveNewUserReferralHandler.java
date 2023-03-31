package io.versehub.app.handler.referral.user_referral;

import com.google.inject.Inject;
import io.versehub.app.AbstractHandler;
import io.versehub.bef.commons.http.response.BefHttpResponse;
import io.versehub.common.aapi.annotation.RegisterHandler;
import io.versehub.common.aapi.handler.VhProtocol;
import io.versehub.common.aapi.message.VhHttpMethod;
import io.versehub.common.aapi.message.http.VhHttpRequest;
import io.versehub.domain.referral.user_referral.model.UserReferral;
import io.versehub.domain.referral.user_referral.UserReferralRepository;
import io.versehub.domain.referral.user_referral.request.UserWithReferralLinkDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.concurrent.CompletionStage;

@Slf4j
@RegisterHandler(protocol = VhProtocol.HTTP, method = VhHttpMethod.POST, endpoint = "/users/ref/save")
public class SaveNewUserReferralHandler extends AbstractHandler<UserReferral> {

    @Inject
    UserReferralRepository userReferralRepository;

    @Override
    protected CompletionStage<BefHttpResponse<UserReferral>> doHandle(VhHttpRequest request) {
        var userWithReferralLink = request.getBody().toJsonObject().mapTo(UserWithReferralLinkDto.class);
        return userReferralRepository.save(userWithReferralLink)
                .thenApply(BefHttpResponse::success)
                .exceptionally(e -> {
                    log.error("Error while get user top streak.", e);
                    return ExceptionUtils.rethrow(e);
                });
    }
}
