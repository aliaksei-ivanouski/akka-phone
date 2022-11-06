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
* Start Prometheus
    ```shell
    docker-compose -f docker-compose.local.yml up -d prometheus
    ```

* Start Grafana
    ```shell
    docker-compose -f docker-compose.local.yml up -d grafana
    ```
* Start application
    ```shell
    ./gradlew spring-boot:run -Dspring-boot.run.profiles=local
    ```

#### Swagger API
  * Available here http://localhost:8080/docs

#### Prometheus dashboard
  * Available here http://localhost:9090
  * Due to possible bug with Prometheus and Docker compose
    please check if the port 9090 has been exposed by calling
    ```shell 
    docker ps
    ```
    if it was not exposed please stop the container and run it by the following command
    ```shell
     docker run --name prometheus --rm -v <ABSOLUTE_PATH_TO_PROJECT>/akka-phone/src/main/resources/prometheus:/etc/prometheus -p 9090:9090 prom/prometheus
    ```
    
#### Grafana dashboard
  * Available here http://localhost:3000
  * login: admin
  * password: admin

#### RabbitMQ
  * Available here http://localhost:15672
  * login: guest
  * password: guest
