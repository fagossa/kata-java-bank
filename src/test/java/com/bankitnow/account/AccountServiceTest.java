package com.bankitnow.account;

import com.bankitnow.money.Balance;
import javaslang.control.Try;
import org.junit.Test;

import java.io.IOException;
import java.time.OffsetDateTime;

import static com.bankitnow.account.Account.withRandomId;
import static com.bankitnow.money.Currency.USD;
import static org.assertj.core.api.Assertions.assertThat;

public class AccountServiceTest {

    private AccountRepository repository = new AccountRepository();
    private AccountService service = new AccountService(repository);

    @Test
    public void should_increase_balance_when_deposit() throws IOException {
        //Given
        Balance initialBalance = Balance.of(10, USD);
        Balance depositBalance = Balance.of(100, USD);
        Balance expectedBalance = Balance.of(110, USD);

        // When
        Account anAccount = new Account(withRandomId(), OffsetDateTime.now(), initialBalance);
        final Try<Account> result = service.deposit(anAccount, depositBalance);

        // Then
        final Account updatedAccount = result.getOrElseThrow(() -> new IllegalStateException("Expected a success"));
        assertThat(updatedAccount.balance()).isEqualTo(expectedBalance);
    }

    @Test
    public void should_decrease_balance_when_withdraw() throws IOException {
        //Given
        Balance initialBalance = Balance.of(100, USD);
        Balance depositBalance = Balance.of(50, USD);
        Balance expectedBalance = Balance.of(50, USD);

        // When
        Account anAccount = new Account(withRandomId(), OffsetDateTime.now(), initialBalance);
        final Try<Account> result = service.withdraw(anAccount, depositBalance);

        // Then
        final Account updatedAccount = result.getOrElseThrow(() -> new IllegalStateException("Expected a success"));
        assertThat(updatedAccount.balance()).isEqualTo(expectedBalance);
    }
}