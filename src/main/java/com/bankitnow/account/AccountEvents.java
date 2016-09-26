package com.bankitnow.account;

import com.bankitnow.money.Balance;

import java.io.PrintStream;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.Supplier;

interface AccountEvents {

    static class Deposit {
        Balance valueToDeposit;
        OffsetDateTime aDateTime;

        public Deposit(Balance valueToDeposit, OffsetDateTime aDateTime) {
            this.valueToDeposit = valueToDeposit;
            this.aDateTime = aDateTime;
        }
    }

    static class Withdraw {
        Balance valueToDeposit;
        OffsetDateTime aDateTime;

        public Withdraw(Balance valueToDeposit, OffsetDateTime aDateTime) {
            this.valueToDeposit = valueToDeposit;
            this.aDateTime = aDateTime;
        }
    }

    static class PrintStatements {
        final Supplier<String> columns;
        final Function<Transaction, String> formatter;

        public PrintStatements(Supplier<String> columns, Function<Transaction, String> formatter) {
            this.columns = columns;
            this.formatter = formatter;
        }
    }

    static class GetBalance {
        public GetBalance() {}
    }

    static class GetStatements {

        String accountId;
        Supplier<String> columns;

        public GetStatements(String accountId, Supplier<String> columns) {
            this.accountId = accountId;
            this.columns = columns;
        }
    }

    static class StatementList {

        String accountId;
        Queue<Transaction> transactions;
        Supplier<String> columns;

        public StatementList(String accountId, Queue<Transaction> transactions, Supplier<String> columns) {
            this.accountId = accountId;
            this.transactions = transactions;
            this.columns = columns;
        }
    }
}
