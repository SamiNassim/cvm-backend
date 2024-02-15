package com.saminassim.cvm.service;

import com.saminassim.cvm.entity.Conversation;

public interface MessageService {
    Conversation getOrCreateConversation(String receiverId);

}
