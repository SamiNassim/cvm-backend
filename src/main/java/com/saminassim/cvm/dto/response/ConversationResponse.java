package com.saminassim.cvm.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class ConversationResponse {
    private String id;
    private List<MessageResponse> messages;
    private UserResponse currentUser;
    private Integer currentUserUnread;
    private UserResponse otherUser;
    private Integer otherUserUnread;
    private String createdAt;
    private String updatedAt;
}
