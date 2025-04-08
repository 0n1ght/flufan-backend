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
    public void addServiceToAcc(long buyerId, long sellerId, long quantity) {
        Account buyerAccount = accountService.getById(buyerId);
        Account sellerAccount = accountService.getById(sellerId);
        //todo
        // dodaj wiadomosc na chat "informacje"
        // dodaj powiadomienie dla sellerId
        // dodaj mozliwosc realizacji uslugi przez sellera
        // jezeli sellerAccount.getProfile().getMenu() zawiera rzecz ktora ktos kupil, to ona tu jest
        // dodaj do konta liste z zakupionymi serwisami tak jak z wiadomosciami. obniz tutaj licznik po wykonaniu

//        messageService.sendMessage(sellerId, sellerAccount.getProfile().getMenu(), MessageType.BOUGHT_SERVICE);

        sellerAccount.setNotifications(sellerAccount.getNotifications()+1);

        accountService.updateAccount(buyerAccount);
        accountService.updateAccount(sellerAccount);
    }
}
