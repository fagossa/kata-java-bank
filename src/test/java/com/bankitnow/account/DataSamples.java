package com.bankitnow.account;

import com.bankitnow.money.Money;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static com.bankitnow.money.Currency.EUR;
import static com.bankitnow.money.Currency.USD;
import static java.time.OffsetDateTime.of;

interface DataSamples {

    Money zeroDollars = Money.of(0, USD);

    Money fiveDollars = Money.of(5, USD);

    Money tenDollars = Money.of(10, USD);

    Money fifteenDollars = Money.of(15, USD);

    Money twentyDollars = Money.of(20, USD);

    Money fiveEuros = Money.of(5, EUR);

    Money tenEuros = Money.of(10, EUR);

    default String aRandomId() {
        return UUID.randomUUID().toString();
    }

    default OffsetDateTime aFixedDateTime() {
        final LocalDateTime localDateTime = LocalDateTime.parse("2016-09-24T10:22:17");
        final ZoneOffset zoneOffset = ZoneOffset.of("+02:00");
        return of(localDateTime, zoneOffset);
    }
}
