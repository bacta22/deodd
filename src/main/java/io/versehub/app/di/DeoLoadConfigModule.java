package io.versehub.app.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.hazelcast.config.XmlConfigBuilder;
import io.versehub.common.preboot.YamlConfigReader;
import io.versehub.infrastructure.common.eventbus.VertxProducer;
import io.versehub.infrastructure.config.DeoAppConfig;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Properties;

@Slf4j
public class DeoLoadConfigModule extends AbstractModule {

    private static final String DEFAULT_APP_CONFIG = "config/app.yaml";

    @Override
    protected void configure() {
//        bind(PremiumProvider.class).to(PremiumProviderImpl.class);
    }
    /*
    @Singleton
     @Provides
     Authenticator authenticator(BefJackpotAppConfig config) {
         var loginAlgorithm = readKeypairRSA256(config.getAuth().getLoginKeypair());
         return VhJwtAuthenticator.builder()
                 .defaultAlgorithm(loginAlgorithm)
                 .loginAlgorithm(loginAlgorithm)
                 .accessTokenExpiration(ofMillis(config.getAuth().getAccessTokenExpirationMillis()))
                 .build();
     }

     @Singleton
     @Provides
     Authorizator authorizator(BefJackpotAppConfig config) {
         List<String> ids = new ArrayList<>();
         if (!Strings.isNullOrEmpty(config.getAdminUsers())) {
             ids.addAll(Arrays.stream(config.getAdminUsers().toLowerCase().replaceAll("\"", "").split(",")).toList());
         }
         return new Authorizator(ids);
     }

     private Algorithm readKeypairRSA256(BefJackpotAppConfig.BefJackpotKeyPairConfig keypair) {
         return Algorithm.RSA256(readPublicKey(keypair.getPublicKey()),
                 readPrivateKey(keypair.getPrivateKey()));
     }
    */
    @Provides
    @Singleton
    DeoAppConfig loadAppConfig() {
//        URL resource = getClass().getClassLoader().getResource(DEFAULT_APP_CONFIG);
        var res = loadConfig(DEFAULT_APP_CONFIG);
        log.info(res.getHazelcast().getFilePath());
        log.info(res.getEventBus().getHost());
        return res;
    }

    @SneakyThrows
    private static DeoAppConfig loadConfig(String appConfig) {
//        log.info("reading config from {}", appConfig);
        return YamlConfigReader.forType(DeoAppConfig.class).readYaml(new File(appConfig));
    }

    private static final String CONFIG_HAZELCAST_LOCAL_XML = "config/hazelcast-local.xml";

    @SneakyThrows
    @Provides
    @Singleton
    public Vertx vertx(DeoAppConfig config) {
        String hzFilePath = CONFIG_HAZELCAST_LOCAL_XML;
        Properties hzProps = null;

        var hzConfig = config.getHazelcast();
        if (hzConfig != null) {
            var filePath = hzConfig.getFilePath();
            if (filePath != null && !filePath.isBlank()) {
                hzFilePath = filePath;
            }
            var props = hzConfig.getProps();
            if (props != null && props.size() > 0) {
                hzProps = props;
            }
        }
//        URL resource = getClass().getClassLoader().getResource(hzFilePath);

        var hzConfigBuilder = new XmlConfigBuilder(hzFilePath);
        if (hzProps != null) {
            hzConfigBuilder.setProperties(hzProps);
        }

        var clusterManager = new HazelcastClusterManager(hzConfigBuilder.build());
        var options = new VertxOptions() //
                .setClusterManager(clusterManager) //
                .setBlockedThreadCheckInterval(1000_000);

        var eventBusConfig = config.getEventBus();
        if (eventBusConfig != null) {
            var eventBusOptions = new EventBusOptions();

            var host = eventBusConfig.getHost();
            if (host != null) {
                eventBusOptions.setHost(host);
            }

            int port = eventBusConfig.getPort();
            if (port > 0) {
                eventBusOptions.setPort(port);
            }

            options.setEventBusOptions(eventBusOptions);
        }
        return Vertx.clusteredVertx(options).toCompletionStage().toCompletableFuture().get();
    }

    @Singleton
    @Provides
    VertxProducer vertxProducer(Vertx vertx) {
        return VertxProducer.of(vertx);
    }

}
