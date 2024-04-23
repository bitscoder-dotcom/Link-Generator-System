
# Link Generator System

## Description
The Link Generator System is a web application developed using Spring Boot that allows generating payment links for invoices and facilitating the payment process for customers.

## Dependencies
The project utilizes several dependencies managed via Maven:

- Spring Boot Starter Web
- Spring Boot Starter Undertow
- Spring Boot Starter Thymeleaf
- Spring Boot Starter Data JPA
- Spring Boot Starter Mail
- Spring Boot Starter Validation
- Spring Boot Starter Security
- H2 Database
- Lombok
- ModelMapper
- JSON Web Token (JWT) libraries
- Java JWT
- JUnit
- Spring Boot Starter Test
- Spring Security Test

## Configuration
### In-Memory Database
The application uses an H2 in-memory database configured as follows:
- URL: jdbc:h2:mem:lgs
- Username: sa
- Password: [blank]

### Email Configuration
Email properties are configured for SMTP using Gmail:
- Host: smtp.gmail.com
- Port: 465
- Username: [Your Gmail username]
- Password: [Your Gmail password]

### JWT Properties
JWT properties are configured for token generation:
- Expiration Time: 89999999 ms
- Secret Key: [Your JWT secret key]

### Thymeleaf Configuration
Thymeleaf template configuration:
- Prefix: classpath:/templates/
- Suffix: .html

## Building and Running
To build and run the project, ensure you have Maven and Java installed on your system. Then, execute the following command:
```
mvn spring-boot:run
```

The application will start, and you can access it via `http://localhost:8080`.

## Testing
The project includes unit tests and integration tests. You can run the tests using Maven:
```
mvn test
```

## API Documentation
For API documentation, refer to the Postman documentation:
[Link to API Documentation](https://documenter.getpostman.com/view/25585012/2sA3Bq4qju)

## Contributors

---

## The visuals
Email sent to customer with details of invoice that require payment

Wrong mock payment details inserted

Error page displayed due to invalid details

When details are valid, a success page is displayed with information

Email sent to Customer when mock payment is successful
