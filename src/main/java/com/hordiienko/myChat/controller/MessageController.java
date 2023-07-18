package com.hordiienko.myChat.controller;

import com.hordiienko.myChat.dto.LastMessageDto;
import com.hordiienko.myChat.dto.MessageDto;
import com.hordiienko.myChat.dto.MessageSetDto;
import com.hordiienko.myChat.entity.Message;
import com.hordiienko.myChat.entity.User;
import com.hordiienko.myChat.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Controller
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @MessageMapping("postMessage")
    public Mono<Message> postMessage(MessageDto messageDto, @AuthenticationPrincipal User user) {
        return messageService.saveMessage(messageDto, user);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @MessageMapping("putMessage")
    public Mono<Message> putMessage(MessageDto messageDto, @AuthenticationPrincipal User user) {
        return messageService.changeMessage(messageDto, user);
    }

    @MessageMapping("messages")
    public Flux<Message> lastMessages() {
        return messageService.getLatestMessages();
    }

    @MessageMapping("tempMethodLastMessages")
    public Mono<MessageSetDto> tempLastMessages() {
        return messageService.tempRealizationGetLatestMessages();
    }

    @MessageMapping("tempMethodUpdateChat")
    public Mono<MessageSetDto> tempUpdateChat(LastMessageDto lastMessage){
        MessageSetDto emptySet = new MessageSetDto();
        emptySet.setMessages(Collections.emptySet());
        return messageService.tempRealizationGetAllAfterMessage(lastMessage.getLastMessageId())
                .switchIfEmpty(Mono.just(emptySet));
    }
}
