package com.bankitnow.account;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import com.bankitnow.money.Balance;
import javaslang.control.Option;
import javaslang.control.Try;

import java.time.OffsetDateTime;
import java.util.function.Function;

class Account extends AbstractLoggingActor {

    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private String id;
    private Balance balance;
    private ActorRef journal;

    private Option<Function<Transaction, String>> maybeFormatter = Option.none();

    Account(String id, Balance balance, ActorRef journal) {
        this.id = id;
        this.balance = balance;
        this.journal = journal;

        receive(
                ReceiveBuilder
                        .match(AccountEvents.Deposit.class, this::onDeposit)
                        .match(AccountEvents.GetBalance.class, this::onBalance)
                        .match(AccountEvents.Withdraw.class, this::onWithdraw)
                        .match(AccountEvents.PrintStatements.class, this::onPrintStatements)
                        .match(AccountEvents.StatementList.class, this::onStatementList)
                        .build()
        );
    }

    static Props props(String id, Balance balance, ActorRef journal) {
        return Props.create(Account.class, id, balance, journal);
    }

    private void onBalance(AccountEvents.GetBalance getBalance) {
        sender().tell(balance, self());
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

    private void onPrintStatements(AccountEvents.PrintStatements printStatements) {
        this.maybeFormatter = Option.some(printStatements.formatter);
        journal.tell(new AccountEvents.GetStatements(this.id, printStatements.columns), self());
    }

    private void onStatementList(AccountEvents.StatementList statements) {
        final Function<Transaction, String> formatter = this.maybeFormatter.getOrElse(Transaction::toString);
        log.info(statements.columns.get());
        statements.transactions.stream().forEach((transaction) ->
                log.info(formatter.apply(transaction))
        );
    }

}
