package io.versehub.app.handler.user.authen.request;

import io.versehub.app.handler.user.authen.model.AddressWithNonce;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class ConnectWalletRequest {
    private String signature;
    private String wallet;
}
