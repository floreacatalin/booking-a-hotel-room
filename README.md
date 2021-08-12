# Booking a hotel room 
This project aims to solve the following coding challenge:

> Post-Covid scenario:
>
> People are now free to travel everywhere but because of the pandemic, a lot of hotels went bankrupt. Some former famous travel places are left with only one hotel.
>
>You’ve been given the responsibility to develop a booking API for the very last hotel in Cancun.
>
>The requirements are:
>- API will be maintained by the hotel’s IT department.
>- As it’s the very last hotel, the quality of service must be 99.99 to 100% => no downtime
>- For the purpose of the test, we assume the hotel has only one room available
>- To give a chance to everyone to book the room, the stay can’t be longer than 3 days and can’t be reserved more than 30 days in advance.
>- All reservations start at least the next day of booking,
>- To simplify the use case, a “DAY’ in the hotel room starts from 00:00 to 23:59:59.
>- Every end-user can check the room availability, place a reservation, cancel it or modify it.
>- To simplify the API is insecure.

## Project infrastructure

The project consists of:
- an H2 database cluster with two (or more) nodes
- a Netflix Eureka cluster with two (or more) nodes, for service discovery
- a Booking API (Spring Boot) server running on two (or more) instances
- a Booking Client (Spring Boot) application with a Swagger interface, that simply acts as a client service to the Booking API - it can be viewed as the front-end used by the hotel staff   ​

![Infrastructure diagram](diagrams/booking_infrastructure,png?raw=true "Infrastructure diagram")

*Note*: Ideally, all these nodes and instances would run on different computers, but for the purposes of this demonstration, they are all configured to run on `localhost`.

## Approach

I built my solution with two main goals in mind:
- **Achieving high availability**

Each project component, with the exception of the client-service, is run on multiple replicas (by default 2). This design prevents any single (database/Eureka/Booking API) node that unexpectedly dies from "taking down" the entire application with it. 

The client service calls the Booking API by connecting to Eureka, with the help of the Netflix Feign library. It then uses the Spring Cloud LoadBalancer library to do client-side load balancing when choosing which of the running Booking API instances to communicate with. 

Each new Booking API server instance registers itself with Eureka and interacts with the database cluster. If one of the API servers goes down, it typically takes around 20 seconds until the Booking Client is informed by Eureka not to communicate with that server instance anymore. 

Quoting from the H2 documentation, the H2 cluster consists of *two database servers*, and on each server there *is a copy of the same database. If both servers run, each database operation is executed on both computers. If one server fails (power, hardware or network failure), the other server can still continue to work. From this point on, the operations will be executed only on one server until the other server is back up.*

The Eureka cluster is conceptually similar to the H2 cluster - each of its peers replicates all the registered Eureka clients, and if one of the peers dies, the other ones will take over its load. 

- **Preventing race conditions**

The main race condition I was concerned with was the danger of two persons succeeding to book the room for the same date(s). The database design prevents this from happening through a UNIQUE constraint placed on the DATE column of the BOOKED_DAY table. 

![Database diagram](diagrams/booking_database.png?raw=true "Database diagram")

If an attempted booking save/update operation conflicts with the dates of another booking, I catch the exception thrown by Hibernate, make sure it is related to the mentioned unique constraint, abort the operation and inform the client of the conflict of dates.

I also use optimistic locking on the Booking table, in order to prevent the client from acting upon stale information, i.e. to prevent any lost-update problems.

## Usage

### Prerequisites

Java and Maven installations are prerequisites of running this project on a local machine. 

Another prerequisite is adding the following two lines to the `C:\Windows\System32\drivers\etc\hosts` (or equivalent) file:

`127.0.0.1 eureka-peer-1`

`127.0.0.1 eureka-peer-2`

(This is necessary because Eureka clusters misfunction when peers use the `localhost` hostname.)

### Running the project

I included a batch script called `start_booking_app.bat` which configures and starts the entire infrastructure.
Therefore, on Windows, it is enough to enter the script's name into a command line within the project folder:
```bash
start_booking_app.bat
```
If additional nodes are desired, they can be started from new command lines, by using the commands included in the script. The Java and Maven commands can be used in the same way on UNIX environments.  

### Accessing the client interface

Visit [http://localhost:9999/swagger-ui/](http://localhost:9999/swagger-ui/) and use the Swagger interface to make calls to the Booking API.

The H2 database console can be accessed at [http://localhost:8081/h2-console/](http://localhost:8081/h2-console/) (the 8081 can be replaced with the port of any of the functioning API nodes). 

The consoles of Eureka peers can be accessed at [http://localhost:9011/](http://localhost:9011/) and at [http://localhost:9012/](http://localhost:9012/) .

## Assumptions
- I externalized the maximum length of stays (by default 3) and the maximum number of days one can book in advance (by default 30) into application properties (`booking.max.length` and `room.max.availability`) that can be configured before runtime.
- I assumed that the dates of cancelled bookings do not need to be kept in the system. 
- I assumed that checking the room availability means checking only the next `room.max.availability` dates. 
- I assumed that a booking cannot contain dates that are farther than `room.max.availability` days away from the current date. 

