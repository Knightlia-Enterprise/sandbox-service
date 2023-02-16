package com.knightlia.particle.sandbox.model.payload;

import java.util.Collection;

public record UserListPayload(MessageType messageType, Collection<String> userList) {
}
