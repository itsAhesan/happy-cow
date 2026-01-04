# Happy Cow Dairy Management System

A comprehensive Spring MVC-based web application for managing dairy operations, including product inventory, agent management, collection tracking, and payment processing.

## Overview

Happy Cow is an enterprise-style dairy management system designed to streamline operations for dairy businesses. The application facilitates product management, agent registration and tracking, milk collection recording, payment processing, and comprehensive reporting. It provides separate dashboards for administrators and agents with role-based access control.


## Tech Stack

- **Java** - Core programming language
- **Spring MVC** - Web framework for building MVC architecture
- **Hibernate / Spring ORM** - Object-relational mapping and persistence
- **JSP & JSTL** - Server-side view rendering
- **MySQL** - Relational database management system
- **Apache Tomcat** - Application server
- **Maven** - Build automation and dependency management
- **HTML, CSS, Bootstrap** - Frontend styling and responsive design
- **Spring WebSocket** - Real-time notification system
- **Swagger** - API documentation
- **Jackson** - JSON processing
- **Apache POI** - Excel file generation
- **PDFBox** - PDF document generation
- **ZXing** - QR code generation
- **Lombok** - Boilerplate code reduction

## Features

### Admin Module
- Secure authentication with account locking mechanism
- Password reset via OTP email verification
- Admin profile management with image upload
- Dashboard with key performance indicators
- Comprehensive audit logging for all admin activities

### Agent Management
- Agent registration and profile management
- Agent search and pagination
- Bank details management for payment processing
- QR code generation for agent profiles
- Profile picture upload and management
- Agent dashboard with order tracking

### Product Management
- Product CRUD operations (Create, Read, Update, Delete)
- Product categorization by type and milk variety
- Product import/export functionality (Excel)
- Active/inactive product status management
- Product pricing management

### Product Collection
- Record milk collection from agents
- Track collection by date, quantity, and type
- Calculate total amounts automatically
- Collection history and reporting
- Agent-wise collection aggregation

### Payment Management
- Bi-monthly payment window calculation
- Payment history tracking
- Agent payment status monitoring
- Payment window management
- Real-time payment notifications via WebSocket

### Additional Features
- Email service integration for OTP and notifications
- Real-time notifications using Spring WebSocket
- Excel export for reports and data
- PDF invoice generation
- QR code generation for agent profiles
- Comprehensive audit trail for all entities
- RESTful APIs with Swagger documentation
- Session management and security

## Project Architecture

The application follows the **Model-View-Controller (MVC)** architectural pattern with clear separation of concerns:

### Controller Layer
- Handles HTTP requests and responses
- Manages user sessions and authentication
- Delegates business logic to service layer
- Returns appropriate views or JSON responses
- Located in `com.xworkz.happycow.controller` and `com.xworkz.happycow.restcontroller`

### Service Layer
- Contains business logic and transaction management
- Validates data and enforces business rules
- Coordinates between controllers and repositories
- Handles email notifications and file operations
- Located in `com.xworkz.happycow.service`

### Repository/DAO Layer
- Provides data access abstraction
- Implements CRUD operations using Hibernate
- Contains custom queries and aggregations
- Manages entity persistence and retrieval
- Located in `com.xworkz.happycow.repo`

### Entity Layer
- JPA entities representing database tables
- Defines relationships (One-to-One, Many-to-One)
- Contains validation annotations
- Located in `com.xworkz.happycow.entity`

### DTO Layer
- Data Transfer Objects for data exchange
- Separates entity structure from API contracts
- Located in `com.xworkz.happycow.dto`

### View Layer
- JSP pages for server-side rendering
- Bootstrap-based responsive UI
- JavaScript for dynamic interactions
- Located in `src/main/webapp`

## Project Structure

```
happy-cow/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/xworkz/happycow/
│   │   │       ├── config/          # Spring configuration classes
│   │   │       ├── controller/      # MVC controllers
│   │   │       ├── restcontroller/  # REST API controllers
│   │   │       ├── service/         # Business logic layer
│   │   │       ├── repo/            # Data access layer
│   │   │       ├── entity/          # JPA entities
│   │   │       ├── dto/             # Data transfer objects
│   │   │       └── util/            # Utility classes
│   │   ├── resources/
│   │   │   ├── application.properties  # Configuration properties
│   │   │   └── logback.xml            # Logging configuration
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       │   └── web.xml          # Web application descriptor
│   │       ├── *.jsp                # JSP view pages
│   │       ├── css/                 # Stylesheets
│   │       ├── js/                  # JavaScript files
│   │       └── images/              # Static images
│   └── test/                        # Test classes
├── pom.xml                          # Maven configuration
└── README.md                        # Project documentation
```

