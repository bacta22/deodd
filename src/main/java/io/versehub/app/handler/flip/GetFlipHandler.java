package io.versehub.app.handler.flip;

import com.google.inject.Inject;
import io.versehub.app.AbstractHandler;
import io.versehub.bef.commons.http.response.BefHttpResponse;
import io.versehub.common.aapi.annotation.RegisterHandler;
import io.versehub.common.aapi.handler.VhProtocol;
import io.versehub.common.aapi.message.VhHttpMethod;
import io.versehub.common.aapi.message.http.VhHttpRequest;
import io.versehub.domain.flip.FlipRepository;
import io.versehub.domain.flip.model.Flip;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.concurrent.CompletionStage;

@Slf4j
@RegisterHandler(protocol = VhProtocol.HTTP, method = VhHttpMethod.GET, endpoint = "/flip/:id")
public class GetFlipHandler extends AbstractHandler<Flip> {

    @Inject
    FlipRepository flipRepository;

    @Override
    protected CompletionStage<BefHttpResponse<Flip>> doHandle(VhHttpRequest request) {
       Integer flipId = Integer.valueOf(request.getPathParam("id"));
       return flipRepository.getById(flipId)
               .thenApply(BefHttpResponse::success)
               .exceptionally(e -> {
                   log.error("Error while get flip by Id", e);
                   return ExceptionUtils.rethrow(e);
               });
    }
}
