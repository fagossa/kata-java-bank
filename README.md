# Bank account kata [![Build Status](https://travis-ci.org/fagossa/kata-java-bank.svg?branch=master)](https://travis-ci.org/fagossa/kata-java-bank)

Think of your personal bank account experience When in doubt, go for the simplest solution.
 
## Requirements

* Deposit and Withdrawal
* Account statement (date, amount, balance)
* Statement printing
 
## User Stories

### US 1

```
In order to save money
As a bank client
I want to make a deposit in my account
```

### US 2

```
In order to retrieve some or all of my savings
As a bank client
I want to make a withdrawal from my account
```

### US 3

```
In order to check my operations
As a bank client
I want to see the history (operation, date, amount, balance)  of my operations
```

## About the implementation

### Master branch

This is a simple implementation based on Pojos.

### with-akka

This one is based on [Akka actors](http://doc.akka.io/docs/akka/2.4.10/general/actors.html).

## Testing

As a maven project you need to execute the following command in order to run the test:

```
$ mvn clean test
```
