package io.versehub.domain.user.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.versehub.infrastructure.common.utils.LocalDateTimeDeserializer;
import io.versehub.infrastructure.common.utils.LocalDateTimeSerializer;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfile {
    String wallet;
    String userName;
    Integer avatarId;
    Integer nonce;
    Integer maxStreakLength;
    BigDecimal streakAmount;
    Integer currentStreakLength;
    BigDecimal currentStreakAmount;
    Long blockTimeStamp;
    String referralLink;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDateTime signInDateTime;
}
