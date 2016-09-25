package com.bankitnow.account;

import com.bankitnow.money.Balance;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static com.bankitnow.money.Currency.EUR;
import static com.bankitnow.money.Currency.USD;
import static java.time.OffsetDateTime.of;

interface DataSamples {

    Balance zeroDollars = Balance.of(0, USD);

    Balance fiveDollars = Balance.of(5, USD);

    Balance tenDollars = Balance.of(10, USD);

    Balance fifteenDollars = Balance.of(15, USD);

    Balance twentyDollars = Balance.of(20, USD);

    Balance fiveEuros = Balance.of(5, EUR);

    Balance tenEuros = Balance.of(10, EUR);

    default String aRandomId() {
        return UUID.randomUUID().toString();
    }

    default OffsetDateTime aFixedDateTime() {
        final LocalDateTime localDateTime = LocalDateTime.parse("2016-09-24T10:22:17");
        final ZoneOffset zoneOffset = ZoneOffset.of("+02:00");
        return of(localDateTime, zoneOffset);
    }
}

