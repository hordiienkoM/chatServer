package com.hordiienko.myChat.repository;

import com.hordiienko.myChat.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

public interface MessageRepository extends ReactiveMongoRepository<Message, String> {
    Flux<Message> findAllByCreationDateTimeBeforeOrderByCreationDateTimeDesc(LocalDateTime dateTime, Pageable pageable);
}
