package io.versehub.app.handler.user.authen;

import com.google.inject.ImplementedBy;
import io.versehub.app.handler.user.authen.request.ConnectWalletRequest;
import io.versehub.app.handler.user.authen.response.GetNonceResponse;
import io.versehub.app.handler.user.authen.response.LoginResponse;
import io.versehub.bef.commons.http.response.BefHttpResponse;

import java.util.concurrent.CompletionStage;

@ImplementedBy(DefaultAccountService.class)
public interface AccountService {

    CompletionStage<BefHttpResponse<String>> ping();

    CompletionStage<BefHttpResponse<GetNonceResponse>> getNonce(String walletId);

    CompletionStage<BefHttpResponse<LoginResponse>> connectWallet(ConnectWalletRequest request);

    CompletionStage<BefHttpResponse<LoginResponse>> refreshAccessToken(String refreshToken);

    CompletionStage<BefHttpResponse<Void>> logout(String refreshToken);

}
