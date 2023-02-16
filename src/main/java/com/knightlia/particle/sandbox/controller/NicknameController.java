package com.knightlia.particle.sandbox.controller;

import com.knightlia.particle.sandbox.model.RequiresToken;
import com.knightlia.particle.sandbox.model.request.NicknameRequest;
import com.knightlia.particle.sandbox.service.NicknameService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequiresToken
public class NicknameController {

    private final NicknameService nicknameService;

    @PostMapping("/nickname")
    public Collection<String> setNickname(@RequestHeader String token, @RequestBody @Valid NicknameRequest nicknameRequest) {
        log.info("Nickname request: {}", nicknameRequest);
        return nicknameService.setNickname(token, nicknameRequest.getNickname());
    }
}
