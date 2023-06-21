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
- Kubernetes (optional, for deployment)
- Helm (optional, for deployment)

### Building the Project

From the project root directory, run the following command:

```bash
./gradlew clean build
```
This will build all the services. Built JAR files can be found in the `build/libs` directory of each service.

### Running the Services

Each service can be started as a standalone Spring Boot application:
```bash
java -jar service-name/build/libs/service-name.jar
```

Replace `service-name` with the actual service directory and JAR file name.

## Deployment

The repository includes Helm charts for deploying the services to a Kubernetes cluster. Detailed deployment instructions will be provided later.
