package com.hordiienko.myChat.service;

import com.hordiienko.myChat.dto.MessageDto;
import com.hordiienko.myChat.entity.Message;
import com.hordiienko.myChat.repository.MessageRepository;
import com.hordiienko.myChat.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final RSocketRequester.Builder rsocketRequesterBuilder;
    @Value("${spring.rsocket.server.address}")
    private String rsocketAddr;
    @Value("${spring.rsocket.server.port}")
    private Integer rsocketPort;


    public Flux<Message> get100LatestMessages() {
        return messageRepository.findAll(Sort.by(Sort.Direction.DESC, "creationDateTime"))
                .take(100);
    }

    public Flux<Message> get100PreviousMessages(String lastMessageId) {
        return messageRepository.findById(lastMessageId)
                .flatMapMany(lastMessage -> {
                    LocalDateTime lastMessageCreationDateTime = lastMessage.getCreationDateTime();
                    Pageable pageable = PageRequest.of(0, 100);
                    return messageRepository.findAllByCreationDateTimeBeforeOrderByCreationDateTimeDesc(
                            lastMessageCreationDateTime, pageable);
                });
    }

    public Mono<Message> saveMessage(MessageDto messageDto, UserDetailsImpl user) {
        return Mono.defer(() -> {
            Message message = Message.builder()
                    .userName(user.getUsername())
                    .userId(user.getUserId())
                    .title(messageDto.getTittle())
                    .text(messageDto.getText())
                    .creationDateTime(LocalDateTime.now())
                    .build();

            return messageRepository.save(message);
        }).doOnSuccess(savedMessage -> {
            rsocketRequesterBuilder.tcp(rsocketAddr, rsocketPort)
                    .route("newMessage")
                    .data(savedMessage)
                    .send()
                    .subscribe();
        });
    }

    public Mono<Message> editMessage(MessageDto message, UserDetailsImpl userDetails) {
        return messageRepository.findById(message.getId())
                .flatMap(existingMessage -> {
                    if (existingMessage.getUserId().equals(userDetails.getUserId())) {
                        existingMessage.setText(message.getText());
                        existingMessage.setTitle(message.getTittle());
                        existingMessage.setEdited(true);
                        existingMessage.setEditDateTime(LocalDateTime.now());

                        return messageRepository.save(existingMessage)
                                .doOnSuccess(savedMessage -> {
                                    rsocketRequesterBuilder.tcp(rsocketAddr, rsocketPort)
                                            .route("editedMessage")
                                            .data(savedMessage)
                                            .send()
                                            .subscribe();
                                });
                    } else {
                        return Mono.error(new IllegalArgumentException("You are not authorized to edit this message."));
                    }
                });
    }
}
