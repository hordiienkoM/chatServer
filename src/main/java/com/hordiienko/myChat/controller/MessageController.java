package com.hordiienko.myChat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hordiienko.myChat.dto.LastDateDto;
import com.hordiienko.myChat.dto.MessageDto;
import com.hordiienko.myChat.dto.MessageSetDto;
import com.hordiienko.myChat.entity.Message;
import com.hordiienko.myChat.entity.User;
import com.hordiienko.myChat.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    public Mono<MessageSetDto> tempUpdateChat(LastDateDto lastDate){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        LocalDateTime lastMessageDate = LocalDateTime.parse(lastDate.getLastMessageDate(), formatter);
        MessageSetDto emptySet = new MessageSetDto();
        emptySet.setMessages(Collections.emptySet());
        return messageService.tempRealizationGetAllAfterDate(lastMessageDate)
                .switchIfEmpty(Mono.just(emptySet));
    }
}
