package com.bankitnow.account;

import com.bankitnow.money.Balance;
import javaslang.control.Try;

class AccountService {
    private AccountRepository repository;

    AccountService(AccountRepository repository) {
        this.repository = repository;
    }

    Try<Account> deposit(Account anAccount, Balance toDeposit) {
        Try<Balance> maybeTotal = anAccount.balance().plus(toDeposit);
        return maybeTotal.map((total) ->
                new Account(
                        anAccount.id(),
                        anAccount.openingDate(),
                        total
                ));
    }

    Try<Account> withdraw(Account anAccount, Balance toWithdraw) {
        Try<Balance> maybeTotal = anAccount.balance().minus(toWithdraw);
        return maybeTotal.map((total) ->
                new Account(
                        anAccount.id(),
                        anAccount.openingDate(),
                        total
                )
        );
    }
}
