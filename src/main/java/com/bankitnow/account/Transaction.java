package com.bankitnow.account;

import com.bankitnow.money.Balance;
import javaslang.control.Option;
import javaslang.control.Try;

import java.time.OffsetDateTime;
import java.util.Objects;

class Transaction {
    private String accountId;
    private Balance operation;
    private Balance balance;
    private OffsetDateTime dateTime;
    private Type type;

    private Transaction(String accountId, Balance operation, Balance balance, OffsetDateTime dateTime, Type type) {
        this.accountId = accountId;
        this.operation = operation;
        this.balance = balance;
        this.dateTime = dateTime;
        this.type = type;
    }

    public String accountId() {
        return accountId;
    }

    public Balance balance() {
        return balance;
    }

    public Balance operation() {
        return operation;
    }

    public OffsetDateTime dateTime() {
        return dateTime;
    }

    public Type type() {
        return type;
    }

    static class TransactionBuilder {
        private OffsetDateTime dateTime;
        private String accountId;
        private Balance total;
        private Balance operation;
        private Type type;

        TransactionBuilder at(OffsetDateTime dateTime) {
            this.dateTime = dateTime;
            return this;
        }

        TransactionBuilder forAccount(String accountId) {
            this.accountId = accountId;
            return this;
        }

        TransactionBuilder withNewBalance(Balance total) {
            this.total = total;
            return this;
        }

        TransactionBuilder withType(Type type) {
            this.type = type;
            return this;
        }

        TransactionBuilder withOperation(Balance operation) {
            this.operation = operation;
            return this;
        }

        Try<Transaction> build() {
            return Try.of(() ->
                    new Transaction(
                            Option.of(accountId).get(),
                            Option.of(operation).get(),
                            Option.of(total).get(),
                            Option.of(dateTime).get(),
                            Option.of(type).get()
                    )
            );
        }
    }

    enum Type {
        Deposit, Withdraw
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transaction)) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(accountId, that.accountId) &&
                Objects.equals(operation, that.operation) &&
                Objects.equals(balance, that.balance) &&
                Objects.equals(dateTime, that.dateTime) &&
                type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, operation, balance, dateTime, type);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "accountId='" + accountId + '\'' +
                ", operation=" + operation +
                ", balance=" + balance +
                ", dateTime=" + dateTime +
                ", type=" + type +
                '}';
    }
}
