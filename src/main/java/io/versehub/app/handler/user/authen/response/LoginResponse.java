package io.versehub.app.handler.user.authen.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String refreshToken;
    private String accessToken;
}
