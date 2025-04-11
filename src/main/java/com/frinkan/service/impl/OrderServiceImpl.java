package com.frinkan.service.impl;

import com.frinkan.entity.Account;
import com.frinkan.enums.MessageType;
import com.frinkan.service.AccountService;
import com.frinkan.service.MessageService;
import com.frinkan.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private AccountService accountService;
    @Autowired
    private MessageService messageService;

    @Override
    public void realiseMessage(long buyerId, long sellerId, long quantity) {
        addSendMessageToAcc(buyerId, sellerId, quantity);
        addSendMessageToAcc(sellerId, buyerId, quantity);
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
    public void realiseService(long buyerId, long sellerId, String productName, long quantity) {
        Account sellerAccount = accountService.getById(sellerId);

        messageService.sendMessage(sellerId, productName, MessageType.BOUGHT_SERVICE);
        addSendMessageToAcc(sellerId, buyerId, quantity);
    }

    private void addSendMessageToAcc(long senderId, long receiverId, long quantity) {
        Account acc = accountService.getById(senderId);

        Map<Long, Long> buyerAvailableMessages = acc.getAvailableMessages();
        Long buyerCurrentCount = buyerAvailableMessages.get(receiverId);
        if (buyerCurrentCount == null) {
            buyerCurrentCount = 0L;
        }
        buyerAvailableMessages.put(receiverId, buyerCurrentCount + quantity);
        acc.setAvailableMessages(buyerAvailableMessages);
        accountService.updateAccount(acc);
    }
}
