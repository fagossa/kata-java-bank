package com.bankitnow.account;

import javaslang.control.Try;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.PrintStream;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.OffsetDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AccountTest implements DataSamples {

    @Mock
    PrintStream out;

    @Mock
    private AccountJournal anEventJournal;

    @Test
    public void should_increase_balance_when_deposit() {
        // Given
        String anId = aRandomId();
        Account anAccount = new Account(anId, tenDollars, anEventJournal);
        OffsetDateTime aMoment = now();
        Transaction expectedTransaction = new Transaction.TransactionBuilder()
                .forAccount(anId)
                .withOperation(tenDollars)
                .withNewBalance(twentyDollars)
                .at(aMoment)
                .withType(Transaction.Type.Deposit)
                .build().get();

        // When
        when(anEventJournal.send(any(Transaction.class))).thenReturn(Try.success(expectedTransaction));
        anAccount.deposit(tenDollars, aMoment);

        // Then
        verify(anEventJournal).send(expectedTransaction);
        assertThat(anAccount.balance()).isEqualTo(twentyDollars);
    }

    @Test
    public void should_not_increase_balance_when_journal_fails() {
        // Given
        Account anAccount = new Account(aRandomId(), tenDollars, anEventJournal);

        // When
        when(anEventJournal.send(any())).thenReturn(Try.failure(new IllegalStateException()));
        anAccount.deposit(tenDollars, now());

        // Then
        assertThat(anAccount.balance()).isEqualTo(tenDollars);
    }

    @Test
    public void should_not_increase_balance_when_deposit_different_currency() {
        // Given
        Account anAccount = new Account(aRandomId(), tenDollars, anEventJournal);

        // When
        anAccount.deposit(tenEuros, now());

        // Then
        verify(anEventJournal, never()).send(any());
    }

    @Test
    public void should_decrease_balance_when_withdraw() {
        // Given
        String anId = aRandomId();
        OffsetDateTime aMoment = now();
        Account anAccount = new Account(anId, twentyDollars, anEventJournal);
        Transaction expectedTransaction = new Transaction.TransactionBuilder()
                .forAccount(anId)
                .withOperation(fiveDollars)
                .withNewBalance(fifteenDollars)
                .at(aMoment)
                .withType(Transaction.Type.Withdraw)
                .build().get();

        // When
        when(anEventJournal.send(any(Transaction.class))).thenReturn(Try.success(expectedTransaction));
        anAccount.withdraw(fiveDollars, aMoment);

        // Then
        verify(anEventJournal).send(expectedTransaction);
        assertThat(anAccount.balance()).isEqualTo(fifteenDollars);
    }

    @Test
    public void should_not_decrease_balance_when_journal_fails() {
        // Given
        Account anAccount = new Account(aRandomId(), twentyDollars, anEventJournal);

        // When
        when(anEventJournal.send(any())).thenReturn(Try.failure(new IllegalStateException()));
        anAccount.withdraw(fiveDollars, now());

        // Then
        assertThat(anAccount.balance()).isEqualTo(twentyDollars);
    }

    @Test
    public void should_decrease_balance_when_withdraw_different_currency() {
        // Given
        Account anAccount = new Account(aRandomId(), tenDollars, anEventJournal);

        // When
        anAccount.withdraw(fiveEuros, now());

        // Then
        verify(anEventJournal, never()).send(any());
    }

    @Test
    public void should_print_story() {
        // Given
        Account anAccount = new Account(aRandomId(), zeroDollars, new VolatileJournal());
        OffsetDateTime aMoment = aFixedDateTime();

        // When
        anAccount.deposit(twentyDollars, aMoment);
        anAccount.deposit(tenDollars, aMoment);
        anAccount.withdraw(fiveDollars, aMoment);
        anAccount.history(out,
                () -> "AMT, BALANCE, TYPE, DATE",
                (transaction) -> String.format(
                        "%s, %s, %s, %s",
                        transaction.operation().amt(),
                        transaction.balance().amt(),
                        transaction.type(),
                        transaction.dateTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                )
        );

        // Then
        verify(out, times(1)).println("AMT, BALANCE, TYPE, DATE");
        verify(out, times(1)).println("20, 20, Deposit, 2016-09-24T10:22:17+02:00");
        verify(out, times(1)).println("10, 30, Deposit, 2016-09-24T10:22:17+02:00");
        verify(out, times(1)).println("5, 25, Withdraw, 2016-09-24T10:22:17+02:00");
    }

}