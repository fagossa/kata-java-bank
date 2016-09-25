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

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class TransactionTest implements MoneySamples {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {UUID.randomUUID().toString(), zeroDollars, tenDollars, OffsetDateTime.now(), Transaction.Type.Deposit, true},
                {UUID.randomUUID().toString(), tenDollars, tenEuros, OffsetDateTime.now(), Transaction.Type.Deposit, true},
                {null, zeroDollars, tenDollars, OffsetDateTime.now(), Transaction.Type.Deposit, false},
                {UUID.randomUUID().toString(), null, tenEuros, OffsetDateTime.now(), Transaction.Type.Deposit, false},
                {UUID.randomUUID().toString(), zeroDollars, null, OffsetDateTime.now(), Transaction.Type.Deposit, false},
                {UUID.randomUUID().toString(), zeroDollars, tenEuros, null, Transaction.Type.Deposit, false},
                {UUID.randomUUID().toString(), zeroDollars, tenEuros, OffsetDateTime.now(), null, false}
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
