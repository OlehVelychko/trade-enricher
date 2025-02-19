# Trade Enricher Service

## Description
This project provides a Spring Boot service for enriching trade data using product names from a static file. It supports CSV, JSON, and XML formats, allowing efficient trade enrichment and validation.

## Features

1. **Trade Data Enrichment**
   - Reads trade files and enriches them with product names from a Redis-backed product database.
   - Supports large datasets efficiently using asynchronous processing.
   
2. **File Format Support**
   - Accepts input in **CSV, JSON, and XML** formats.
   - Automatically detects and processes files based on content type.

3. **Asynchronous Processing**
   - Uses `CompletableFuture` for non-blocking execution.
   - Allows efficient handling of large files.

4. **Streaming Response**
   - Streams enriched trade data back to the client without consuming excessive memory.

5. **Validation & Error Handling**
   - Ensures date format validity (`yyyyMMdd`).
   - Logs missing product mappings and assigns "Missing Product Name" where necessary.

## Technologies Used

- **Java 17**
- **Spring Boot 3.4.2**
- **Spring Data Redis** for caching product data.
- **Jackson** for JSON/XML parsing.
- **Apache Commons CSV & OpenCSV** for CSV processing.
- **Maven** for dependency management.

## Setup Instructions

1. Clone the repository:
   ```sh
   git clone https://github.com/OlehVelychko/trade-enricher.git
   cd trade-enricher
   ```
2. Ensure **Redis** is running:
   ```sh
   brew services start redis
   ```
3. Build and run the application:
   ```sh
   mvn clean package
   java -jar target/trade-enricher-0.0.1-SNAPSHOT.jar
   ```

## API Usage

### Endpoint: `/api/v1/trades/enrich`

#### Sample `cURL` Requests:

For **CSV**:
```sh
curl -X POST -F "file=@src/main/resources/trade.csv" -H "Content-Type: multipart/form-data" http://localhost:8080/api/v1/trades/enrich
```

For **Large CSV File**:
```sh
curl -X POST -F "file=@src/main/resources/middleSizeTrade.csv" -H "Content-Type: multipart/form-data" http://localhost:8080/api/v1/trades/enrich
```

For **JSON**:
```sh
curl -X POST -F "file=@src/main/resources/trade.json" -H "Content-Type: application/json" http://localhost:8080/api/v1/trades/enrich
```

For **XML**:
```sh
curl -X POST -F "file=@src/main/resources/trade.xml" -H "Content-Type: application/xml" http://localhost:8080/api/v1/trades/enrich
```

## Test Data

- Preloaded test files are available in the project under:
  - `src/main/resources/largeSizeProduct.csv`
  - `src/main/resources/product.csv`
  - `src/main/resources/trade.csv`
- You can download **middleSizeTrade.csv** from:
  [Download Here](https://drive.google.com/file/d/1M_ln1KKICQkoV8S8RBsB7XLaHHknDZvA/view)
- Ensure test files are placed in `src/main/resources` for local testing
- Screenshots of test results can be found here:
  [Download Here](https://drive.google.com/drive/folders/13195rwptTe00va2o4otBbz1Rct-vGxVb?usp=sharing)

## What Could Be Improved

⚡ **Parser Factory**
   - Implement a factory pattern to dynamically select the appropriate parser based on `Content-Type`.

⚡ **Reactive Streams**
   - Replace `CompletableFuture` with **Reactor Flux** for better reactive streaming.
   - Migrate to **Spring WebFlux** for fully non-blocking execution.

⚡ **Distributed Processing**
   - Introduce Kafka or RabbitMQ for event-driven trade processing.
