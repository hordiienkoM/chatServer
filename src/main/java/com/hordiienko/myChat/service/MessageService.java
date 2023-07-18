package com.hordiienko.myChat.service;

import com.hordiienko.myChat.dto.MessageDto;
import com.hordiienko.myChat.dto.MessageSetDto;
import com.hordiienko.myChat.entity.Message;
import com.hordiienko.myChat.entity.User;
import com.hordiienko.myChat.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;
import reactor.util.context.Context;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.HashSet;

@Service
public class MessageService {
    @Autowired
    private final MessageRepository messageRepository;
    private final Sinks.Many<Message> messageSink;
    private Flux<Message> messageStream;

    @PostConstruct
    private Mono<Void> toDelete() {
        messageRepository.findAll()
                .flatMap(message -> messageRepository.delete(message));
        return Mono.empty();
    }

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
        this.messageSink = Sinks.many().replay().limit(1000);
        this.messageStream = messageSink
                .asFlux()
                .sort((m1, m2) -> m2.getCreationDateTime().compareTo(m1.getCreationDateTime()))
                .limitRate(100)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PostConstruct
    private void init() {
        this.get100LatestMessages()
                .subscribe(messageSink::tryEmitNext);
    }

    public Flux<Message> getLatestMessages() {
        return this.messageStream;
    }

//
//    public Flux<Message> get100PreviousMessages(String lastMessageId) {
//        return messageRepository.findById(lastMessageId)
//                .flatMapMany(lastMessage -> {
//                    LocalDateTime lastMessageCreationDateTime = lastMessage.getCreationDateTime();
//                    Pageable pageable = PageRequest.of(0, 100);
//                    return messageRepository.findAllByCreationDateTimeBeforeOrderByCreationDateTimeDesc(
//                            lastMessageCreationDateTime, pageable);
//                });
//    }
//
    public Mono<Message> saveMessage(MessageDto messageDto, User user) {
        return Mono.defer(() -> {
            Message message = Message.builder()
                    .username(user.getUsername())
                    .topic(messageDto.getTittle())
                    .text(messageDto.getText())
                    .creationDateTime(LocalDateTime.now())
                    .build();

            return messageRepository.save(message)
                    .doOnSuccess(saved -> messageSink.emitNext(saved, Sinks.EmitFailureHandler.FAIL_FAST));
        });
    }

    public Mono<Message> changeMessage(MessageDto message, User user) {
        return messageRepository.findById(message.getId())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Message not found")))
                .flatMap(existingMessage -> {
                    if (existingMessage.getUsername().equals(user.getUsername())) {
                        existingMessage.setText(message.getText());
                        existingMessage.setTopic(message.getTittle());
                        existingMessage.setEdited(true);
                        existingMessage.setEditDateTime(LocalDateTime.now());

                        return messageRepository.save(existingMessage);
                    } else {
                        return Mono.error(new IllegalArgumentException("You are not authorized to edit this message."));
                    }
                });
    }
    private Flux<Message> get100LatestMessages() {
        return messageRepository.findAll(Sort.by(Sort.Direction.DESC, "creationDateTime"))
                .take(100);
    }

    public Mono<MessageSetDto> tempRealizationGetLatestMessages() {
        return get100LatestMessages()
                .collectList()
                .flatMap(messages -> Mono.just(new HashSet<>(messages)))
                .flatMap(setMessages -> Mono.just(new MessageSetDto(setMessages)));
    }

    public Mono<MessageSetDto> tempRealizationGetAllAfterDate(LocalDateTime creationDate) {
        LocalDateTime afterTime = creationDate.plusNanos(1);
        return messageRepository.findAllAfterCreationDateTime(afterTime)
                .switchIfEmpty(Mono.empty())
                .collectList()
                .flatMap(messages -> Mono.just(new HashSet<>(messages)))
                .flatMap(setMessages -> Mono.just(new MessageSetDto(setMessages)));
    }
}
