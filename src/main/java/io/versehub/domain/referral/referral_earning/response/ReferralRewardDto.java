package io.versehub.domain.referral.referral_earning.response;

import io.versehub.domain.referral.referral_earning.model.ReferralEarning;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReferralRewardDto {
    List<ReferralEarning> referralEarningRoleGrandfatherList;
    List<ReferralEarning> referralEarningRoleFatherList;
    BigDecimal totalReward;
    BigDecimal claimedReward;
    BigDecimal unclaimedReward;
}
