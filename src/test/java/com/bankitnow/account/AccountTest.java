package com.bankitnow.account;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;
import akka.testkit.TestProbe;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.OffsetDateTime.now;

public class AccountTest implements DataSamples {

    static ActorSystem system;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create();
    }

    @AfterClass
    public static void teardown() {
        JavaTestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void should_increase_balance_when_deposit() {
        new JavaTestKit(system) {{
            // Given
            final TestProbe anEventJournal = new TestProbe(system, "eventJournal");
            String anId = aRandomId();
            ActorRef anAccount = system.actorOf(
                    Account.props(anId, tenDollars, anEventJournal.ref()),
                    "Account_" + anId
            );
            OffsetDateTime aMoment = now();
            Transaction expectedTransaction = new Transaction.TransactionBuilder()
                    .forAccount(anId)
                    .withOperation(tenDollars)
                    .withNewBalance(twentyDollars)
                    .at(aMoment)
                    .withType(Transaction.Type.Deposit)
                    .build().get();

            // When
            final TestProbe worker = new TestProbe(system, "worker");
            worker.send(anAccount, new AccountEvents.Deposit(tenDollars, aMoment));
            worker.send(anAccount, new AccountEvents.GetBalance());

            // Then
            anEventJournal.expectMsg(expectedTransaction);
            worker.expectMsg(twentyDollars);
        }};
    }

    @Test
    public void should_not_increase_balance_when_deposit_different_currency() {
        new JavaTestKit(system) {{
            // Given
            final TestProbe anEventJournal = new TestProbe(system, "eventJournal");
            String anId = aRandomId();
            ActorRef anAccount = system.actorOf(
                    Account.props(anId, tenDollars, anEventJournal.ref()),
                    "Account_" + anId
            );

            // When
            final TestProbe worker = new TestProbe(system, "worker");
            worker.send(anAccount, new AccountEvents.Deposit(tenEuros, now()));

            // Then
            anEventJournal.expectNoMsg();
        }};
    }

    @Test
    public void should_decrease_balance_when_withdraw() {
        new JavaTestKit(system) {{
            // Given
            final TestProbe anEventJournal = new TestProbe(system, "eventJournal");
            String anId = aRandomId();
            OffsetDateTime aMoment = now();
            ActorRef anAccount = system.actorOf(
                    Account.props(anId, twentyDollars, anEventJournal.ref()),
                    "Account_" + anId
            );
            Transaction expectedTransaction = new Transaction.TransactionBuilder()
                    .forAccount(anId)
                    .withOperation(fiveDollars)
                    .withNewBalance(fifteenDollars)
                    .at(aMoment)
                    .withType(Transaction.Type.Withdraw)
                    .build().get();

            // When
            final TestProbe worker = new TestProbe(system, "worker");
            worker.send(anAccount, new AccountEvents.Withdraw(fiveDollars, aMoment));
            worker.send(anAccount, new AccountEvents.GetBalance());

            // Then
            anEventJournal.expectMsg(expectedTransaction);
            worker.expectMsg(fifteenDollars);
        }};
    }

    @Test
    public void should_decrease_balance_when_withdraw_different_currency() {
        new JavaTestKit(system) {{
            // Given
            final TestProbe anEventJournal = new TestProbe(system, "eventJournal");
            String anId = aRandomId();
            ActorRef anAccount = system.actorOf(
                    Account.props(anId, tenDollars, anEventJournal.ref()),
                    "Account_" + anId
            );

            // When
            final TestProbe worker = new TestProbe(system, "worker");
            worker.send(anAccount, new AccountEvents.Withdraw(fiveEuros, now()));

            // Then
            anEventJournal.expectNoMsg();
        }};
    }

    @Test
    public void should_print_story() {
        new JavaTestKit(system) {{
            // Given
            final TestProbe worker = new TestProbe(system, "worker");
            String anId = aRandomId();
            final ActorRef volatileJournalRef = system.actorOf(VolatileJournal.props(worker.ref()), "Journal_" + anId);
            ActorRef anAccount = system.actorOf(Account.props(anId, zeroDollars, volatileJournalRef), "Account_" + anId);
            OffsetDateTime aMoment = aFixedDateTime();

            // When
            worker.send(anAccount, new AccountEvents.Deposit(twentyDollars, aMoment));
            worker.send(anAccount, new AccountEvents.Deposit(tenDollars, aMoment));
            worker.send(anAccount, new AccountEvents.Withdraw(fiveDollars, aMoment));
            worker.send(anAccount, new AccountEvents.History(
                    () -> "AMT, BALANCE, TYPE, DATE",
                    (transaction) -> String.format(
                            "%s, %s, %s, %s",
                            transaction.operation().amt(),
                            transaction.balance().amt(),
                            transaction.type(),
                            transaction.dateTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                    )
            ));

            // Then
            worker.expectMsg("AMT, BALANCE, TYPE, DATE");
            worker.expectMsg("20, 20, Deposit, 2016-09-24T10:22:17+02:00");
            worker.expectMsg("10, 30, Deposit, 2016-09-24T10:22:17+02:00");
            worker.expectMsg("5, 25, Withdraw, 2016-09-24T10:22:17+02:00");
        }};
    }

}