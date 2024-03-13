package com.saminassim.cvm.dto.response;

import lombok.Data;

@Data
public class MessageResponse {
    private String id;
    private String content;
    private String senderId;
    private String createdAt;
    private String updatedAt;
}
