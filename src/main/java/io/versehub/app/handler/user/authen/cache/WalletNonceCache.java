package io.versehub.app.handler.user.authen.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class WalletNonceCache {
    private static ConcurrentMap<String, String> walletIdNonceMap = new ConcurrentHashMap<>();

    private WalletNonceCache() {
    }

    public static String get(String key) {
        return walletIdNonceMap.get(key);
    }

    public static void put(String key, String value) {

        walletIdNonceMap.put(key, value);
    }
}
