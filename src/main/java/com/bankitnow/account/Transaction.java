package com.bankitnow.account;

import com.bankitnow.money.Money;
import javaslang.control.Option;
import javaslang.control.Try;

import java.time.OffsetDateTime;
import java.util.Objects;

class Transaction {
    private String accountId;
    private Money operation;
    private Money money;
    private OffsetDateTime dateTime;
    private Type type;

    private Transaction(String accountId, Money operation, Money money, OffsetDateTime dateTime, Type type) {
        this.accountId = accountId;
        this.operation = operation;
        this.money = money;
        this.dateTime = dateTime;
        this.type = type;
    }

    public String accountId() {
        return accountId;
    }

    public Money balance() {
        return money;
    }

    public Money operation() {
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
        private Money total;
        private Money operation;
        private Type type;

        TransactionBuilder at(OffsetDateTime dateTime) {
            this.dateTime = dateTime;
            return this;
        }

        TransactionBuilder forAccount(String accountId) {
            this.accountId = accountId;
            return this;
        }

        TransactionBuilder withNewBalance(Money total) {
            this.total = total;
            return this;
        }

        TransactionBuilder withType(Type type) {
            this.type = type;
            return this;
        }

        TransactionBuilder withOperation(Money operation) {
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
                Objects.equals(money, that.money) &&
                Objects.equals(dateTime, that.dateTime) &&
                type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, operation, money, dateTime, type);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "accountId='" + accountId + '\'' +
                ", operation=" + operation +
                ", balance=" + money +
                ", dateTime=" + dateTime +
                ", type=" + type +
                '}';
    }
}
