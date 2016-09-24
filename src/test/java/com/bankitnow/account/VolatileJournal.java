package com.bankitnow.account;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import com.bankitnow.money.Balance;
import javaslang.control.Try;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.function.Supplier;

class VolatileJournal extends AbstractLoggingActor {

    private ConcurrentMap<String, Queue<Transaction>> store = new ConcurrentHashMap<>();
    private ActorRef probe;

    VolatileJournal(ActorRef probe) {
        this.probe = probe;
        receive(
                ReceiveBuilder
                        .match(Transaction.class, this::onTransaction)
                        .match(AccountEvents.HistoryForAccount.class, this::onHistory)
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

    private void onHistory(AccountEvents.HistoryForAccount history) {
        String transactionId = history.accountId;
        Supplier<String> columns = history.columns;
        Function<Transaction, String> formatter = history.formatter;
        probe.tell(columns.get(), ActorRef.noSender());
        final Queue<Transaction> transactions = store.computeIfAbsent(transactionId, (key) -> new LinkedList<>());
        transactions.stream().forEach((transaction) ->
                probe.tell(formatter.apply(transaction), ActorRef.noSender())
        );
    }

    static Props props(ActorRef probe) {
        return Props.create(VolatileJournal.class, probe);
    }
}
