package com.knightlia.particle.sandbox.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NicknameRequest {

    @NotBlank(message = "error.nickname.required")
    @Pattern(regexp = "^\\S+$", message = "error.nickname.invalid")
    private String nickname;
}
