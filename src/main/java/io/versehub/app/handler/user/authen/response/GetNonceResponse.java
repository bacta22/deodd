package io.versehub.app.handler.user.authen.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetNonceResponse {
    private String nonce;
}
