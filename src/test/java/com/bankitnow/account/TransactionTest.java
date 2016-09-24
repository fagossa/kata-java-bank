package com.bankitnow.account;

import com.bankitnow.money.Balance;
import javaslang.control.Try;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import static com.bankitnow.money.Currency.EUR;
import static com.bankitnow.money.Currency.USD;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class TransactionTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {UUID.randomUUID().toString(), Balance.of(0, USD), Balance.of(10, USD), OffsetDateTime.now(), Transaction.Type.Deposit, true},
                {UUID.randomUUID().toString(), Balance.of(20, USD), Balance.of(10, EUR), OffsetDateTime.now(), Transaction.Type.Deposit, true},
                {null, Balance.of(0, USD), Balance.of(10, USD), OffsetDateTime.now(), Transaction.Type.Deposit, false},
                {UUID.randomUUID().toString(), null, Balance.of(10, EUR), OffsetDateTime.now(), Transaction.Type.Deposit, false},
                {UUID.randomUUID().toString(), Balance.of(0, USD), null, OffsetDateTime.now(), Transaction.Type.Deposit, false},
                {UUID.randomUUID().toString(), Balance.of(0, USD), Balance.of(10, EUR), null, Transaction.Type.Deposit, false},
                {UUID.randomUUID().toString(), Balance.of(0, USD), Balance.of(10, EUR), OffsetDateTime.now(), null, false}
        });
    }

    @Parameterized.Parameter
    public String accountId;

    @Parameterized.Parameter(value = 1)
    public Balance operation;

    @Parameterized.Parameter(value = 2)
    public Balance balance;

    @Parameterized.Parameter(value = 3)
    public OffsetDateTime moment;

    @Parameterized.Parameter(value = 4)
    public Transaction.Type type;

    @Parameterized.Parameter(value = 5)
    public Boolean expectedResult;

    @Test
    public void should_validate_correct_build() {
        final Try<Transaction> aTry = new Transaction.TransactionBuilder()
                .forAccount(accountId)
                .withNewBalance(balance)
                .withOperation(operation)
                .at(moment)
                .withType(type)
                .build();
        assertThat(aTry.isSuccess()).isEqualTo(expectedResult);
    }
}
