package com.hordiienko.myChat.dto;

import com.hordiienko.myChat.entity.Message;
import lombok.Data;

import java.util.Set;

@Data
public class SetMessageDto {
    private Set<Message> messages;
}
