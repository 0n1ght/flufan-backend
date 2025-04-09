package com.frinkan.service.impl;

import com.frinkan.entity.Account;
import com.frinkan.enums.MessageType;
import com.frinkan.exception.ServiceNotFoundException;
import com.frinkan.service.AccountService;
import com.frinkan.service.MessageService;
import com.frinkan.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private AccountService accountService;
    @Autowired
    private MessageService messageService;

    @Override
    public void addMessageToAcc(long buyerId, long sellerId, long quantity) {
        Account buyerAccount = accountService.getById(buyerId);
        Account sellerAccount = accountService.getById(sellerId);

        Map<Long, Long> buyerAvailableMessages = buyerAccount.getAvailableMessages();
        Long buyerCurrentCount = buyerAvailableMessages.get(sellerId);
        if (buyerCurrentCount == null) {
            buyerCurrentCount = 0L;
        }
        buyerAvailableMessages.put(sellerId, buyerCurrentCount + quantity);
        buyerAccount.setAvailableMessages(buyerAvailableMessages);
        accountService.updateAccount(buyerAccount);

        Map<Long, Long> sellerAvailableMessages = sellerAccount.getAvailableMessages();
        Long sellerCurrentCount = sellerAvailableMessages.get(buyerId);
        if (sellerCurrentCount == null) {
            sellerCurrentCount = 0L;
        }
        sellerAvailableMessages.put(buyerId, sellerCurrentCount + quantity);
        sellerAccount.setAvailableMessages(sellerAvailableMessages);
        accountService.updateAccount(sellerAccount);
    }

    @Override
    public void addCallToAcc(long buyerId, long sellerId) {
        //todo
        // powiadomienie dla influencera
        // na chacie pokazuje sie mini kalendarzyk, jaka godzine zaproponowal kupujacy
        // influencer moze zaakceptowac albo odrzucic i zaproponowac jakas inna
        // buyer dostaje powiadomienie
    }

    @Override
    public void addServiceToAcc(long buyerId, long sellerId, String productName, long quantity) {
        Account sellerAccount = accountService.getById(sellerId);
        //todo
        // dodaj mozliwosc realizacji uslugi przez sellera
        List<com.frinkan.entity.Service> menu = sellerAccount.getProfile().getMenu();
        if (menu.stream()
                .noneMatch(service -> productName.equalsIgnoreCase(service.getTitle())))
            throw new ServiceNotFoundException("Service not found");

        messageService.sendMessage(sellerId, productName, MessageType.BOUGHT_SERVICE);
        sellerAccount.setNotifications(sellerAccount.getNotifications()+1);
        accountService.updateAccount(sellerAccount);
    }
}
