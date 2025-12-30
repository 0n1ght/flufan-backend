package com.flufan.service.impl;

import com.flufan.entity.Account;
import com.flufan.enums.MessageType;
import com.flufan.service.AccountService;
import com.flufan.service.MessageService;
import com.flufan.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final AccountService accountService;
    private final MessageService messageService;

    @Override
    public void realiseMessage(long buyerId, long sellerId, String content, String details) {
        messageService.sendMessage(sellerId, content, MessageType.valueOf(details.toUpperCase()));
        addRepliesToAcc(sellerId, buyerId, 1);
    }

    @Override
    public void realiseCall(long buyerId, long sellerId) {
        //todo
        // powiadomienie dla influencera
        // na chacie pokazuje sie mini kalendarzyk, jaka godzine zaproponowal kupujacy
        // influencer moze zaakceptowac albo odrzucic i zaproponowac jakas inna
        // buyer dostaje powiadomienie
    }

    @Override
    public void realiseService(long buyerId, long sellerId, String productName, long quantity, String details) {
        messageService.sendMessage(sellerId, productName+"\n"+details, MessageType.BOUGHT_SERVICE);
        addRepliesToAcc(sellerId, buyerId, quantity);
    }

    private void addRepliesToAcc(long senderId, long receiverId, long quantity) {
        Account acc = accountService.findById(senderId);

        Map<Long, Long> buyerAvailableReplies = acc.getAvailableReplies();
        Long buyerCurrentCount = buyerAvailableReplies.get(receiverId);
        if (buyerCurrentCount == null) {
            buyerCurrentCount = 0L;
        }
        buyerAvailableReplies.put(receiverId, buyerCurrentCount + quantity);
        acc.setAvailableReplies(buyerAvailableReplies);
        accountService.updateAccount(acc);
    }
}
