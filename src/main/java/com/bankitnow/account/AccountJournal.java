package com.bankitnow.account;

import javaslang.control.Try;

import java.io.PrintStream;
import java.util.function.Function;
import java.util.function.Supplier;

interface AccountJournal {
    Try<Transaction> send(Transaction transaction);

    void historyOf(String transactionId, PrintStream out, Supplier<String> columns, Function<Transaction, String> formatter);
}
