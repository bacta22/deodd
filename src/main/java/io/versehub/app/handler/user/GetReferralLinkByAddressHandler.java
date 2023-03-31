package io.versehub.app.handler.user;

import com.google.inject.Inject;
import io.versehub.app.AbstractHandler;
import io.versehub.bef.commons.http.response.BefHttpResponse;
import io.versehub.common.aapi.annotation.RegisterHandler;
import io.versehub.common.aapi.handler.VhProtocol;
import io.versehub.common.aapi.message.VhHttpMethod;
import io.versehub.common.aapi.message.http.VhHttpRequest;
import io.versehub.domain.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.concurrent.CompletionStage;

@Slf4j
@RegisterHandler(protocol = VhProtocol.HTTP, method = VhHttpMethod.GET, endpoint = "/users/ref/findLink")
public class GetReferralLinkByAddressHandler extends AbstractHandler<String> {

    @Inject
    UserRepository userRepository;

    @Override
    protected CompletionStage<BefHttpResponse<String>> doHandle(VhHttpRequest request) {
        String address = request.getQueryParam("address");
        return userRepository.getReferralLinkByAddress(address)
                .thenApply(BefHttpResponse::success)
                .exceptionally(e -> {
                    log.error("Error while get user top streak.", e);
                    return ExceptionUtils.rethrow(e);
                });
    }
}
