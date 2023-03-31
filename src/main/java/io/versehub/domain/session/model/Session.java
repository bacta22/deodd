package io.versehub.domain.session.model;

import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@ToString
@Builder
public class Session {
    private UUID id;
    private @NonNull OffsetDateTime createdAt;
    private @NonNull OffsetDateTime expiredAt;
    private @NonNull JsonObject info;
    private boolean disabled;
    private @NonNull String wallet;
    private @NonNull OffsetDateTime lastActive;
}
