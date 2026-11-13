# Toll Plaza Finder

Toll Plaza Finder is a Spring Boot application that finds toll plazas between two Indian PIN codes.

It uses the Mappls Routing API to get the route and checks which toll plazas are located along that route. The toll plaza details are stored in a PostgreSQL database.

## What does this project do?

- Takes source and destination PIN codes as input.
- Finds the driving route between the two locations.
- Finds toll plazas along the route.
- Returns toll plaza names, coordinates, and distance from the source.
- Uses caching to avoid making the same API request repeatedly.
- Validates PIN codes and handles errors.
- Filters duplicate toll plaza results.

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

Make sure you have the following installed:

- Java 21
- Maven
- PostgreSQL
- A Mappls API key

## Environment Variables

Before running the application, configure these environment variables:

| Variable         | Description                      |
| ---------------- | -------------------------------- |
| `MAPPLS_API_KEY` | Your Mappls API key              |
| `DB_URL`         | PostgreSQL database URL          |
| `DB_USERNAME`    | Database username                |
| `DB_PASSWORD`    | Database password                |
| `PORT`           | Application port (default: 8080) |

For local development, the database URL can be:

`jdbc:postgresql://localhost:5432/toll_finder`

Do not upload your actual API key or database password to GitHub.

## How to Run

First, clone the repository:

```bash
git clone https://github.com/YOUR_USERNAME/toll-plaza-finder.git
cd toll-plaza-finder
```

Build the project:

```bash
mvn clean install
```

Start the application:

```bash
mvn spring-boot:run
```

The application will run at `http://localhost:8080`.

## API Details

**Endpoint:** `POST /api/v1/toll-plazas`

**Request Body:**

```json
{
  "sourcePincode": "560064",
  "destinationPincode": "411045"
}
```

The API returns a list of toll plazas found along the route, including their names, locations, and distances.

## Swagger Documentation

After starting the application, open:

`http://localhost:8080/swagger-ui/index.html`

You can use Swagger UI to test the API directly.

## Caching

I have used Caffeine caching to store the results for previously requested PIN-code combinations. Cached results expire after 30 minutes.

## Running Tests

To run the test cases, use:

```bash
mvn clean test
```

The project includes tests for request validation, controller behaviour, service logic, and application startup.

## A Note About Route Matching

The application uses Mappls to calculate the driving route. Toll plazas are matched against the route using their coordinates.

PIN-code locations are representative locations returned by Mappls, rather than exact postal addresses. Duplicate toll plaza results are also filtered using normalized names and location proximity.

## License

This project was created as a technical assignment.
