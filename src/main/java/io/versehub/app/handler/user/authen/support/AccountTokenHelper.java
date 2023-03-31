package io.versehub.app.handler.user.authen.support;

import com.auth0.jwt.algorithms.Algorithm;
import io.versehub.common.token.JwtHelper;
import io.versehub.common.token.Token;
import io.versehub.common.token.TokenBuilder;
import io.versehub.common.token.TokenValidator;
import lombok.Builder;
import lombok.NonNull;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import static java.time.Duration.ofDays;
import static java.time.Duration.ofMinutes;

public class AccountTokenHelper {

    private static final String TYPE = "type";
    private static final String SESSION_ID = "session_id";
    private static final String WALLET = "wallet";
    private static final String EXPIRED_AT = "expired_at";
    private static final String SECRET = "secret";

    private static final String USER_ID = "user_id";

    private static final String ACCESS_TOKEN_TYPE = "access_token";
    private static final Function<Token, Boolean> ACCESS_TOKEN_TYPE_CHECK = typeCheck(ACCESS_TOKEN_TYPE);

    private static final String REFRESH_TOKEN_TYPE = "refresh_token";
    private static final Function<Token, Boolean> REFRESH_TOKEN_TYPE_CHECK = typeCheck(REFRESH_TOKEN_TYPE);

    private static final Function<Token, Boolean> DEFAULT_EXPITY_CHECK = notExpiredCheck();
    private static final Function<Token, Boolean> DEFAULT_HAS_SESSION_ID_CHECK = hasSessionId();

    private final @NonNull Duration accessTokenExpiration;
    private final @NonNull Duration refreshTokenExpiration;

    private final @NonNull JwtHelper otpHelper;
    private final @NonNull JwtHelper loginHelper;

    @Builder
    private AccountTokenHelper( //
                                Algorithm defaultAlgorithm, //
                                Algorithm otpAlgorithm, //
                                Algorithm loginAlgorithm, //
                                Duration resetExpiration, //
                                Duration normalExpiration, //
                                Duration verifyEmailExpiration, Duration otpWaitingExpiration, //
                                Duration totpSetupExpiration, //
                                Duration completeProfileExpiration, //
                                Duration accessTokenExpiration, Duration refreshTokenExpiration) {

        this.otpHelper = new JwtHelper(otpAlgorithm != null ? otpAlgorithm : defaultAlgorithm);
        this.loginHelper = new JwtHelper(loginAlgorithm != null ? loginAlgorithm : defaultAlgorithm);
        this.accessTokenExpiration = accessTokenExpiration != null ? accessTokenExpiration : ofMinutes(3);
        this.refreshTokenExpiration = refreshTokenExpiration != null ? refreshTokenExpiration : ofDays(60);
    }


    // ---------------------------------------------------------------------------
    private static Function<Token, Boolean> typeCheck(@NonNull String expectedType) {
        return token -> expectedType.equals(token.getString(TYPE));
    }


    private static Function<Token, Boolean> notExpiredCheck() {
        return token -> {
            var expiredAt = token.getEpochMilliAsInstant(EXPIRED_AT);
            if (expiredAt == null)
                return false;
            var now = Instant.now();
            return expiredAt.isAfter(now);
        };
    }

    private static Function<Token, Boolean> hasSessionId() {
        return exists(SESSION_ID);
    }

    private static Function<Token, Boolean> exists(String name) {
        return token -> token.getClaim(name) != null;
    }

    private static CompletionStage<Token> validateDefault(JwtHelper helper, String token,
                                                          Function<Token, Boolean>... moreChecks) {
        return helper //
                .decodeAsync(token) //
                .thenApply(Token::toValidator) //
                .thenApply(v -> v //
                        .addChecks(moreChecks) //
                        .addCheck(DEFAULT_EXPITY_CHECK)) //
                .thenApply(TokenValidator::validateAll) //
                .thenApply(TokenValidator::getToken);
    }

