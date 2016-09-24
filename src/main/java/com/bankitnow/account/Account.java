package com.bankitnow.account;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import com.bankitnow.money.Balance;
import javaslang.control.Try;

import java.time.OffsetDateTime;
import java.util.function.Function;
import java.util.function.Supplier;

class Account extends AbstractLoggingActor {

    private String id;
    private Balance balance;
    private ActorRef journal;

    Account(String id, Balance balance, ActorRef journal) {
        this.id = id;
        this.balance = balance;
        this.journal = journal;

        receive(
                ReceiveBuilder
                        .match(AccountEvents.Deposit.class, this::onDeposit)
                        .match(AccountEvents.GetBalance.class, this::onBalance)
                        .match(AccountEvents.Withdraw.class, this::onWithdraw)
                        .match(AccountEvents.History.class, this::onHistory)
                        .build()
        );
    }

    static Props props(String id, Balance balance, ActorRef journal) {
        return Props.create(Account.class, id, balance, journal);
    }

    private void onBalance(AccountEvents.GetBalance getBalance) {
        sender().tell(balance, ActorRef.noSender());
    }

    private void onDeposit(AccountEvents.Deposit deposit) {
        Balance valueToDeposit = deposit.valueToDeposit;
        OffsetDateTime aDateTime = deposit.aDateTime;
        Try<Balance> maybeBalance = balance.plus(valueToDeposit);
        maybeBalance.forEach((newBalance) -> {
            balance = newBalance;
            sendTransactionHaving(id, Transaction.Type.Deposit, valueToDeposit, newBalance, aDateTime);
        });
    }

    private void onWithdraw(AccountEvents.Withdraw withdraw) {
        Balance valueToWithdraw = withdraw.valueToDeposit;
        OffsetDateTime aDateTime = withdraw.aDateTime;
        Try<Balance> maybeBalance = balance.minus(valueToWithdraw);
        maybeBalance.forEach((newBalance) -> {
            balance = newBalance;
            sendTransactionHaving(id, Transaction.Type.Withdraw, valueToWithdraw, newBalance, aDateTime);
        });
    }

    private void onHistory(AccountEvents.History history) {
        Supplier<String> columns = history.columns;
        Function<Transaction, String> formatter = history.formatter;
        journal.tell(new AccountEvents.HistoryForAccount(this.id, columns, formatter), ActorRef.noSender());
    }

    private void sendTransactionHaving(String accountId, Transaction.Type type, Balance operation,
                                       Balance total, OffsetDateTime dateTime) {
        final Try<Transaction> maybeTransaction = new Transaction.TransactionBuilder()
                .at(dateTime).forAccount(accountId)
                .withOperation(operation)
                .withNewBalance(total).withType(type).build();
        maybeTransaction.forEach(transaction ->
                journal.tell(transaction, ActorRef.noSender())
        );
    }
}
