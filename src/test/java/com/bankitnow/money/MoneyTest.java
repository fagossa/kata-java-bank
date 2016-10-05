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
import static javaslang.control.Option.none;
import static javaslang.control.Option.some;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class MoneyTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {Money.of(10, USD), Money.of(10, USD), some(Money.of(20, USD)), some(Money.of(0, USD))},
                {Money.of(10, USD), Money.of(-10, USD), some(Money.of(20, USD)), some(Money.of(0, USD))},
                {Money.of(-10, USD), Money.of(10, USD), some(Money.of(20, USD)), some(Money.of(0, USD))},
                {Money.of(-10, USD), Money.of(-10, USD), some(Money.of(20, USD)), some(Money.of(0, USD))},
                {Money.of(10, USD), Money.of(10, EUR), none(), none()},
                {Money.of(10, EUR), Money.of(10, USD), none(), none()}
        });
    }

    @Parameterized.Parameter
    public Money firstMoney;

    @Parameterized.Parameter(value = 1)
    public Money secondMoney;

    @Parameterized.Parameter(value = 2)
    public Option<Money> totalPlus;

    @Parameterized.Parameter(value = 3)
    public Option<Money> totalMinus;

    @Test
    public void should_validate_same_currency_when_plus() {
        //When
        final Try<Money> aTry = firstMoney.plus(secondMoney);
        final Option<Money> result = aTry.toOption();

        //Then
        assertThat(result).isEqualTo(totalPlus);
    }

    @Test
    public void should_validate_same_currency_when_minus() {
        //When
        final Try<Money> aTry = firstMoney.minus(secondMoney);
        final Option<Money> result = aTry.toOption();

        //Then
        assertThat(result).isEqualTo(totalMinus);
    }
}
