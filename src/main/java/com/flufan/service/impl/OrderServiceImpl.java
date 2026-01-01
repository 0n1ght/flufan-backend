package com.flufan.service.impl;

import com.flufan.entity.Account;
import com.flufan.enums.MessageType;
import com.flufan.service.AccountService;
import com.flufan.service.MessageService;
import com.flufan.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final AccountService accountService;
    private final MessageService messageService;

    @Override
    public void realiseMessage(UUID buyerPublicId, UUID sellerPublicId, String content, String details) {
        messageService.sendMessage(sellerPublicId, content, MessageType.valueOf(details.toUpperCase()));
        addRepliesToAcc(sellerPublicId, buyerPublicId, 1);
    }

    @Override
    public void realiseCall(UUID buyerPublicId, UUID sellerPublicId) {
        //todo
        // powiadomienie dla influencera
        // na chacie pokazuje sie mini kalendarzyk, jaka godzine zaproponowal kupujacy
        // influencer moze zaakceptowac albo odrzucic i zaproponowac jakas inna
        // buyer dostaje powiadomienie
    }

    @Override
    public void realiseService(UUID buyerPublicId, UUID sellerPublicId, String productName, long quantity, String details) {
        messageService.sendMessage(sellerPublicId, productName+"\n"+details, MessageType.BOUGHT_SERVICE);
        addRepliesToAcc(sellerPublicId, buyerPublicId, quantity);
    }

    private void addRepliesToAcc(UUID senderPublicId, UUID receiverPublicId, long quantity) {
        Account acc = accountService.findByPublicId(senderPublicId);

        Map<UUID, Long> buyerAvailableReplies = acc.getAvailableReplies();
        Long buyerCurrentCount = buyerAvailableReplies.get(receiverPublicId);
        if (buyerCurrentCount == null) {
            buyerCurrentCount = 0L;
        }
        buyerAvailableReplies.put(receiverPublicId, buyerCurrentCount + quantity);
        acc.setAvailableReplies(buyerAvailableReplies);
        accountService.updateAccount(acc);
    }
}
