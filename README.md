## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

What things you need to install the software and how to install them

```
JDK 11
```
```
Docker
```

## How to run

Before run the application , we should set up the container for pgAdmin and Postgresql by running the following command
```
docker-compose up
```
### How to access pgAdmin page

Open localhost:{PORT} in browser (default port 8002). You can customise the {PORT} in docker-compose.yml, line 24 as following:
```
ports:
    - <PORT>:80
```
Use default email: asyncworking@chui.com and password: admin to login. You can customise the email and password in docker-compose.yml, line 26 as following:
```
environment:
      PGADMIN_DEFAULT_EMAIL: {EMAIL}
      PGADMIN_DEFAULT_PASSWORD: {PASSWORD}
```
create a new server by entering postgresql in host name/address area, username postgres and password admin. You can customise email and password in docker-compose.yml.
```
environment:
      - POSTGRES_DB=postgres
      - POSTGRES_USER={username}
      - POSTGRES_PASSWORD={password}
```
## Running the tests

 Run the automated tests for this system by following:
 
 Gradle -> Tasks -> verification -> test
 
 or use cli in terminal using following:
 
 ```
 ./gradlew test

 ```
 it will automatically run checkStyle as the first step before run gradle test.
 
## And coding style tests

In this project, we use [CheckStyle](https://checkstyle.sourceforge.io/) for static code analysis.

### How to install CheckStyle

Install it to IDEA by opening Preferences -> Plugins, then search CheckStyle in marketplace and install.

## Swagger Instruction
**Swagger UI is only enable in development and UAT environment**
### View API Documentation via swagger ui
Open http://localhost:{{port}}/api/v1/swagger-ui.html in the web browser.

### Send Request via the api which required JWT token
Copy the JWT token <br>
-> Click `Authentication` button in Swagger UI page<br>
-> Paste the JWT token with `Bearer ` prefix.
```
Bearer <token>
```