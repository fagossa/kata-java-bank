package com.bankitnow.account;

import com.bankitnow.money.Balance;
import javaslang.control.Try;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static com.bankitnow.money.Currency.EUR;
import static com.bankitnow.money.Currency.USD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AccountTest {

    @Mock
    PrintStream out;

    @Mock
    private AccountJournal anEventJournal;

    private Balance zeroDollars = Balance.of(0, USD);

    private Balance fiveDollars = Balance.of(5, USD);

    private Balance tenDollars = Balance.of(10, USD);

    private Balance fifteenDollars = Balance.of(15, USD);

    private Balance twentyDollars = Balance.of(20, USD);

    private Balance fiveEuros = Balance.of(5, EUR);

    private Balance tenEuros = Balance.of(10, EUR);

    @Test
    public void should_increase_balance_when_deposit() {
        // Given
        String anId = UUID.randomUUID().toString();
        Account anAccount = new Account(anId, tenDollars, anEventJournal);
        OffsetDateTime aMoment = OffsetDateTime.now();
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
    public void should_not_increase_balance_when_deposit_different_currency() {
        // Given
        String anId = UUID.randomUUID().toString();
        Account anAccount = new Account(anId, tenDollars, anEventJournal);
        OffsetDateTime aMoment = OffsetDateTime.now();

        // When
        anAccount.deposit(tenEuros, aMoment);

        // Then
        verify(anEventJournal, never()).send(any());
    }

    @Test
    public void should_decrease_balance_when_withdraw() {
        // Given
        String anId = UUID.randomUUID().toString();
        OffsetDateTime aMoment = OffsetDateTime.now();
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
    public void should_decrease_balance_when_withdraw_different_currency() {
        // Given
        String anId = UUID.randomUUID().toString();
        OffsetDateTime aMoment = OffsetDateTime.now();
        Account anAccount = new Account(anId, tenDollars, anEventJournal);

        // When
        anAccount.withdraw(fiveEuros, aMoment);

        // Then
        verify(anEventJournal, never()).send(any());
    }

    @Test
    public void should_print_story() {
        // Given
        String anId = UUID.randomUUID().toString();
        Account anAccount = new Account(anId, zeroDollars, new VolatileJournal());
        OffsetDateTime aMoment = aDateTime();

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

    private OffsetDateTime aDateTime() {
        final LocalDateTime localDateTime = LocalDateTime.parse("2016-09-24T10:22:17");
        final ZoneOffset zoneOffset = ZoneOffset.of("+02:00");
        return OffsetDateTime.of(localDateTime, zoneOffset);
    }
}