package io.versehub.app.handler.user;

import com.google.inject.Inject;
import io.versehub.app.AbstractHandler;
import io.versehub.bef.commons.http.response.BefHttpResponse;
import io.versehub.common.aapi.annotation.RegisterHandler;
import io.versehub.common.aapi.handler.VhProtocol;
import io.versehub.common.aapi.message.VhHttpMethod;
import io.versehub.common.aapi.message.http.VhHttpRequest;
import io.versehub.domain.user.UserRepository;
import io.versehub.domain.user.model.UserProfile;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.core.util.JsonUtils;

import java.util.concurrent.CompletionStage;

@Slf4j
@RegisterHandler(protocol = VhProtocol.HTTP, method = VhHttpMethod.PUT, endpoint = "/users/nonce/generate")
public class GenerateNonceByAddressHandler extends AbstractHandler<UserProfile> {

    @Inject
    UserRepository userRepository;

    @Override
    protected CompletionStage<BefHttpResponse<UserProfile>> doHandle(VhHttpRequest request) {
        var updateUserProfile = request.getBody().toJsonObject().mapTo(UserProfile.class);
        return userRepository.generateNonceByAddress(updateUserProfile)
                .thenApply(BefHttpResponse::success)
                .exceptionally(e -> {
                    log.error("Error while get user top streak.", e);
                    return ExceptionUtils.rethrow(e);
                });
    }
}
