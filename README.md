# Toll Plaza Finder

Toll Plaza Finder is a Spring Boot project that helps find toll plazas between two Indian PIN codes.

The application uses the Mappls Routing API to get the driving route and identifies toll plazas located along the way. Toll plaza information is stored in a PostgreSQL database.

## Live Demo

- **Live Application:** https://toll-plaza-finder.onrender.com
- **Swagger UI:** https://toll-plaza-finder.onrender.com/swagger-ui/index.html
- **API Documentation:** https://toll-plaza-finder.onrender.com/v3/api-docs
- **GitHub Repository:** https://github.com/Vijay-krishnaa/toll-plaza-finder

## What Does This Project Do?

- Accepts source and destination PIN codes.
- Finds the driving route between the two locations.
- Checks which toll plazas are available along the route.
- Returns toll plaza names, coordinates, and distance from the starting point.
- Uses caching to reduce repeated API requests.
- Checks PIN code formats and handles errors.
- Removes duplicate toll plaza results.

## Technologies Used

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- PostgreSQL
- Mappls API
- Caffeine Cache
- Maven
- JUnit 5 and Mockito
- Swagger UI

## Project Structure

```text
src/
 ├── main/
 │   ├── java/com/tollfinder/
 │   │   ├── config/
 │   │   ├── controller/
 │   │   ├── dto/
 │   │   ├── entity/
 │   │   ├── exception/
 │   │   ├── repository/
 │   │   └── service/
 │   └── resources/
 │       ├── application.properties
 │       └── toll_plaza_india.csv
 └── test/
     └── java/com/tollfinder/
```

## Requirements

To run this project, you need:

- Java 21
- Maven
- PostgreSQL
- A Mappls API key

## Environment Variables

Set up the following environment variables before running the application:

| Variable         | Description                      |
| ---------------- | -------------------------------- |
| `MAPPLS_API_KEY` | Your Mappls API key              |
| `DB_URL`         | PostgreSQL database URL          |
| `DB_USERNAME`    | Database username                |
| `DB_PASSWORD`    | Database password                |
| `PORT`           | Application port (default: 8080) |

For local development, use this database URL:

```text
jdbc:postgresql://localhost:5432/toll_finder
```

Make sure you do not upload your actual API key or database password to GitHub.

## How to Run

### 1. Clone the repository

```bash
git clone https://github.com/Vijay-krishnaa/toll-plaza-finder.git
cd toll-plaza-finder
```

### 2. Build the project

```bash
mvn clean install
```

### 3. Start the application

```bash
mvn spring-boot:run
```

## Local Application Links

Once the application starts, you can access it using these links:

- **Local Application:** http://localhost:8080
- **Local Swagger UI:** http://localhost:8080/swagger-ui/index.html
- **Local API Documentation:** http://localhost:8080/v3/api-docs

Note: These links work only on your computer while the application is running.

## API Details

**Endpoint:** `POST /api/v1/toll-plazas`

**Request Body:**

```json
{
  "sourcePincode": "560064",
  "destinationPincode": "411045"
}
```

The API returns the toll plazas found along the route, along with their names, coordinates, and distance from the source.

## Swagger Documentation

I have added Swagger UI so the API can be explored and tested directly from a browser.

- **Live Swagger UI:** https://toll-plaza-finder.onrender.com/swagger-ui/index.html
- **Local Swagger UI:** http://localhost:8080/swagger-ui/index.html

## Caching

The application uses Caffeine Cache to save results for previously requested PIN-code combinations. Cached results are kept for 30 minutes, which helps avoid making the same API request repeatedly.

## Running Tests

Run the following command to execute the test cases:

```bash
mvn clean test
```

The project includes tests for request validation, controller behaviour, service logic, and application startup.

## Route Matching

Mappls is used to calculate the driving route. The application compares toll plaza coordinates with the route to find toll plazas located along the way.

The PIN-code locations returned by Mappls are representative locations, not exact postal addresses. The application also filters duplicate toll plaza results using normalized names and location proximity.

## License

This project was developed as part of a technical assignment.
