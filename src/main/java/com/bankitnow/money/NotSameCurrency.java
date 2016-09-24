package com.bankitnow.money;

class NotSameCurrency extends RuntimeException {

    NotSameCurrency(Currency first, Currency second) {
        super(String.format("Invalid transaction between %s and %s", first, second));
    }

}
