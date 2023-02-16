package com.knightlia.particle.sandbox.controller;

import com.knightlia.particle.sandbox.model.RequiresToken;
import com.knightlia.particle.sandbox.model.request.MessageRequest;
import com.knightlia.particle.sandbox.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequiresToken
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/message")
    public void sendMessage(@RequestHeader String token, @RequestBody @Valid MessageRequest messageRequest) {
        log.debug("New message: {}", messageRequest);
        messageService.sendMessage(token, messageRequest);
    }
}
