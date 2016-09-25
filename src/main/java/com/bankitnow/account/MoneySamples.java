package com.bankitnow.account;

import com.bankitnow.money.Balance;

import static com.bankitnow.money.Currency.EUR;
import static com.bankitnow.money.Currency.USD;

interface MoneySamples {

    Balance zeroDollars = Balance.of(0, USD);

    Balance fiveDollars = Balance.of(5, USD);

    Balance tenDollars = Balance.of(10, USD);

    Balance fifteenDollars = Balance.of(15, USD);

    Balance twentyDollars = Balance.of(20, USD);

    Balance fiveEuros = Balance.of(5, EUR);

    Balance tenEuros = Balance.of(10, EUR);

}