    private static CompletionStage<Token> validateBySessionId(JwtHelper helper, String token,
                                                              Function<Token, Boolean>... moreChecks) {
        return helper //
                .decodeAsync(token) //
                .thenApply(Token::toValidator) //
                .thenApply(v -> v //
                        .addChecks(moreChecks) //
                        .addCheck(DEFAULT_EXPITY_CHECK) //
                        .addCheck(DEFAULT_HAS_SESSION_ID_CHECK)) //
                .thenApply(TokenValidator::validateAll) //
                .thenApply(TokenValidator::getToken);
    }

    private static CompletionStage<Token> validateBySessionIdAndAccountId(JwtHelper helper, String token,
                                                                          Function<Token, Boolean>... moreChecks) {
        return helper //
                .decodeAsync(token) //
                .thenApply(Token::toValidator) //
                .thenApply(v -> v //
                        .addChecks(moreChecks) //
                        .addCheck(DEFAULT_EXPITY_CHECK) //
                        .addCheck(DEFAULT_HAS_SESSION_ID_CHECK)
                ) //
                .thenApply(TokenValidator::validateAll) //
                .thenApply(TokenValidator::getToken);
    }

    private static TokenBuilder tokenBuilder(JwtHelper helper, String type, UUID userId, Duration expiredAfter) {
        return tokenBuilder(helper) //
                .withClaim(TYPE, type) //
                .withClaim(USER_ID, userId) //
                .withClaim(EXPIRED_AT, Instant.now().plus(expiredAfter));
    }

    private static TokenBuilder tokenBuilderBySessionId(JwtHelper helper, String type, UUID sessionId,
                                                        Duration expiredAfter) {
        return tokenBuilder(helper) //
                .withClaim(TYPE, type) //
                .withClaim(SESSION_ID, sessionId) //
                .withClaim(EXPIRED_AT, Instant.now().plus(expiredAfter));
    }

    private static TokenBuilder tokenBuilderBySessionIdAndAccountId(JwtHelper helper, String type, UUID sessionId,
                                                                    @NonNull String wallet, Duration expiredAfter) {
        return tokenBuilder(helper) //
                .withClaim(TYPE, type) //
                .withClaim(SESSION_ID, sessionId) //
                .withClaim(WALLET, wallet) //
                .withClaim(EXPIRED_AT, Instant.now().plus(expiredAfter));
    }

    private static TokenBuilder tokenBuilderBySessionIdAndApplicationId(JwtHelper helper, String type, UUID sessionId,
                                                                        Duration expiredAfter) {
        return tokenBuilder(helper) //
                .withClaim(TYPE, type) //
                .withClaim(SESSION_ID, sessionId) //
                .withClaim(EXPIRED_AT, Instant.now().plus(expiredAfter));
    }

    private static TokenBuilder tokenBuilder(JwtHelper helper) {
        return helper.tokenBuilder();
    }

    private static String buildDefaultToken(JwtHelper helper, String type, UUID userId, Duration expiredAfter) {
        return tokenBuilder(helper, type, userId, expiredAfter).build();
    }


    public String generateAccessTokenBySessionIdAndAccountId(@NonNull UUID sessionId, @NonNull String wallet) {
        return tokenBuilderBySessionIdAndAccountId(loginHelper, ACCESS_TOKEN_TYPE, sessionId, wallet,
                accessTokenExpiration).build();
    }

    public String generateApplicationAccessToken(@NonNull UUID sessionId) {
        return tokenBuilderBySessionIdAndApplicationId(loginHelper, ACCESS_TOKEN_TYPE, sessionId, accessTokenExpiration)
                .build();
    }

    public CompletionStage<Token> validateAccessToken(String token) {
        return validateBySessionIdAndAccountId(loginHelper, token, ACCESS_TOKEN_TYPE_CHECK);
    }

    public String generateRefreshTokenBySessionId(UUID sessionId) {
        return tokenBuilderBySessionId(loginHelper, REFRESH_TOKEN_TYPE, sessionId, refreshTokenExpiration).build();
    }

    public CompletionStage<Token> validateRefreshToken(String token) {
        return validateBySessionId(loginHelper, token, REFRESH_TOKEN_TYPE_CHECK);
    }
}
