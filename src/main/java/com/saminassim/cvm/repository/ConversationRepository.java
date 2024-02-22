package com.saminassim.cvm.repository;

import com.saminassim.cvm.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, String> {
    Conversation findConversationByUserOneIdAndUserTwoId(String userOneId, String userTwoId);
    List<Conversation> findConversationsByUserOneId(String userOneId);
    List<Conversation> findConversationsByUserTwoId(String userTwoId);
}