## Database Design

The application uses **MySQL** database with the following key entities:

### Core Tables
- **admin_info** - Administrator accounts and profiles
- **agent_info** - Agent registration and profile information
- **products** - Product catalog with pricing
- **product_collection** - Milk collection records
- **agent_bank_details** - Agent banking information for payments
- **agent_payment_window** - Payment window tracking and calculations

### Audit Tables
- **admin_audit** - Admin activity audit trail
- **agent_audit** - Agent modification history
- **agent_bank_audit** - Bank details change tracking
- **product_collection_audit** - Collection record audit

### Relationships
- Product Collection has Many-to-One relationship with Agent and Admin
- Agent Bank Details has One-to-One relationship with Agent
- All entities maintain audit relationships for change tracking

## Setup and Installation

### Prerequisites
- Java 8 or higher
- Maven 3.6+
- MySQL 8.0+
- Apache Tomcat 9.0+

### Installation Steps

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd happy-cow
   ```

2. **Create MySQL database**
   ```sql
   CREATE DATABASE happy_cow;
   ```

3. **Configure database connection**
    - Update `src/main/resources/application.properties` with your database credentials:
      ```properties
      jdbc.url=jdbc:mysql://localhost:3306/happy_cow
      jdbc.username=your_username
      jdbc.password=your_password
      ```

4. **Configure email service (optional)**
    - Update email properties in `application.properties`:
      ```properties
      mail.host=smtp.gmail.com
      mail.port=587
      mail.username=your_email@gmail.com
      mail.password=your_app_password
      ```

5. **Build the project**
   ```bash
   mvn clean install
   ```

6. **Deploy to Tomcat**
    - Copy the generated `target/happy-cow.war` to Tomcat's `webapps` directory
    - Or configure Tomcat in your IDE and run the application

7. **Access the application**
    - Open browser and navigate to `http://localhost:8080/happy-cow`
    - The database schema will be auto-generated on first run (if `hibernate.hbm2ddl.auto=update`)

## Configuration

### Application Properties

The `application.properties` file contains:

- **Database Configuration**: JDBC connection settings, Hibernate dialect, and DDL auto-update
- **File Upload**: Multipart file size limits (default: 2MB)
- **Email Configuration**: SMTP settings for email notifications
- **Logging**: Hibernate SQL logging levels

### Hibernate Configuration

- **Dialect**: MySQL8Dialect for MySQL 8.0+ compatibility
- **DDL Auto**: Set to `update` for automatic schema generation
- **Show SQL**: Configured for development/debugging
- **Entity Scanning**: Automatically scans `com.xworkz.happycow.entity` package

### WebSocket Configuration

Real-time notifications are enabled via Spring WebSocket for payment alerts and system notifications.

### Swagger Configuration

API documentation is available at `/swagger-ui.html` after deployment.

## Future Enhancements

- **Spring Security Integration**: Implement comprehensive security framework for enhanced authentication and authorization
- **REST API Expansion**: Develop comprehensive RESTful APIs for mobile application integration
- **Performance Optimization**: Implement caching strategies, query optimization, and connection pooling
- **Microservices Migration**: Consider breaking down into microservices for better scalability
- **Docker Containerization**: Package application and database in Docker containers for easy deployment
- **CI/CD Pipeline**: Set up automated testing and deployment pipelines
- **Advanced Reporting**: Implement comprehensive analytics and reporting dashboards
- **Mobile Application**: Develop native or hybrid mobile apps for agents
- **Payment Gateway Integration**: Integrate with payment gateways for online transactions
- **Multi-tenancy Support**: Add support for multiple dairy organizations

## Author

**Ahesan Chowdhury**  
Java Backend Developer

---

*This project demonstrates enterprise-level Spring MVC application development with comprehensive features for dairy management operations.*

