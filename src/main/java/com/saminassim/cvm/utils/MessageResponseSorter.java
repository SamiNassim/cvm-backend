package com.saminassim.cvm.utils;

import com.saminassim.cvm.dto.response.MessageResponse;

import java.util.Comparator;
import java.util.List;

public class MessageResponseSorter {
    public static void sortByCreatedAt(List<MessageResponse> messages) {
        messages.sort(Comparator.comparing(MessageResponse::getCreatedAt));
    }
}
