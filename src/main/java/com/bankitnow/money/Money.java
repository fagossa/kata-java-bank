package com.bankitnow.money;

import javaslang.control.Try;

import java.util.Objects;

import static java.lang.Math.*;

public class Money {

    private final int amt;
    private final Currency currency;

    private Money(int amt, Currency currency) {
        this.amt = abs(amt);
        this.currency = currency;
    }

    public int amt() {
        return amt;
    }

    public Try<Money> plus(Money newValue) {
        return this.currency == newValue.currency
                ? Try.success(of(amt + (newValue.amt), currency))
                : Try.failure(new NotSameCurrency(this.currency, newValue.currency));
    }

    public Try<Money> minus(Money newValue) {
        return this.currency == newValue.currency
                ? Try.success(of(amt - (newValue.amt), currency))
                : Try.failure(new NotSameCurrency(this.currency, newValue.currency));
    }

    public static Money of(int amt, Currency currency) {
        return new Money(amt, currency);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money)) return false;
        Money money = (Money) o;
        return amt == money.amt &&
                currency == money.currency;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amt, currency);
    }

    @Override
    public String toString() {
        return "Balance{" +
                "amt=" + amt +
                ", currency=" + currency +
                '}';
    }
}
