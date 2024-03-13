package com.saminassim.cvm.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class ConversationResponse {
    private String id;
    private List<MessageResponse> messages;
    private UserResponse currentUser;
    private UserResponse otherUser;
}
