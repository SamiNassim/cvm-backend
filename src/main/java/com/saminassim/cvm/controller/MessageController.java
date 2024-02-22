package com.saminassim.cvm.controller;

import com.saminassim.cvm.dto.request.MessageRequest;
import com.saminassim.cvm.exception.MessageCannotBeDeletedException;
import com.saminassim.cvm.exception.MessageCannotBeSentException;
import com.saminassim.cvm.exception.UnauthorizedRequestException;
import com.saminassim.cvm.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000/", allowCredentials = "true")
public class MessageController {

    private final MessageService messageService;
    @GetMapping("/{receiverId}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<?> getOrCreateConversation(@PathVariable String receiverId){
        return ResponseEntity.ok(messageService.getOrCreateConversation(receiverId));
    }

    @PostMapping("/{conversationId}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<?> sendMessage(@RequestBody MessageRequest messageRequest, @PathVariable String conversationId){
        try {
            return ResponseEntity.ok(messageService.sendMessage(messageRequest.getMessageContent(), conversationId));
        } catch (MessageCannotBeSentException e){
            return ResponseEntity.status(401).body(e.getMessage());
        }

    }

    @DeleteMapping("/{messageId}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<?> deleteMessage(@PathVariable String messageId){
        try {
            messageService.deleteMessage(messageId);
            return ResponseEntity.ok().build();
        } catch (MessageCannotBeDeletedException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @GetMapping
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<?> getAllConversations(){
        try {
            return ResponseEntity.ok(messageService.getAllConversations());
        } catch (UnauthorizedRequestException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }
}
