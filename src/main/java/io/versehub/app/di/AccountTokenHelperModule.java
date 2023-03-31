package io.versehub.app.di;

import com.auth0.jwt.algorithms.Algorithm;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.versehub.app.handler.user.authen.support.AccountTokenHelper;
import io.versehub.infrastructure.config.DeoAppConfig;

import static io.versehub.common.token.support.KeypairHelper.readPrivateKey;
import static io.versehub.common.token.support.KeypairHelper.readPublicKey;
import static java.time.Duration.ofMillis;

public class AccountTokenHelperModule extends AbstractModule {
    @Provides
    AccountTokenHelper initAuthenticator(DeoAppConfig config) {
        var authConfig = config.getAuth();
        var loginAlgorithm = readKeypairRSA256(authConfig.getLoginKeypair());
        return AccountTokenHelper.builder()
            .defaultAlgorithm(loginAlgorithm)
            .loginAlgorithm(loginAlgorithm)
            .accessTokenExpiration(ofMillis(authConfig.getAccessTokenExpirationMillis()))
            .refreshTokenExpiration(ofMillis(authConfig.getRefreshTokenExpirationMillis()))
            .build();
    }

    private Algorithm readKeypairRSA256(DeoAppConfig.DeoKeyPairConfig keypair) {
        return Algorithm.RSA256(readPublicKey(keypair.getPublicKey()),
            readPrivateKey(keypair.getPrivateKey()));
    }
}
