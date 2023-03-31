package io.versehub.domain.user.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class UserTopNetGains {
    String wallet;
    String userName;
    Integer avatarId;
    BigDecimal netGains;
}
