package com.bankitnow.account;

import com.bankitnow.money.Balance;
import javaslang.control.Try;

import java.io.PrintStream;
import java.time.OffsetDateTime;
import java.util.function.Function;

class Account {

    private String id;
    private Balance balance;
    private AccountJournal journal;

    Account(String id, Balance balance, AccountJournal journal) {
        this.id = id;
        this.balance = balance;
        this.journal = journal;
    }

    void deposit(Balance valueToDeposit, OffsetDateTime aDateTime) {
        Try<Balance> maybeBalance = balance.plus(valueToDeposit);
        sendTransactionHaving(id, Transaction.Type.Deposit, valueToDeposit, maybeBalance, aDateTime);
    }

    void withdraw(Balance valueToWithdraw, OffsetDateTime aDateTime) {
        Try<Balance> maybeBalance = balance.minus(valueToWithdraw);
        sendTransactionHaving(id, Transaction.Type.Withdraw, valueToWithdraw, maybeBalance, aDateTime);
    }

    void history(PrintStream out, Function<Transaction, String> formatter) {
        journal.historyOf(id, out, formatter);
    }

    private void sendTransactionHaving(String accountId, Transaction.Type type, Balance operation, Try<Balance> newBalance, OffsetDateTime dateTime) {
        newBalance
                .flatMap((total) ->
                        new Transaction.TransactionBuilder()
                                .at(dateTime)
                                .forAccount(accountId)
                                .withOperation(operation)
                                .withNewBalance(total)
                                .withType(type)
                                .build()
                )
                .flatMap(transaction -> journal.send(transaction));
    }
}
