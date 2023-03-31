package io.versehub.app.handler.user;

import com.google.inject.Inject;
import io.versehub.app.AbstractHandler;
import io.versehub.bef.commons.http.response.BefHttpResponse;
import io.versehub.common.aapi.annotation.RegisterHandler;
import io.versehub.common.aapi.handler.VhProtocol;
import io.versehub.common.aapi.message.VhHttpMethod;
import io.versehub.common.aapi.message.http.VhHttpRequest;
import io.versehub.domain.user.UserRepository;
import io.versehub.domain.user.model.UserTopStreak;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Collection;
import java.util.concurrent.CompletionStage;

@Slf4j
@RegisterHandler(protocol = VhProtocol.HTTP, method = VhHttpMethod.GET, endpoint = "/topstreak")
public class GetUserTopStreakHandler extends AbstractHandler<Collection<UserTopStreak>> {

    @Inject
    UserRepository userRepository;

    @Override
    protected CompletionStage<BefHttpResponse<Collection<UserTopStreak>>> doHandle(VhHttpRequest request) {
       return userRepository.getUserTopStreak()
               .thenApply(BefHttpResponse::success)
               .exceptionally(e -> {
                   log.error("Error while get user top streak.", e);
                   return ExceptionUtils.rethrow(e);
               });
    }
}
