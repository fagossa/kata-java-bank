package com.bankitnow.account;

import javaslang.control.Try;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.function.Supplier;

class VolatileJournal implements AccountJournal {

    private ConcurrentMap<String, Queue<Transaction>> store = new ConcurrentHashMap<>();

    @Override
    public Try<Transaction> send(Transaction newTransaction) {
        store.computeIfAbsent(newTransaction.accountId(), (key) -> new LinkedList<>());
        store.compute(newTransaction.accountId(), (key, value) -> {
                    value.add(newTransaction);
                    return value;
                }
        );
        return Try.success(newTransaction);
    }

    @Override
    public void historyOf(String transactionId, PrintStream out, Supplier<String> columns, Function<Transaction, String> formatter) {
        out.println(columns.get());
        final Queue<Transaction> transactions = store.computeIfAbsent(transactionId, (key) -> new LinkedList<>());
        transactions.stream().forEach((transaction) ->
                out.println(formatter.apply(transaction))
        );
    }
}
