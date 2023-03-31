package io.versehub.app.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.ndl.common.pgpool.config.PgConfig;
import com.ndl.common.pgpool.supplier.PgConnectionSupplier;
import io.versehub.common.uri.ParsedUri;
import io.versehub.domain.flip.FlipRepository;
import io.versehub.domain.referral.referral_earning.ReferralEarningRepository;
import io.versehub.domain.referral.user_referral.UserReferralRepository;
import io.versehub.domain.session.SessionRepository;
import io.versehub.domain.user.UserRepository;
import io.versehub.infrastructure.config.DeoAppConfig;
import io.versehub.infrastructure.flip.PgFlipRepository;
import io.versehub.infrastructure.referral.referral_earning.PgReferralEarningRepository;
import io.versehub.infrastructure.referral.user_referral.PgUserReferralRepository;
import io.versehub.infrastructure.session.PgSessionRepository;
import io.versehub.infrastructure.user.PgUserRepository;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashMap;

@Slf4j
public class PgRepoModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(FlipRepository.class).to(PgFlipRepository.class);
        bind(UserRepository.class).to(PgUserRepository.class);
        bind(UserReferralRepository.class).to(PgUserReferralRepository.class);
        bind(ReferralEarningRepository.class).to(PgReferralEarningRepository.class);
        bind(SessionRepository.class).to(PgSessionRepository.class);
    }

    @Singleton
    @Provides
    PgConnectionSupplier connectionSupplier(DeoAppConfig config, Vertx vertx) {
        var databaseConfig = config.getDatabase();
        final var addresses = Arrays.stream(config.getDatabase() //
                        .getHostAddresses() //
                        .split(",")) //
                .toList();

        final var uriParams = new HashMap<String, String>();
        uriParams.put("maxSize", "64");
        uriParams.put("maxWaitQueueSize", "1024");
        if ("true".equals(databaseConfig.getUseSSL())) {
            uriParams.put("useSSL", databaseConfig.getUseSSL());
            uriParams.put("trustAll", databaseConfig.getUseSSL());
            uriParams.put("pemRootCaPath", databaseConfig.getPemRootCaPath());
        }
        var parseUri = ParsedUri.builder()
                .path(config.getDatabase().getName())
                .user(config.getDatabase().getUser())
                .password(config.getDatabase().getPassword())
                .addresses(addresses)
                .scheme(config.getDatabase().getConnectionScheme())
                .params(uriParams)
                .build();
        var pgConfig = PgConfig.from(parseUri);

        log.info("Create connection to DB using uri {}:", parseUri);
        return PgConnectionSupplier.createSteady(pgConfig, vertx);
    }
}
