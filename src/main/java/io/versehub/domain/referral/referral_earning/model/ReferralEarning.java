package io.versehub.domain.referral.referral_earning.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.versehub.infrastructure.common.utils.LocalDateDeserializer;
import io.versehub.infrastructure.common.utils.LocalDateSerializer;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReferralEarning {
    Integer id;
    String userWalletGrandfather;
    String userNameGrandfather;
    String userWalletFather;
    String userNameFather;
    String userWalletReferred;
    String userNameReferred;
    BigDecimal rewardGrandfatherClaimed;
    BigDecimal rewardGrandfatherUnclaimed;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    LocalDate expiredDateForGrandfather;
    BigDecimal rewardFatherClaimed;
    BigDecimal rewardFatherUnclaimed;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    LocalDate expiredDateForFather;
}
