# Flufan Backend

Flufan is a backend application that enables users to connect with experts via paid messages and video calls. Built with **Java and Spring Boot**, it demonstrates practical skills in creating scalable web applications.

## Features
- User authentication and authorization: **JWT (access/refresh), OAuth2**
- Payment integration: **Stripe**
- Media handling: user profile pictures
- Communication: **chat and video calls**
- Reverse proxy: **NGINX**

## Technologies
- **Backend:** Java, Spring Boot, JPA/Hibernate
- **Database:** MySQL / PostgreSQL
- **Unit Testing:** JUnit, Mockito
- **Containerization:** Docker
- **Build Tool:** Maven

## Project Structure
- `src/main/java/com/flufan/modules` – modular structure: `user`, `chat`, `order`, `admin`
- `controller`, `service`, `repo`, `entity`, `dto` – layered architecture
- `config/security` – security and JWT configuration
- `resources` – application configuration (`application.properties`)

## Testing
Includes unit tests for services and controllers to ensure backend quality and stability.

## Running the Project
1. Configure your database in `application.properties`
2. Run the application with Maven:  
   ```bash
   ./mvnw spring-boot:run
   ```
3. The application will be available on the default port 8080.
