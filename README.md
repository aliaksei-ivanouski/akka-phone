## Akka Phone Booking API

### Compilation
This should be done after adding any sql migration scripts for database.
```shell
./gradlew clean build
```

### Running Locally

* Start Postgres
    ```shell
    docker-compose -f docker-compose.local.yml up -d db
    ```
* Start Redis
    ```shell
    docker-compose -f docker-compose.local.yml up -d redis
    ```
* Start RabbitMQ
    ```shell
    docker-compose -f docker-compose.local.yml up -d rabbitmq
    ```
* Start application
    ```shell
    ./gradlew spring-boot:run -Dspring-boot.run.profiles=local
    ```

#### Swagger API
  * Available here http://localhost:8080/docs

#### RabbitMQ
  * Available here http://localhost:15672
  * login: guest
  * password: guest
