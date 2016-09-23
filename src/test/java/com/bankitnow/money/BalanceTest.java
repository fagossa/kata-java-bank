package com.bankitnow.money;

import javaslang.control.Option;
import javaslang.control.Try;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static com.bankitnow.money.Currency.EUR;
import static com.bankitnow.money.Currency.USD;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class BalanceTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {Balance.of(10, USD), Balance.of(10, USD), Option.some(Balance.of(20, USD)), Option.some(Balance.of(0, USD))},
                {Balance.of(10, USD), Balance.of(10, EUR), Option.none(), Option.none()}
        });
    }

    @Parameterized.Parameter
    public Balance firstBalance;

    @Parameterized.Parameter(value = 1)
    public Balance secondBalance;

    @Parameterized.Parameter(value = 2)
    public Option<Balance> totalPlus;

    @Parameterized.Parameter(value = 3)
    public Option<Balance> totalMinus;

    @Test
    public void should_validate_same_currency_when_plus() {
        //When
        final Try<Balance> aTry = firstBalance.plus(secondBalance);
        final Option<Balance> result = aTry.toOption();

        //Then
        assertThat(result).isEqualTo(totalPlus);
    }

    @Test
    public void should_validate_same_currency_when_minus() {
        //When
        final Try<Balance> aTry = firstBalance.minus(secondBalance);
        final Option<Balance> result = aTry.toOption();

        //Then
        assertThat(result).isEqualTo(totalMinus);
    }
}
