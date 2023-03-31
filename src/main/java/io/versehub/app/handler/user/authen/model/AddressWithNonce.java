package io.versehub.app.handler.user.authen.model;

import lombok.NonNull;

public final class AddressWithNonce {
    private @NonNull String address;
    private @NonNull String nonce;

    public AddressWithNonce() {
    }

    public @NonNull String getAddress() {
        return this.address;
    }

    public @NonNull String getNonce() {
        return this.nonce;
    }
}
