package com.knightlia.particle.sandbox.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageRequest {

    @NotBlank(message = "error.message.required")
    private String message;

    @Positive(message = "error.timestamp.invalid")
    private long timestamp;
}
