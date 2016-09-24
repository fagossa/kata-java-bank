package com.bankitnow.account;

import javaslang.control.Try;

import java.io.PrintStream;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;

class VolatileJournal implements AccountJournal {

    private ConcurrentMap<String, Set<Transaction>> store = new ConcurrentHashMap<>();

    @Override
    public Try<Transaction> send(Transaction newTransaction) {
        store.computeIfAbsent(newTransaction.accountId(), (key) -> new ConcurrentSkipListSet<>());
        store.compute(newTransaction.accountId(), (key, value) -> {
                    value.add(newTransaction);
                    return value;
                }
        );
        return Try.success(newTransaction);
    }

    @Override
    public void historyOf(String transactionId, PrintStream out, TransactionFormatter formatter) {
        final Set<Transaction> transactions = store.computeIfAbsent(transactionId, (key) -> new ConcurrentSkipListSet<>());
        transactions.stream().forEach((transaction) ->
                out.println(formatter.format(transaction))
        );
    }
}
