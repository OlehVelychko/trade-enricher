# Trade Enricher Service

## Description
This project provides a Spring Boot service for enriching trade data using product names from a Redis-backed product database. It supports CSV, JSON, and XML formats, ensuring efficient trade enrichment, validation, and error handling.

## Features

1. **Trade Data Enrichment**
   - Reads trade files and enriches them with product names using a Redis-backed database.
   - Efficiently handles large datasets with asynchronous processing.
   
2. **File Format Support**
   - Accepts input in **CSV, JSON, and XML** formats.
   - Uses a **Parser Factory** to dynamically select the appropriate parser based on content type.

3. **Asynchronous Processing**
   - Implements `CompletableFuture` for non-blocking execution.
   - Optimized for handling large files without performance bottlenecks.

4. **Streaming Response**
   - Streams enriched trade data back to the client in real-time to prevent memory overload.

5. **Validation & Error Handling**
   - Ensures date format validity (`yyyyMMdd`), skipping invalid records instead of failing.
   - Logs missing product mappings and assigns "Missing Product Name" where necessary.
   - Improved CSV parsing with **safe date handling**.

6. **Enhanced Logging**
   - Uses **SLF4J** instead of `System.out.println` for consistent and configurable logging.

## Recent Enhancements

- **Added Comprehensive Testing:**
  - Unit tests: `TradeServiceTest`, `ProductServiceTest`.
  - Integration test: `TradeControllerTest`.
  - Uses **Mockito** for mocking dependencies and **Awaitility** for async testing.

- **Improved CSV Error Handling:**
  - Skips invalid rows instead of throwing exceptions, ensuring robust data processing.

- **Implemented Parser Factory:**
  - Dynamically selects the appropriate parser (`CsvDataParser`, `JsonDataParser`, `XmlDataParser`) based on the file's content type.

- **Logging Enhancement:**
  - Removed `System.out.println`, replaced with SLF4J's `Logger` for better maintainability and debugging.

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
- Ensure test files are placed in `src/main/resources` for local testing.

## Running Tests

To execute all tests:
```sh
mvn test
```

### Test Coverage:
- **Unit Tests:**
  - `TradeServiceTest`
  - `ProductServiceTest`
- **Integration Test:**
  - `TradeControllerTest`

## What Could Be Improved

⚡ **Extended Parser Factory**
   - Support more file formats beyond CSV, JSON, and XML.

⚡ **Reactive Streams**
   - Replace `CompletableFuture` with **Reactor Flux** for better reactive streaming.
   - Migrate to **Spring WebFlux** for fully non-blocking execution.

⚡ **Distributed Processing**
   - Introduce Kafka or RabbitMQ for event-driven trade processing.

## Contributing

Contributions are welcome! Please fork the repository and submit a pull request for any enhancements or bug fixes.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Acknowledgements

Special thanks to the open-source community for providing tools and libraries that make this project possible.
