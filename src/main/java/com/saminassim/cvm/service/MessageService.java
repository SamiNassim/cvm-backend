package com.saminassim.cvm.service;

import com.saminassim.cvm.entity.Conversation;
import com.saminassim.cvm.entity.Message;

public interface MessageService {
    Conversation getOrCreateConversation(String receiverId);
    Message sendMessage(String messageContent, String conversationId);
    void deleteMessage(String messageId);
}
