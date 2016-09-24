package com.bankitnow.account;

import com.bankitnow.money.Balance;

import java.time.OffsetDateTime;
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

    static class History {
        final Supplier<String> columns;
        final Function<Transaction, String> formatter;

        public History(Supplier<String> columns, Function<Transaction, String> formatter) {
            this.columns = columns;
            this.formatter = formatter;
        }
    }

    static class GetBalance {
        public GetBalance() {}
    }

    static class HistoryForAccount {

        String accountId;
        Supplier<String> columns;
        Function<Transaction, String> formatter;

        public HistoryForAccount(String accountId, Supplier<String> columns, Function<Transaction, String> formatter) {
            this.accountId = accountId;
            this.columns = columns;
            this.formatter = formatter;
        }
    }
}
