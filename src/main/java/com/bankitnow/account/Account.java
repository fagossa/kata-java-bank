package com.bankitnow.account;

import com.bankitnow.money.Money;
import javaslang.control.Try;

import java.io.PrintStream;
import java.time.OffsetDateTime;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.bankitnow.account.Transaction.TransactionBuilder;
import static com.bankitnow.account.Transaction.Type;

class Account {

    private String id;
    private Money balance;
    private AccountJournal journal;

    Account(String id, Money balance, AccountJournal journal) {
        this.id = id;
        this.balance = balance;
        this.journal = journal;
    }

    Money balance() {
        return balance;
    }

    void deposit(Money valueToDeposit, OffsetDateTime aDateTime) {
        balance.plus(valueToDeposit)
                .flatMap((total) -> sendTransactionHaving(id, Type.Deposit, valueToDeposit, total, aDateTime))
                .forEach((transaction) -> balance = transaction.balance());
    }

    void withdraw(Money valueToWithdraw, OffsetDateTime aDateTime) {
        balance.minus(valueToWithdraw)
                .flatMap((total) -> sendTransactionHaving(id, Type.Withdraw, valueToWithdraw, total, aDateTime))
                .forEach((transaction) -> balance = transaction.balance());
    }

    void transfer(Money valueToTransfer, Account destination, OffsetDateTime aDateTime) {
        withdraw(valueToTransfer, aDateTime);
        destination.deposit(valueToTransfer, aDateTime);
    }

    void history(PrintStream out, Supplier<String> columns, Function<Transaction, String> formatter) {
        journal.historyOf(this.id, out, columns, formatter);
    }

    private Try<Transaction> sendTransactionHaving(String accountId, Type type, Money operation, Money total, OffsetDateTime dateTime) {
        return new TransactionBuilder()
                .at(dateTime).withType(type)
                .forAccount(accountId)
                .withOperation(operation)
                .withNewBalance(total)
                .build()
                .flatMap(transaction -> journal.send(transaction));
    }
}
