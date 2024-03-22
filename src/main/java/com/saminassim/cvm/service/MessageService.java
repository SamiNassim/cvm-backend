package com.saminassim.cvm.service;

import com.saminassim.cvm.dto.response.ConversationResponse;
import com.saminassim.cvm.entity.Message;

import java.util.List;

public interface MessageService {
    ConversationResponse getOrCreateConversation(String receiverId);
    Message sendMessage(String messageContent, String conversationId);
    void deleteMessage(String messageId);
    List<ConversationResponse> getAllConversations();
}
