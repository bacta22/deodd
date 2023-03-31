package io.versehub.app.handler.user.authen;

import com.google.inject.Inject;
import io.versehub.app.handler.user.authen.cache.WalletNonceCache;
import io.versehub.app.handler.user.authen.request.ConnectWalletRequest;
import io.versehub.app.handler.user.authen.response.GetNonceResponse;
import io.versehub.app.handler.user.authen.response.LoginResponse;
import io.versehub.app.handler.user.authen.support.AccountTokenHelper;
import io.versehub.bef.commons.exception.BefException;
import io.versehub.bef.commons.exception.Errors;
import io.versehub.bef.commons.http.response.BefHttpResponse;
import io.versehub.domain.user.UserRepository;
import io.versehub.domain.user.model.UserProfile;
import io.vertx.core.json.JsonObject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.security.SignatureException;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Slf4j
public class DefaultAccountService extends AbstractAuthenticationService implements AccountService {
    private static final int NONCE_LENGTH = 16;

    @Inject
    private UserRepository userRepository;

    @Inject
    private @NonNull AccountTokenHelper accountTokenHelper;

    @Override
    public CompletionStage<BefHttpResponse<String>> ping() {
        return null;
    }

    private CompletionStage<BefHttpResponse<LoginResponse>> userToRefreshTokenResponse(
            UserProfile userProfile) {
        var now = OffsetDateTime.now();
        var expiredAt = now.plusDays(getTimeOutSession());

        return getSessionRepository().createSession(userProfile.getWallet(), new JsonObject(), expiredAt, now)
                .thenApply(sessionId -> BefHttpResponse.success(LoginResponse.builder()
                        .refreshToken(accountTokenHelper.generateRefreshTokenBySessionId(sessionId))
                        .accessToken(accountTokenHelper
                                .generateAccessTokenBySessionIdAndAccountId(sessionId, userProfile.getWallet()))
                        .build()));
    }

    @Override
    public CompletionStage<BefHttpResponse<GetNonceResponse>> getNonce(String walletId) {
        final String nonce = randomBase64String(NONCE_LENGTH);
        WalletNonceCache.put(walletId, nonce);

        return CompletableFuture.completedFuture(GetNonceResponse.builder() //
                        .nonce(nonce)
                        .build()) //
                .thenApply(BefHttpResponse::success);
    }

    private String randomBase64String(int length) {
        byte[] array = new byte[length];
        new Random().nextBytes(array);
        return Base64.getUrlEncoder().encodeToString(array);
    }

    @Override
    public CompletionStage<BefHttpResponse<LoginResponse>> connectWallet(ConnectWalletRequest request) {

        String signature = request.getSignature();
        String publicAddress = request.getWallet();
        if (signature == null || publicAddress == null) {
            return CompletableFuture.failedStage(new BefException(Errors.INVALID_LOGIN_INFO));
        } else {
            return userRepository.getUserByAddress(publicAddress)
                    .thenCompose(userProfile -> {
                        if (Objects.isNull(userProfile)) {
                            return CompletableFuture.failedStage(new BefException(Errors.ACCOUNT_NOT_EXISTS));
                        }
                        try {
                            val r = signature.substring(0, 66);
                            val s = "0x" + signature.substring(66, 130);
                            val v = "0x" + signature.substring(130, 132);
                            val data = new Sign.SignatureData(
                                    Numeric.hexStringToByteArray(v),
                                    Numeric.hexStringToByteArray(r),
                                    Numeric.hexStringToByteArray(s)
                            );
                            String msg = "Sign message to verify you are owner of wallet"/* " + userProfile.getNonce()*/; // concat nonce with message.
                            val keyRecover = Sign.signedPrefixedMessageToKey(/*user.getNonce().toString().getBytes()*/ msg.getBytes(), data);
                            log.info("nonce : " + userProfile.getNonce());
                            Boolean matches = matches(keyRecover, publicAddress);
                            if (!matches) {
                                log.error("User is not allowed to log in.");
                                return CompletableFuture.failedStage(new BefException(Errors.INVALID_LOGIN_INFO));
                            } else {
                                log.info("Login successfully");
                                userProfile.setNonce((int) Math.floor(Math.random() * 10000));
                                return userRepository.generateNonceByAddress(userProfile)
                                        .thenCompose(this::userToRefreshTokenResponse);
                            }
                        } catch (SignatureException | StringIndexOutOfBoundsException exception) {
                            return failedStageByErrors(Errors.UNKNOWN);
                        }
                    });
        }
    }

    private Boolean matches(BigInteger key, String address) {
        val addressRecover = "0x" + Keys.getAddress(key).toLowerCase();
        return addressRecover.equals(address.toLowerCase());
    }

    @Override
    public CompletionStage<BefHttpResponse<LoginResponse>> refreshAccessToken(String refreshToken) {
        return getSessionByRefreshToken(refreshToken)
                .thenApply(sessionBean -> LoginResponse.builder()
                        .accessToken(accountTokenHelper.generateAccessTokenBySessionIdAndAccountId(
                                sessionBean.getId(), sessionBean.getWallet()))
                        .build())
                .thenApply(BefHttpResponse::success)
                .exceptionallyCompose(CompletableFuture::failedStage);
    }

    @Override
    public CompletionStage<BefHttpResponse<Void>> logout(String refreshToken) {
        return getSessionByRefreshToken(refreshToken)
                .thenCompose(sessionBean -> getSessionRepository().disableSession(sessionBean.getId()))
                .thenApply(BefHttpResponse::success)
                .exceptionallyCompose(this::failedStage);
    }

}
