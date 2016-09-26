package com.bankitnow.account;

import akka.actor.AbstractLoggingActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import javaslang.control.Try;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

class VolatileJournal extends AbstractLoggingActor {

    private ConcurrentMap<String, Queue<Transaction>> store = new ConcurrentHashMap<>();

    VolatileJournal() {
        receive(
                ReceiveBuilder
                        .match(Transaction.class, this::onTransaction)
                        .match(AccountEvents.GetStatements.class, this::onGetStatements)
                        .build()
        );
    }

    private Try<Transaction> onTransaction(Transaction newTransaction) {
        store.computeIfAbsent(newTransaction.accountId(), (key) -> new LinkedList<>());
        store.compute(newTransaction.accountId(), (key, value) -> {
                    value.add(newTransaction);
                    return value;
                }
        );
        return Try.success(newTransaction);
    }

    private void onGetStatements(AccountEvents.GetStatements statements) {
        final Queue<Transaction> transactions = store.computeIfAbsent(statements.accountId, (key) -> new LinkedList<>());
        final AccountEvents.StatementList message = new AccountEvents.StatementList(
                statements.accountId,
                transactions,
                statements.columns
        );
        sender().tell(message, self());
    }

    static Props props() {
        return Props.create(VolatileJournal.class);
    }
}
