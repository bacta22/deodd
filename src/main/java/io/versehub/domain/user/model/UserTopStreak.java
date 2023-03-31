package io.versehub.domain.user.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class UserTopStreak {
    String wallet;
    String userName;
    Integer avatarId;
    Integer maxStreakLength;
    BigDecimal streakAmount;
    Long time;
}
