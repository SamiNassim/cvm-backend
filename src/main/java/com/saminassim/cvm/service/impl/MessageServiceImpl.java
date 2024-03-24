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
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    @Override
    public ConversationResponse getOrCreateConversation(String receiverId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByEmail(authentication.getName()).orElseThrow();
        User otherUser = userRepository.findById(receiverId).orElseThrow();

        Conversation existingConversation = conversationRepository.findConversationByUserOneIdAndUserTwoId(currentUser.getId(), receiverId) != null ? conversationRepository.findConversationByUserOneIdAndUserTwoId(currentUser.getId(), receiverId) : conversationRepository.findConversationByUserOneIdAndUserTwoId(receiverId, currentUser.getId());

        if(existingConversation != null) {
            User userOne = userRepository.findById(existingConversation.getUserOneId()).orElseThrow();
            User userTwo = userRepository.findById(existingConversation.getUserTwoId()).orElseThrow();

            ConversationResponse convResponse = new ConversationResponse();
            UserResponse currentUserResponse = new UserResponse();
            UserResponse otherUserResponse = new UserResponse();
            List<MessageResponse> convMessages = new ArrayList<>();
            User currentUserInConv = Objects.equals(existingConversation.getUserOneId(), currentUser.getId()) ? userOne : userTwo;
            User otherUserInConv = Objects.equals(existingConversation.getUserOneId(), currentUser.getId()) ? userTwo : userOne;

            currentUserResponse.setUserId(currentUserInConv.getId());
            currentUserResponse.setEmail(currentUserInConv.getEmail());
            if (userTwo.getProfile().getGender() != null) {
                currentUserResponse.setGender(userTwo.getProfile().getGender().getDisplayName());
            }
            currentUserResponse.setCountry(currentUserInConv.getProfile().getCountry() != null ? currentUserInConv.getProfile().getCountry() : null);
            currentUserResponse.setRegion(currentUserInConv.getProfile().getRegion() != null ? currentUserInConv.getProfile().getRegion() : null);
            currentUserResponse.setDateOfBirth(currentUserInConv.getProfile().getDateOfBirth() != null ? String.valueOf(currentUserInConv.getProfile().getDateOfBirth()) : null);
            currentUserResponse.setRelation(currentUserInConv.getProfile().getRelation() != null ? currentUserInConv.getProfile().getRelation().getDisplayName() : null);
            currentUserResponse.setBio(currentUserInConv.getProfile().getBio());
            currentUserResponse.setImageUrl(currentUserInConv.getProfile().getImageUrl());

            otherUserResponse.setUserId(otherUserInConv.getId());
            otherUserResponse.setEmail(otherUserInConv.getEmail());
            if (otherUserInConv.getProfile().getGender() != null) {
                otherUserResponse.setGender(otherUserInConv.getProfile().getGender().getDisplayName());
            }
            otherUserResponse.setCountry(otherUserInConv.getProfile().getCountry() != null ? otherUserInConv.getProfile().getCountry() : null);
            otherUserResponse.setRegion(otherUserInConv.getProfile().getRegion() != null ? otherUserInConv.getProfile().getRegion() : null);
            otherUserResponse.setDateOfBirth(otherUserInConv.getProfile().getDateOfBirth() != null ? String.valueOf(otherUserInConv.getProfile().getDateOfBirth()) : null);
            otherUserResponse.setRelation(otherUserInConv.getProfile().getRelation() != null ? otherUserInConv.getProfile().getRelation().getDisplayName() : null);
            otherUserResponse.setBio(otherUserInConv.getProfile().getBio());
            otherUserResponse.setImageUrl(otherUserInConv.getProfile().getImageUrl());

            for(Message message : existingConversation.getMessages()){
                MessageResponse messageResponse = new MessageResponse();
                messageResponse.setId(message.getId());
                messageResponse.setContent(message.getContent());
                messageResponse.setSenderId(message.getSender().getId());
                messageResponse.setCreatedAt(String.valueOf(message.getCreatedAt()));
                messageResponse.setUpdatedAt(String.valueOf(message.getUpdatedAt()));

                convMessages.add(messageResponse);
            }

            convResponse.setId(existingConversation.getId());
            convResponse.setMessages(convMessages);
            convResponse.setCurrentUser(currentUserResponse);
            convResponse.setOtherUser(otherUserResponse);
            convResponse.setCreatedAt(String.valueOf(existingConversation.getCreatedAt()));
            convResponse.setUpdatedAt(String.valueOf(existingConversation.getUpdatedAt()));

            return convResponse;
        }

        Conversation newConversation = new Conversation();

        newConversation.setUserOneId(currentUser.getId());
        newConversation.setUserTwoId(otherUser.getId());

        conversationRepository.save(newConversation);

        ConversationResponse newConvResponse = new ConversationResponse();
        UserResponse newCurrentUserResponse = new UserResponse();
        UserResponse newOtherUserResponse = new UserResponse();

        newCurrentUserResponse.setUserId(currentUser.getId());
        newCurrentUserResponse.setEmail(currentUser.getEmail());
        if (currentUser.getProfile().getGender() != null) {
            newCurrentUserResponse.setGender(currentUser.getProfile().getGender().getDisplayName());
        }
        newCurrentUserResponse.setCountry(currentUser.getProfile().getCountry() != null ? currentUser.getProfile().getCountry() : null);
        newCurrentUserResponse.setRegion(currentUser.getProfile().getRegion() != null ? currentUser.getProfile().getRegion() : null);
        newCurrentUserResponse.setDateOfBirth(currentUser.getProfile().getDateOfBirth() != null ? String.valueOf(currentUser.getProfile().getDateOfBirth()) : null);
        newCurrentUserResponse.setRelation(currentUser.getProfile().getRelation() != null ? currentUser.getProfile().getRelation().getDisplayName() : null);
        newCurrentUserResponse.setBio(currentUser.getProfile().getBio());
        newCurrentUserResponse.setImageUrl(currentUser.getProfile().getImageUrl());

        newOtherUserResponse.setUserId(otherUser.getId());
        newOtherUserResponse.setEmail(otherUser.getEmail());
        if (otherUser.getProfile().getGender() != null) {
            newOtherUserResponse.setGender(otherUser.getProfile().getGender().getDisplayName());
        }
        newOtherUserResponse.setCountry(otherUser.getProfile().getCountry() != null ? otherUser.getProfile().getCountry() : null);
        newOtherUserResponse.setRegion(otherUser.getProfile().getRegion() != null ? otherUser.getProfile().getRegion() : null);
        newOtherUserResponse.setDateOfBirth(otherUser.getProfile().getDateOfBirth() != null ? String.valueOf(otherUser.getProfile().getDateOfBirth()) : null);
        newOtherUserResponse.setRelation(otherUser.getProfile().getRelation() != null ? otherUser.getProfile().getRelation().getDisplayName() : null);
        newOtherUserResponse.setBio(otherUser.getProfile().getBio());
        newOtherUserResponse.setImageUrl(otherUser.getProfile().getImageUrl());

        newConvResponse.setId(newConversation.getId());
        newConvResponse.setCurrentUser(newCurrentUserResponse);
        newConvResponse.setOtherUser(newOtherUserResponse);
        newConvResponse.setCreatedAt(String.valueOf(newConversation.getCreatedAt()));
        newConvResponse.setUpdatedAt(String.valueOf(newConversation.getUpdatedAt()));

        return newConvResponse;
    }

    @Override
    public Message sendMessage(String messageContent, String conversationId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByEmail(authentication.getName()).orElseThrow();

        Conversation selectedConversation = conversationRepository.findById(conversationId).orElseThrow();

        if(selectedConversation.getUserOneId().equals(currentUser.getId()) || selectedConversation.getUserTwoId().equals(currentUser.getId())){

            Message newMessage = new Message();

            newMessage.setContent(messageContent);
            newMessage.setConversation(selectedConversation);
            newMessage.setSender(currentUser);

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

        if(!selectedMessage.getSender().equals(currentUser)){
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
            User userTwo = userRepository.findById(conversation.getUserTwoId()).orElseThrow();
            UserResponse otherUser = new UserResponse();
            if (userTwo.getProfile().getGender() != null) {
                otherUser.setGender(userTwo.getProfile().getGender().getDisplayName());
            }
            otherUser.setCountry(userTwo.getProfile().getCountry() != null ? userTwo.getProfile().getCountry() : null);
            otherUser.setRegion(userTwo.getProfile().getRegion() != null ? userTwo.getProfile().getRegion() : null);
            otherUser.setDateOfBirth(userTwo.getProfile().getDateOfBirth() != null ? String.valueOf(userTwo.getProfile().getDateOfBirth()) : null);
            otherUser.setRelation(userTwo.getProfile().getRelation() != null ? userTwo.getProfile().getRelation().getDisplayName() : null);
            otherUser.setBio(userTwo.getProfile().getBio());
            otherUser.setImageUrl(userTwo.getProfile().getImageUrl());

            ConversationResponse newConv = new ConversationResponse();
            newConv.setId(conversation.getId());

            for(Message message : conversation.getMessages()){
                MessageResponse messageResponse = new MessageResponse();
                messageResponse.setId(message.getId());
                messageResponse.setContent(message.getContent());
                messageResponse.setSenderId(message.getSender().getId());
                messageResponse.setCreatedAt(String.valueOf(message.getCreatedAt()));
                messageResponse.setUpdatedAt(String.valueOf(message.getUpdatedAt()));

                messageResponseList.add(messageResponse);
            }

            newConv.setMessages(messageResponseList);
            newConv.setOtherUser(otherUser);

            conversationResponseList.add(newConv);
        }

        for (Conversation conversation : conversationsReceived){
            User userOne = userRepository.findById(conversation.getUserOneId()).orElseThrow();
            UserResponse otherUser = new UserResponse();
            if (userOne.getProfile().getGender() != null) {
                otherUser.setGender(userOne.getProfile().getGender().getDisplayName());
            }
            otherUser.setCountry(userOne.getProfile().getCountry() != null ? userOne.getProfile().getCountry() : null);
            otherUser.setRegion(userOne.getProfile().getRegion() != null ? userOne.getProfile().getRegion() : null);
            otherUser.setDateOfBirth(userOne.getProfile().getDateOfBirth() != null ? String.valueOf(userOne.getProfile().getDateOfBirth()) : null);
            otherUser.setRelation(userOne.getProfile().getRelation() != null ? userOne.getProfile().getRelation().getDisplayName() : null);
            otherUser.setBio(userOne.getProfile().getBio());
            otherUser.setImageUrl(userOne.getProfile().getImageUrl());

            ConversationResponse newConv = new ConversationResponse();

            for(Message message : conversation.getMessages()){
                MessageResponse messageResponse = new MessageResponse();
                messageResponse.setId(message.getId());
                messageResponse.setContent(message.getContent());
                messageResponse.setSenderId(message.getSender().getId());
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
