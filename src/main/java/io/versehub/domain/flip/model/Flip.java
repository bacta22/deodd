package io.versehub.domain.flip.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
@Data
@Builder
public class Flip {
    Integer flipId;
    String wallet;
    String userName;
    Integer avatarId;
    BigDecimal amount;
    Integer flipChoice;
    Integer flipResult;
    Integer tossPoint;
    Integer jackPortReward;
    Integer tokenId;
    Integer typeId;
    Long time;
}
