# Intro

This is simple task manager application that is devided into 2 packages which are ment to represent two microservices
- user management service
  - contains a very naive token generation system and
  - user management.
- task management service
  - a simple task management apis.

# How to run

This project you don't need to do alot to run since it 
uses the new [spring boot docker compose starter](https://spring.io/blog/2023/06/21/docker-compose-support-in-spring-boot-3-1).

This mean the databases gets spinned up when the application
start on a docker container.

This means you will need docker installed on you machine.

To run simple past the following on the terminal.
```
 ./mvnw spring-boot:run
```

# Endpoints

To start testing download the [Postman Collection](/postmancollection/thunder-collection_taskmanager_postman.json)

All endpoints that don't have a prefix `/auth/` require authentication.

## Step
- Signup as a user on the endpoint `/auth/login`
- Fetch token from the endpoint `/auth/login`
- using token received, access other endpoints.


# Tests

Integrations test that depend on test containers that are present on the project can be run
```
./mvnw clean test
```
## coverage

can be obtain by running
```
./mvnw clean verify
```

then on target folder access `target/site/jacoco/index.html`