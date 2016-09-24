package com.bankitnow.account;

import java.time.format.DateTimeFormatter;

public class DefaultTransactionFormatter implements TransactionFormatter {

    @Override
    public String format(Transaction transaction) {
        return String.format(
                "%s, %s, %s, %s",
                transaction.balance().amt(),
                transaction.operation().amt(),
                transaction.type(),
                transaction.dateTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        );
    }
}
