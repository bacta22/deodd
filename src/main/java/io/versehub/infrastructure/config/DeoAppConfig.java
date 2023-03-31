package io.versehub.infrastructure.config;

import lombok.Getter;
import lombok.ToString;

import java.util.Properties;

@ToString
@Getter
public class DeoAppConfig {

    private DeoDatabaseConfig database;
    private DeoHttpConfig http;
    private DeoRedisConfig redis;
    private DeoAuthConfig auth;
    private FitXEventBusConfig eventBus;
    private FitXHazelcastConfig hazelcast;
    private String adminUsers;

    @Getter
    public static class DeoKeyPairConfig {
        private String privateKey;
        private String publicKey;
    }

    @Getter
    public static class DeoRedisConfig {
        private String host;
        private int port;
    }

    @Getter
    public static class DeoSendGridConfig {
        private String apiKey;
    }

    @Getter
    @ToString
    public static class DeoAuthConfig {
        private DeoKeyPairConfig loginKeypair;
        private Long accessTokenExpirationMillis;
        private Long refreshTokenExpirationMillis;
        private Long cleanUpSessionMillis;
    }

    @Getter
    public static class FitXEventBusConfig {
        private String host = null;
        private int port = -1;
    }

    @Getter
    public static class FitXHazelcastConfig {
        private String filePath;
        private Properties props;
    }

    @Getter
    public static class FitXKYCConfig {
        private String apiUrl;
        private String apiToken;
        private String username;
        private String password;
    }
}
