package com.saminassim.cvm.service.impl;

import com.saminassim.cvm.dto.response.ConversationResponse;
import com.saminassim.cvm.dto.response.MessageResponse;
import com.saminassim.cvm.dto.response.UserResponse;
import com.saminassim.cvm.entity.Conversation;
import com.saminassim.cvm.entity.Message;
import com.saminassim.cvm.entity.User;
import com.saminassim.cvm.exception.MessageCannotBeDeletedException;
import com.saminassim.cvm.exception.MessageCannotBeSentException;
import com.saminassim.cvm.repository.ConversationRepository;
import com.saminassim.cvm.repository.MessageRepository;
import com.saminassim.cvm.repository.UserRepository;
import com.saminassim.cvm.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
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

    @Override
    public Message sendMessage(String messageContent, String conversationId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByEmail(authentication.getName()).orElseThrow();

        Conversation selectedConversation = conversationRepository.findById(conversationId).orElseThrow();

        if(selectedConversation.getUserOne().equals(currentUser) || selectedConversation.getUserTwo().equals(currentUser)){

            Message newMessage = new Message();

            newMessage.setContent(messageContent);
            newMessage.setConversation(selectedConversation);
            newMessage.setUser(currentUser);

            List<Message> conversationMessages = selectedConversation.getMessages();
            conversationMessages.add(newMessage);

            List<Message> currentUserMessages = currentUser.getMessages();
            currentUserMessages.add(newMessage);

            selectedConversation.setMessages(conversationMessages);
            currentUser.setMessages(currentUserMessages);

            return messageRepository.save(newMessage);

        }

        throw new MessageCannotBeSentException("You are not part of this conversation");
    }


    @Override
    public void deleteMessage(String messageId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByEmail(authentication.getName()).orElseThrow();

        Message selectedMessage = messageRepository.findById(messageId).orElseThrow();

        if(!selectedMessage.getUser().equals(currentUser)){
            throw new MessageCannotBeDeletedException("You can't delete this message !");
        }

        messageRepository.deleteById(messageId);
    }

    @Override
    public List<ConversationResponse> getAllConversations() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByEmail(authentication.getName()).orElseThrow();

        List<Conversation> conversationsStarted = conversationRepository.findConversationsByUserOneId(currentUser.getId());
        List<Conversation> conversationsReceived = conversationRepository.findConversationsByUserTwoId(currentUser.getId());

        List<ConversationResponse> conversationResponseList = new ArrayList<>();
        List<MessageResponse> messageResponseList = new ArrayList<>();

        for (Conversation conversation : conversationsStarted){
            UserResponse otherUser = new UserResponse();
            otherUser.setCountry(conversation.getUserTwo().getProfile().getCountry() != null ? conversation.getUserTwo().getProfile().getCountry() : null);
            otherUser.setRegion(conversation.getUserTwo().getProfile().getRegion() != null ? conversation.getUserTwo().getProfile().getRegion() : null);
            otherUser.setDateOfBirth(conversation.getUserTwo().getProfile().getDateOfBirth() != null ? String.valueOf(conversation.getUserTwo().getProfile().getDateOfBirth()) : null);
            otherUser.setRelation(conversation.getUserTwo().getProfile().getRelation() != null ? conversation.getUserTwo().getProfile().getRelation().getDisplayName() : null);
            otherUser.setBio(conversation.getUserTwo().getProfile().getBio());
            otherUser.setImageUrl(conversation.getUserTwo().getProfile().getImageUrl());

            ConversationResponse newConv = new ConversationResponse();
            newConv.setId(conversation.getId());

            for(Message message : conversation.getMessages()){
                MessageResponse messageResponse = new MessageResponse();
                messageResponse.setId(message.getId());
                messageResponse.setContent(message.getContent());
                messageResponse.setSenderId(message.getUser().getId());
                messageResponse.setCreatedAt(String.valueOf(message.getCreatedAt()));
                messageResponse.setUpdatedAt(String.valueOf(message.getUpdatedAt()));

                messageResponseList.add(messageResponse);
            }

            newConv.setMessages(messageResponseList);
            newConv.setOtherUser(otherUser);

            conversationResponseList.add(newConv);
        }

        for (Conversation conversation : conversationsReceived){
            UserResponse otherUser = new UserResponse();

            otherUser.setCountry(conversation.getUserOne().getProfile().getCountry() != null ? conversation.getUserOne().getProfile().getCountry() : null);
            otherUser.setRegion(conversation.getUserOne().getProfile().getRegion() != null ? conversation.getUserOne().getProfile().getRegion() : null);
            otherUser.setDateOfBirth(conversation.getUserOne().getProfile().getDateOfBirth() != null ? String.valueOf(conversation.getUserOne().getProfile().getDateOfBirth()) : null);
            otherUser.setRelation(conversation.getUserOne().getProfile().getRelation() != null ? conversation.getUserOne().getProfile().getRelation().getDisplayName() : null);
            otherUser.setBio(conversation.getUserOne().getProfile().getBio());
            otherUser.setImageUrl(conversation.getUserOne().getProfile().getImageUrl());

            ConversationResponse newConv = new ConversationResponse();

            for(Message message : conversation.getMessages()){
                MessageResponse messageResponse = new MessageResponse();
                messageResponse.setId(message.getId());
                messageResponse.setContent(message.getContent());
                messageResponse.setSenderId(message.getUser().getId());
                messageResponse.setCreatedAt(String.valueOf(message.getCreatedAt()));
                messageResponse.setUpdatedAt(String.valueOf(message.getUpdatedAt()));

                messageResponseList.add(messageResponse);
            }

            newConv.setId(conversation.getId());
            newConv.setMessages(messageResponseList);
            newConv.setOtherUser(otherUser);

            conversationResponseList.add(newConv);
        }

        return conversationResponseList;

    }
}
