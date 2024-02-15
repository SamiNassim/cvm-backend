package com.saminassim.cvm.controller;

import com.saminassim.cvm.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    @GetMapping("/{receiverId}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<?> getOrCreateConversation(@PathVariable String receiverId){
        return ResponseEntity.ok(messageService.getOrCreateConversation(receiverId));
    }
}
