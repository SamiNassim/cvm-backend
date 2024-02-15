package com.saminassim.cvm.service.impl;

import com.saminassim.cvm.entity.Conversation;
import com.saminassim.cvm.entity.User;
import com.saminassim.cvm.repository.ConversationRepository;
import com.saminassim.cvm.repository.UserRepository;
import com.saminassim.cvm.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;
    @Override
    public Conversation getOrCreateConversation(String receiverId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> currentUser = userRepository.findByEmail(authentication.getName());
        String currentUserId = currentUser.orElseThrow().getId();
        Optional<User> otherUser = userRepository.findById(receiverId);

        Conversation existingConversation = conversationRepository.findConversationByUserOneIdAndUserTwoId(currentUserId, receiverId) != null ? conversationRepository.findConversationByUserOneIdAndUserTwoId(currentUserId, receiverId) : conversationRepository.findConversationByUserOneIdAndUserTwoId(receiverId, currentUserId);

        if(existingConversation != null) {
            return existingConversation;
        }

        Conversation newConversation = new Conversation();

        newConversation.setUserOne(currentUser.orElseThrow());
        newConversation.setUserTwo(otherUser.orElseThrow());

        return conversationRepository.save(newConversation);
    }
}
