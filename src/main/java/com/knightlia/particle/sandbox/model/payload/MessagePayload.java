package com.knightlia.particle.sandbox.model.payload;

public record MessagePayload(
    MessageType messageType,
    String sender,
    String message,
    long timestamp
) {
}
