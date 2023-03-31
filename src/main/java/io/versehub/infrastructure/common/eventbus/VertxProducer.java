package io.versehub.infrastructure.common.eventbus;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "of")
public class VertxProducer {

    private final Vertx vertx;

    public void publish(String topic, String message) {
        vertx.eventBus().publish(topic, message);
    }

    public void send(String topic, String message) {
        vertx.eventBus().send(topic, message);
    }

    public <T>Future<Message<T>> request(String topic, String message) {
        return vertx.eventBus().request(topic, message);
    }

    public void publish(String topic, String message, DeliveryOptions options) {
        vertx.eventBus().publish(topic, message, options);
    }

    public void send(String topic, String message, DeliveryOptions options) {
        vertx.eventBus().send(topic, message, options);
    }

    public <T>Future<Message<T>> request(String topic, String message, DeliveryOptions options) {
        return vertx.eventBus().request(topic, message, options);
    }
}
