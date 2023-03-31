package io.versehub.app.handler.user.authen;

import com.google.inject.Inject;
import io.versehub.app.AbstractHandler;
import io.versehub.app.handler.user.authen.request.ConnectWalletRequest;
import io.versehub.app.handler.user.authen.request.GrantType;
import io.versehub.app.handler.user.authen.response.LoginResponse;
import io.versehub.bef.commons.exception.Errors;
import io.versehub.bef.commons.http.response.BefHttpResponse;
import io.versehub.common.aapi.annotation.RegisterHandler;
import io.versehub.common.aapi.handler.VhProtocol;
import io.versehub.common.aapi.message.VhHttpMethod;
import io.versehub.common.aapi.message.http.VhHttpRequest;

import java.util.concurrent.CompletionStage;
@RegisterHandler(protocol = VhProtocol.HTTP, method = VhHttpMethod.POST, endpoint = "/auth/token")
public class LoginHandler extends AbstractHandler<LoginResponse> {

    @Inject
    AccountService accountService;

    @Override
    protected CompletionStage<BefHttpResponse<LoginResponse>> doHandle(VhHttpRequest request) {
        var grantType = request.getQueryParam("grant_type");
         if (GrantType.wallet_address.name().equals(grantType)) {
            var connectWalletRequest = deserializeRequest(request, ConnectWalletRequest.class);
            return accountService.connectWallet(connectWalletRequest);
        } else if (GrantType.refresh.name().equals(grantType)) {
            var refreshToken = extractTokenFromRequest(request);
            return accountService.refreshAccessToken(refreshToken);
        } else {
            return failedStageByErrors(Errors.INVALID_GRANT_TYPE);
        }
    }
}
