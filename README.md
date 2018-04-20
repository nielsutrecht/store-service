# Stock Service

## Description

This is my submission to the coding test. The application is a Spring Boot 2 REST service that can handle stocks for a 
number of stores. On start-up it will create a supply of 100 unit for every known (hard coded) item in every store.

## Running

Either run the Application class from your IDE or build the service using gradle (`gradle clean build`) 
and run the service with `java -jar build/libs/stockservice-1.0-SNAPSHOT.jar`.

## Task completion

The task was to build a Java service to maintain the stock for a number of different stores. What has been done:

* Implement a `GET /store` end-point that returns a list of stores
* Implement a `GET /supply/{shopId}` end-point that returns the items available at the store, how many are available and the reserved amounts
* Implement a `GET /supply/{shopId}/{itemId}` that returns this result for a single item
* Implement a `PATCH /supply/{shopId}/{itemId}/amount` end-point that can set the supply for a certain item
* Implement a `POST /supply/{shopId}/{itemId}/reservation` end-point that allows you to make reservations for an item that expire after a certain amount of time
* Implement a `DELETE /supply/{shopId}/{itemId}` end-point that removes all supply and reservations for an item
* Unit tests for a few controller and service methods
* Integration test for the supply and reservation flows
* Validation on input:
  * Shop ID must exist
  * Item ID must exist
  * Can lower supply to negative 
  * Can't lower supply below the amount of reservations
  * Reservation duration can't be too low or too high
  * etc.
* Code coverage (line coverage) of 90%
* Error handlers to turn the exceptions into proper JSON error messages with correct HTTP status codes
* Use lombok for Data Transfer Objects, domain objects and entities. 

## Not completed

Due to time constrains the following items were not completed

* Storage of data in a database (the @Repository classes 'simulate' this with Java collections)
* A SPA front-end
* Security

The 'data' (items and stores) are also hard-coded. 