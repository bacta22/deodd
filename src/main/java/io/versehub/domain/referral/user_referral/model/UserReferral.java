package io.versehub.domain.referral.user_referral.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.versehub.infrastructure.common.utils.LocalDateTimeDeserializer;
import io.versehub.infrastructure.common.utils.LocalDateTimeSerializer;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserReferral {
    Integer id;
    String userWalletGrandfather;
    String userNameGrandfather;
    String userWalletFather;
    String userNameFather;
    String userWalletReferred;
    String userNameReferred;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDateTime referralDateTime;
}
