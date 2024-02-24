package com.saminassim.cvm.dto.response;

import com.saminassim.cvm.entity.Message;
import lombok.Data;

import java.util.List;

@Data
public class ConversationsResponse {
    private String id;
    private List<Message> messages;
    private UserResponse otherUser;
}
