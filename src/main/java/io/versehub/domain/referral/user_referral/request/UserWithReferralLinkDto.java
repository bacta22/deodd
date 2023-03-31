package io.versehub.domain.referral.user_referral.request;


import io.versehub.domain.user.model.UserProfile;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserWithReferralLinkDto {
    UserProfile user;
    String referralLink;

}
