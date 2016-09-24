package com.bankitnow.account;

import com.bankitnow.money.Balance;
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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AccountTest {

    @Mock
    private AccountJournal journal;

    @Mock
    PrintStream out;

    @Test
    public void should_increase_balance_when_deposit() {
        //Given
        Balance initialBalance = Balance.of(10, USD);
        String aRandomId = UUID.randomUUID().toString();
        Account anAccount = new Account(aRandomId, initialBalance, journal);
        final OffsetDateTime aMoment = OffsetDateTime.now();
        Transaction expectedTransaction = new Transaction.TransactionBuilder()
                .forAccount(aRandomId)
                .withOperation(Balance.of(100, USD))
                .withNewBalance(Balance.of(110, USD))
                .at(aMoment)
                .withType(Transaction.Type.Deposit)
                .build().get();

        // When
        Balance depositBalance = Balance.of(100, USD);
        anAccount.deposit(depositBalance, aMoment);

        // Then
        verify(journal).send(expectedTransaction);
    }

    @Test
    public void should_not_increase_balance_when_deposit_different_currency() {
        //Given
        Balance initialBalance = Balance.of(10, USD);
        String aRandomId = UUID.randomUUID().toString();
        Account anAccount = new Account(aRandomId, initialBalance, journal);
        final OffsetDateTime aMoment = OffsetDateTime.now();

        // When
        Balance depositBalance = Balance.of(100, EUR);
        anAccount.deposit(depositBalance, aMoment);

        // Then
        verify(journal, never()).send(any());
    }

    @Test
    public void should_decrease_balance_when_withdraw() {
        //Given
        Balance initialBalance = Balance.of(100, USD);
        String aRandomId = UUID.randomUUID().toString();
        final OffsetDateTime aMoment = OffsetDateTime.now();
        Account anAccount = new Account(aRandomId, initialBalance, journal);
        Transaction expectedTransaction = new Transaction.TransactionBuilder()
                .forAccount(aRandomId)
                .withOperation(Balance.of(30, USD))
                .withNewBalance(Balance.of(70, USD))
                .at(aMoment)
                .withType(Transaction.Type.Withdraw)
                .build().get();

        // When
        Balance withdrawBalance = Balance.of(30, USD);
        anAccount.withdraw(withdrawBalance, aMoment);

        // Then
        verify(journal).send(expectedTransaction);
    }

    @Test
    public void should_decrease_balance_when_withdraw_different_currency() {
        //Given
        Balance initialBalance = Balance.of(100, USD);
        String aRandomId = UUID.randomUUID().toString();
        final OffsetDateTime aMoment = OffsetDateTime.now();
        Account anAccount = new Account(aRandomId, initialBalance, journal);

        // When
        Balance withdrawBalance = Balance.of(50, EUR);
        anAccount.withdraw(withdrawBalance, aMoment);

        // Then
        verify(journal, never()).send(any());
    }

    @Test
    public void should_print_story() {
        //Given
        String aRandomId = UUID.randomUUID().toString();
        Account anAccount = new Account(aRandomId, Balance.of(0, USD), new VolatileJournal());
        final OffsetDateTime aMoment = aDate();

        // When
        anAccount.deposit(Balance.of(5, USD), aMoment);
        anAccount.deposit(Balance.of(10, USD), aMoment);
        anAccount.withdraw(Balance.of(2, USD), aMoment);
        anAccount.history(out, (transaction) ->
                String.format(
                        "%s, %s, %s, %s",
                        transaction.balance().amt(),
                        transaction.operation().amt(),
                        transaction.type(),
                        transaction.dateTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                )
        );

        // Then
        verify(out, times(1)).println("5, 5, Deposit, 2013-05-26T10:22:17+02:00");
        verify(out, times(1)).println("10, 15, Deposit, 2013-05-26T10:22:17+02:00");
        verify(out, times(1)).println("2, 13, Withdraw, 2013-05-26T10:22:17+02:00");
    }

    private OffsetDateTime aDate() {
        final LocalDateTime localDateTime = LocalDateTime.parse("2013-05-26T10:22:17");
        final ZoneOffset zoneOffset = ZoneOffset.of("+02:00");
        return OffsetDateTime.of(localDateTime, zoneOffset);
    }
}