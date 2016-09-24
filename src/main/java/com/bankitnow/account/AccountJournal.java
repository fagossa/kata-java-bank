package com.bankitnow.account;

import javaslang.control.Try;

import java.io.PrintStream;

interface AccountJournal {
    Try<Transaction> send(Transaction transaction);

    void historyOf(String transactionId, PrintStream out, TransactionFormatter formatter);
}
