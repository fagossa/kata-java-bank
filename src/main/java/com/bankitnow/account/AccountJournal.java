package com.bankitnow.account;

import javaslang.control.Try;

import java.io.PrintStream;
import java.util.function.Function;

interface AccountJournal {
    Try<Transaction> send(Transaction transaction);

    void historyOf(String transactionId, PrintStream out, Function<Transaction, String> formatter);
}
