package com.hordiienko.myChat.dto;

import com.hordiienko.myChat.entity.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class MessageSetDto {
    private Set<Message> messages;
}
