package io.versehub.infrastructure.config;

import lombok.Getter;

@Getter
public class DeoDatabaseConfig {

    private String hostAddresses;
    private int port;
    private String name;
    private String user;
    private String password;
    private String connectionScheme;
    private String useSSL;
    private String pemRootCaPath;
}
