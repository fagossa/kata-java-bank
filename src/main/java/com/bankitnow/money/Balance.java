package com.bankitnow.money;

import javaslang.control.Try;

import java.util.Objects;

import static java.lang.Math.*;

public class Balance {

    private final int amt;
    private final Currency currency;

    private Balance(int amt, Currency currency) {
        this.amt = abs(amt);
        this.currency = currency;
    }

    public int amt() {
        return amt;
    }

    public Try<Balance> plus(Balance newValue) {
        return this.currency == newValue.currency
                ? Try.success(of(amt + (newValue.amt), currency))
                : Try.failure(new NotSameCurrency(this.currency, newValue.currency));
    }

    public Try<Balance> minus(Balance newValue) {
        return this.currency == newValue.currency
                ? Try.success(of(amt - (newValue.amt), currency))
                : Try.failure(new NotSameCurrency(this.currency, newValue.currency));
    }

    public static Balance of(int amt, Currency currency) {
        return new Balance(amt, currency);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Balance)) return false;
        Balance balance = (Balance) o;
        return amt == balance.amt &&
                currency == balance.currency;
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
