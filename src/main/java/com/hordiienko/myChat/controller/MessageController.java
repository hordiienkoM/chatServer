package com.hordiienko.myChat.controller;

import com.hordiienko.myChat.dto.MessageDto;
import com.hordiienko.myChat.entity.Message;
import com.hordiienko.myChat.security.UserDetailsImpl;
import com.hordiienko.myChat.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @PostMapping("/message")
    public Mono<Message> sendNewMessage(@RequestBody MessageDto message, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return messageService.saveMessage(message, userDetails);
    }

    @PutMapping("/message")
    public Mono<Message> editMessage(@RequestBody MessageDto updatedMessage, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return messageService.editMessage(updatedMessage, userDetails);
    }

    @GetMapping()
    public Flux<Message> get100Last() {
        return messageService.get100LatestMessages();
    }

    @GetMapping("/previous")
    public Flux<Message> get100Previous(@RequestParam String messageId) {
        return messageService.get100PreviousMessages(messageId);
    }
}
