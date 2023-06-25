# Always Right Temp Inc - Anomaly Detection System

This repository contains the source code for the Anomaly Detection System for Always Right Temp Inc. The system is divided into four main services:

1. `anomaly-detection-service`: This service is responsible for generating temperature measurements and detecting anomalies based on provided algorithms.
2. `rest-api-service`: This service provides a RESTful API to fetch anomalies and their related details.
3. `stream-api-service`: This service streams anomaly events in real time.
4. `temperature-measurements-generator`: This service mocks the sensors input data.

All services are developed using Spring Boot and Java 17.

## Project Structure

The project follows a monorepo structure and contains the following directories:

- `anomaly-detection-service`: Contains the source code for the anomaly detection service.
- `rest-api-service`: Contains the source code for the REST API service.
- `stream-api-service`: Contains the source code for the stream API service.
- `temperature-measurements-generator`: Contains the source code for the temperature-measurements-generator.

Each service is a standalone Spring Boot application and can be built and run independently.

## Getting Started

### Prerequisites

- Java 17
- Docker
- Docker-compose

### Building the Project

From the project root directory, run the following command:

```bash
./gradlew clean build
```
This will build all the services. Built JAR files can be found in the `build/libs` directory of each service.

### Running the Services
In order to run this project fallow these steps.


#### Step 1: Make sure you have project build.
```bash
./gradlew clean build
```
#### Step 2: Run docker compose to setup kafka and other dependencies.
```bash
docker-compose up
```
#### Step 3: Run the measurement generator.

```bash
java -jar temperature-measurment-generator/build/libs/temperature-measurment-generator-0.0.1-SNAPSHOT.jar
````
#### Step 4: Run the anomaly detector.

```bash
java -jar anomaly-detection-service/build/libs/anomaly-detection-service-0.0.1-SNAPSHOT.jar
````
#### Step 5: Run the rest api.

```bash
java -jar rest-api-service/build/libs/rest-api-service-0.0.1-SNAPSHOT.jar
````
#### Step 6: Run the stream api.

```bash
java -jar stream-api-service/build/libs/stream-api-service-0.0.1-SNAPSHOT.jar
````

#### Step 7: Now you can check the endpoints.
[REST_API](http://localhost:8080/)

[STREAM_API](http://localhost:8081/anomalies/stream)

