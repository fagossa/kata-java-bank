package com.bankitnow.account;

import com.bankitnow.money.Balance;

import java.time.OffsetDateTime;
import java.util.UUID;

class Account {

    private String id;
    private OffsetDateTime openingDate;
    private Balance balance;


    Account(String id, OffsetDateTime openingDate, Balance balance) {
        this.id = id;
        this.openingDate = openingDate;
        this.balance = balance;
    }

    public String id() {
        return id;
    }

    public OffsetDateTime openingDate() {
        return openingDate;
    }

    Balance balance() {
        return balance;
    }

    public static String withRandomId() {
        return UUID.randomUUID().toString();
    }
}
