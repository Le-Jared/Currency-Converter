# Currency Converter Application
This repository contains the Currency Converter Application developed in Java. The application is designed to perform currency exchanges for a set of users based on a transaction file. The project incorporates features such as loading JSON files, processing transactions, and writing the updates back into a JSON file.

### Features
1. Loads user and exchange rate information from JSON files.
2. Processes a transaction file, performing currency exchanges for users.
3. Validates transactions, including checking for sufficient funds, validating the currency code, and handling various potential errors.
4. Writes the updated user balances back to a JSON file.

### Application Structure
The application consists of the following main classes:

* CurrencyConverter: The main class responsible for orchestrating the currency conversion operations.
* User: A model class representing a user with a wallet containing different currencies.
* Rate: A model class representing the exchange rate for a particular currency.
Unit tests are also included to validate the functionality of the Currency Converter.

### Usage
1. Clone the repository
2. Import the project into your favorite IDE 
3. The main method resides in the CurrencyConverter class, which can be run directly.
4. Ensure that the required JSON files (users.json, fx_rates.json) and the transactions.txt file are present in the specified paths.

### Test Coverage
The project includes unit tests written with Junit5 and Mockito. These tests cover various scenarios including:

* Loading exchange rates
* Processing transactions
* Writing updated user information to a JSON file
* Handling invalid transaction lines, insufficient funds, and unknown users

### Dependencies
* Java 8 or later
* Jackson for JSON processing
* Log4j2 for logging
* JUnit5 and Mockito for testing
 
