# Annual Report Manager

This project is a Spring Boot application that manages and organizes transactions and process data for yearly reporting. It is integrated with the ELK Stack (Elasticsearch, Logstash, Kibana) for logging and observability, and Prometheus for monitoring and observability. Spring Actuator is used for monitoring application health, metrics, and exposing them to Prometheus.

## Table of Contents

1. [Features](#features)
2. [Pre-requisites](#pre-requisites)
3. [Setup and Installation](#setup-and-installation)
4. [Running the Application](#running-the-application)
5. [ELK Stack Integration](#elk-stack-integration)
6. [Spring Actuator](#spring-actuator)
7. [Kibana Dashboard](#kibana-dashboard)
8. [Prometheus Integration](#prometheus-integration)
9. [Troubleshooting](#troubleshooting)

---

## Features

- **File Processing**: Monitors a directory for incoming report files, processes them, and stores them in a database.
- **REST API**: Provides endpoints for CRUD operations related to annual reports.
- **Database**: Integrated with PostgreSQL for data persistence.
- **Logging and Observability**:
    - **Logstash**: Sends logs to Logstash for processing.
    - **Elasticsearch**: Stores logs and makes them searchable.
    - **Kibana**: Visualizes logs and application metrics.
    - **Prometheus**: Monitors and collects metrics from the Spring Boot application.
- **Spring Actuator**: Exposes application health and metrics.

## Pre-requisites

Before setting up the project, ensure you have the following installed on your system:

- **Java 17** (for Spring Boot)
- **Docker** and **Docker Compose**
- **Maven** (for building the application)

## Setup and Installation

### 1. Clone the repository

```bash
git clone https://github.com/jmamarques/AnnualReportManager.git
cd AnnualReportManager
```

### 2. Build the Spring Boot Application

Navigate to the `AnnualReportManager` directory and build the project using Maven:

```bash
mvn clean package
```

### 3. Setup ELK Stack (Elasticsearch, Logstash, Kibana) and Prometheus

The project includes a `docker-compose.yml` file that sets up Elasticsearch, Logstash, Kibana, and Prometheus.

#### Elasticsearch, Logstash, Kibana (ELK Stack), and Prometheus
- **Elasticsearch**: Stores logs.
- **Logstash**: Processes and forwards logs.
- **Kibana**: Visualizes the logs and application metrics.
- **Prometheus**: Monitors and collects metrics from the Spring Boot application.

To start the ELK Stack and Prometheus:

```bash
cd docker
docker-compose up -d
```

This will start:
- **Elasticsearch** on port `9200`
- **Logstash** on port `5000`
- **Kibana** on port `5601`
- **Prometheus** on port `9090`
- **PostgreSQL** on port `5432`

### 4. Environment Configuration

Ensure your environment is properly configured by checking the `application.properties` file in `AnnualReportManager/src/main/resources/`.

#### Sample `application.properties`

```properties
# PostgreSQL
spring.datasource.url=jdbc:postgresql://db:5432/production
spring.datasource.username=wallet
spring.datasource.password=secretWallet

# Actuator
management.endpoints.web.exposure.include=*
management.endpoint.health.show-details=always
management.metrics.export.prometheus.enabled=true

# ELK Integration
logging.level.net.logstash=INFO
logstash.destination=logstash:5000

# Prometheus
management.prometheus.metrics.export.enabled=true
```

You can configure your logging destination (Logstash) and Prometheus settings according to your environment.

## Running the Application

Once everything is set up, you can run the Spring Boot application:

```bash
docker-compose up --build
```

The application will start and be available at [http://localhost:8080](http://localhost:8080).

## ELK Stack Integration

### Logstash Configuration

Logs from the Spring Boot application are sent to Logstash at port `5000` using the Logback appender.

Ensure the following is added to your `logback-spring.xml`:

```xml
<appender name="stash" class="net.logstash.logback.appender.LogstashTcpSocketAppender">
    <destination>logstash:5000</destination>
    <encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
        <providers>
            <timestamp>
                <timeZone>UTC</timeZone>
            </timestamp>
            <logLevel />
            <message />
            <loggerName />
            <threadName />
            <mdc />
        </providers>
    </encoder>
</appender>
```

### Elasticsearch

Logs sent from Logstash will be indexed in Elasticsearch and can be searched using the Kibana dashboard.

### Kibana

To access Kibana, go to [http://localhost:5601](http://localhost:5601). Kibana will allow you to visualize logs and metrics from your application.

1. Go to **Stack Management** > **Index Patterns**.
2. Create an index pattern for `logstash-*`.
3. View the logs using **Discover** or create visualizations and dashboards under **Dashboard**.

## Spring Actuator

Spring Actuator exposes various endpoints to monitor and manage your application. Some useful endpoints include:

- `/actuator/health`: Health status of the application.
- `/actuator/metrics`: Metrics on application performance.
- `/actuator/loggers`: Log configuration details.

These endpoints are exposed at [http://localhost:8080/actuator](http://localhost:8080/actuator).

## Prometheus Integration

Prometheus collects metrics from the Spring Boot application via the `/actuator/prometheus` endpoint.

### Setup Prometheus

Ensure Prometheus is configured to scrape the Spring Boot application's metrics endpoint. Update the Prometheus configuration file (`prometheus.yml`) as follows:

```yaml
scrape_configs:
  - job_name: 'spring-boot'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['app:8080']
```

### Access Prometheus

To access Prometheus, go to [http://localhost:9090](http://localhost:9090). Here you can query and visualize the metrics collected from your Spring Boot application.

### Query Metrics

In the Prometheus web interface, you can use PromQL to query and visualize metrics. For example:

- **Total number of HTTP requests**: `http_server_requests_total`
- **JVM memory usage**: `jvm_memory_used_bytes`

## Kibana Dashboard

Once Kibana is set up, you can access the logs sent from the application, visualize them, and create dashboards based on various log metrics.

### Create a Kibana Visualization:
1. Go to **Visualize**.
2. Choose **Create Visualization**.
3. Choose the type of visualization (e.g., **Bar Chart**, **Pie Chart**).
4. Use the `logstash-*` index to build your visualizations.

You can add these visualizations to a Kibana **Dashboard** for easy access.

## Troubleshooting

### 1. Logstash Connection Refused

If you encounter `Connection refused` errors in your Spring Boot logs, ensure that:
- Logstash is running and reachable at `logstash:5000`.
- Both the application and Logstash are on the same Docker network.

### 2. Logs Not Appearing in Kibana

- Ensure that the logs are being sent to Logstash by checking the Logstash container logs.
- Make sure Elasticsearch is running and accepting data from Logstash.

### 3. Prometheus Metrics Not Appearing

- Ensure Prometheus is configured correctly to scrape the `/actuator/prometheus` endpoint.
- Check the Prometheus logs for any errors or misconfigurations.

---

This README file provides a comprehensive guide for setting up, running, and monitoring the project using Elasticsearch, Logstash, Kibana, Prometheus, and Spring Actuator in a Linux environment.