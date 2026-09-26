# vibolSEN Inventory & POS Management System

[![Java](https://img.shields.io/badge/Java-26-orange.svg?style=flat&logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-Enterprise%20REST%20API-brightgreen.svg?style=flat&logo=springboot)](https://spring.io/projects/spring-boot)
[![MySQL / TiDB](https://img.shields.io/badge/Database-MySQL%20%7C%20TiDB%20Cloud-blue.svg?style=flat&logo=mysql)](https://www.mysql.com/)
[![Docker](https://img.shields.io/badge/Container-Docker%20Multi--Stage-2496ED.svg?style=flat&logo=docker)](https://www.docker.com/)
[![Render](https://img.shields.io/badge/Deployment-Render%20Cloud-46E3B7.svg?style=flat&logo=render)](https://render.com/)
[![Postman](https://img.shields.io/badge/API%20Testing-Postman%20Collection%20v2.1-FF6C37.svg?style=flat&logo=postman)](https://www.postman.com/)

An enterprise-grade **Inventory Management & Point of Sale (POS) RESTful Backend System** built with **Java** and **Spring Boot**. The system delivers end-to-end management of warehouse inventory, multi-item POS sales checkout with KHQR Bakong integration support, supplier procurement shipments, customer loyalty point tracking, and immutable stock audit logs.

> **Academic Course**: Term 5 Java Course Project — STEP Academy  
> **Author**: Vibol Sen  
> **Production Live URL**: [https://vibolsen-inventory-mgt-system.onrender.com](https://vibolsen-inventory-mgt-system.onrender.com)

---

## Table of Contents
- [System Architecture & Features](#system-architecture--features)
- [Entity-Relationship Diagram (ERD)](#entity-relationship-diagram-erd)
- [Technology Stack](#technology-stack)
- [REST API Directory](#rest-api-directory)
- [Getting Started (Local Setup)](#getting-started-local-setup)
- [API Testing with Postman](#api-testing-with-postman)
- [Docker & Cloud Deployment](#docker--cloud-deployment)
- [Project Directory Structure](#project-directory-structure)
- [License](#license)

---

## System Architecture & Features

### 1. Catalog & Product Inventory
- **Category Hierarchy**: Organize inventory into logical categories with searchable hierarchies.
- **Product Lifecycle**: Full SKU and Barcode tracking, safety stock levels (`minStockLevel`), unit costing, dynamic retail pricing, and status management (`ACTIVE`, `INACTIVE`, `DISCONTINUED`).
- **Pagination & Search**: High-performance paginated listings and keyword filtering.

### 2. Point of Sale (POS) & Cart Sessions
- **Active Cart Sessions**: Dedicated cash register sessions tied to users/cashiers and customers.
- **Flexible Items**: Real-time line item price lookup, item discount calculation, and quantity adjustments prior to checkout.

### 3. Sales & Multi-Payment Checkout
- **Instant Checkout**: Atomic conversion of cart/order items into finalized sales records with automated inventory deductions.
- **Flexible Discounts**: Fixed dollar discounts or percentage deductions.
- **Multi-Method Payments**: Support for Cash, Cards, and Cambodian **KHQR (Bakong)** reference tracking.
- **Transaction Rollback & Deletion**: Safe sale cancellation and inventory reversal operations.

### 4. Supplier Procurement & Shipments
- **Purchase Orders / Shipments**: Tracking supplier orders with expected arrival dates and tracking numbers.
- **Warehouse Receiving Flow**: Granular item-by-item receipt logging, recording both received and damaged quantities, automatically reconciling warehouse stock quantities.

### 5. Immutable Stock Audit Trail
- **Traceability**: Every quantity change (Purchase, Sale, Return, Damage, Cycle-Count Adjustment) writes an immutable record to `stock_transactions`.
- **Audit Trails**: Query full movement history by product ID to diagnose inventory discrepancies.

### 6. Customer Loyalty & Accounts
- Maintain customer purchase records, contact info, and dynamic loyalty point balances with increment/decrement endpoints.

### 7. Observability & Health
- Integrated **Spring Boot Actuator** (`/actuator/health`, `/actuator/info`) exposing runtime metrics and MySQL/TiDB database connection status.

---

## Entity-Relationship Diagram (ERD)

The database schema is designed for 3NF normalization, referential integrity with foreign key constraints, and transactional consistency.

![ERD Diagram](ERD%20Diagram.png)

### Key Relationships
- `categories` (1) &rarr; `products` (N)
- `suppliers` (1) &rarr; `products` (N)
- `suppliers` (1) &rarr; `shipments` (N) &rarr; `shipment_items` (N)
- `customers` (1) &rarr; `sales` (N) &rarr; `sale_items` (N) & `sale_payments` (N)
- `products` (1) &rarr; `stock_transactions` (N)
- `users` (1) &rarr; `sales` (N), `shipments` (N), `carts` (N)

---

## Technology Stack

| Layer | Technology |
|---|---|
| **Language** | Java 26 (Temurin JDK) |
| **Framework** | Spring Boot (Web MVC, Data JPA, Actuator, Validation) |
| **Database** | MySQL 8.0+ / PingCAP TiDB Cloud (Serverless MySQL-compatible) |
| **ORM / Persistence** | Hibernate 6+, Spring Data JPA |
| **Boilerplate Reduction** | Project Lombok |
| **Build & Dependency Tool** | Apache Maven (with `mvnw` wrapper) |
| **Containerization** | Docker (Multi-stage build) |
| **Cloud Hosting** | Render (Docker Web Service, Singapore Region) |
| **API Client & Specs** | Postman (Collection v2.1 & Environments) |

---

## REST API Directory

Base URL (Local): `http://localhost:8080`  
Base URL (Cloud): `https://vibolsen-inventory-mgt-system.onrender.com`

| Module | Method | Endpoint | Description |
|---|---|---|---|
| **System** | `GET` | `/actuator/health` | Service & database health status |
| **Categories** | `GET` | `/api/v1/categories` | List all categories |
| | `GET` | `/api/v1/categories/{id}` | Get category by ID |
| | `POST` | `/api/v1/categories` | Create new category |
| | `PUT` | `/api/v1/categories/{id}` | Update existing category |
| | `GET` | `/api/v1/categories/search?name={q}` | Search categories by name |
| **Products** | `GET` | `/api/v1/products` | List all products (paginated) |
| | `GET` | `/api/v1/products/{id}` | Get product details by ID |
| | `POST` | `/api/v1/products` | Create product (with SKU, barcode, supplier) |
| | `PUT` | `/api/v1/products/{id}` | Update product information |
| | `GET` | `/api/v1/products/search?keyword={q}` | Search products by name/SKU |
| **Customers** | `GET` | `/api/v1/customers` | List all customers |
| | `POST` | `/api/v1/customers` | Register customer |
| | `PATCH` | `/api/v1/customers/{id}/loyalty-points` | Adjust customer loyalty points |
| **Suppliers** | `GET` | `/api/v1/suppliers` | List all suppliers |
| | `POST` | `/api/v1/suppliers` | Create supplier profile |
| **Users** | `GET` | `/api/v1/users` | List all system users |
| | `POST` | `/api/v1/users` | Create user (CASHIER, MANAGER, ADMIN) |
| **POS Carts** | `POST` | `/api/v1/carts` | Start POS cart session |
| | `GET` | `/api/v1/carts?status=ACTIVE` | View active cart sessions |
| | `POST` | `/api/v1/carts/{id}/items` | Add item into cart session |
| **Sales** | `POST` | `/api/v1/sales` | Finalize POS sale checkout & payments |
| | `GET` | `/api/v1/sales?page=0&size=10` | Paginated sale order history |
| | `GET` | `/api/v1/sales/{id}` | Retrieve invoice/receipt details |
| | `DELETE` | `/api/v1/sales/{id}` | Void / delete sale record |
| **Shipments** | `POST` | `/api/v1/shipments` | Place purchase shipment from supplier |
| | `GET` | `/api/v1/shipments` | List all incoming/received shipments |
| | `POST` | `/api/v1/shipments/{id}/receive` | Receive goods into warehouse stock |
| **Stock Audit** | `POST` | `/api/v1/stock-transactions/adjust` | Manual inventory count adjustment |
| | `GET` | `/api/v1/stock-transactions` | Paginated global stock audit log |
| | `GET` | `/api/v1/stock-transactions/product/{id}` | Product-specific audit trail |

---

## Getting Started (Local Setup)

### 1. Prerequisites
- **Java JDK 21+** (Java 26 recommended)
- **MySQL Server 8.0+** running locally on port `3306`
- **Git**
- **Postman** (for API testing)

### 2. Clone the Repository
```bash
git clone https://github.com/VibolSen/vibolSEN-inventory-mgt-system.git
cd vibolSEN-inventory-mgt-system
```

### 3. Configure Database
Create a local MySQL database:
```sql
CREATE DATABASE inventory_mgt_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Review or modify credentials in `src/main/resources/application.properties`:
```properties
spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:mysql://localhost:3306/inventory_mgt_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME:root}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:YourPassword}
```

### 4. Build and Run
Using the included Maven wrapper:

**On Windows (PowerShell / CMD):**
```powershell
.\mvnw.cmd spring-boot:run
```

**On Linux / macOS:**
```bash
chmod +x mvnw
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`. Verify with:
```bash
curl http://localhost:8080/actuator/health
```

---

## API Testing with Postman

Pre-configured collection and environment files are available in the [`postman/`](postman/) folder:

1. **Open Postman** and click **Import**.
2. Import the Collection:
   - `postman/vibolSEN_Inventory_System.postman_collection.json`
3. Import the Environment(s):
   - **Local Environment**: `postman/Local.postman_environment.json` (`baseUrl` = `http://localhost:8080`)
   - **Cloud Environment**: `postman/Render_Cloud.postman_environment.json` (`baseUrl` = `https://vibolsen-inventory-mgt-system.onrender.com`)
4. In the top-right corner of Postman, select the **Local (localhost:8080)** environment.
5. Execute requests in order:
   - `1. Health & Actuator` &rarr; `System Health & DB Status`
   - `2. Categories` &rarr; `Create Category`
   - `5. Suppliers` &rarr; `Create Supplier`
   - `3. Products` &rarr; `Create Product`
   - `4. Customers` &rarr; `Create Customer`
   - `6. Users` &rarr; `Create User`
   - `8. Sales` &rarr; `Create Sale (Checkout)`

---

## Docker & Cloud Deployment

### Multi-Stage Docker Build
The project includes an optimized multi-stage `Dockerfile`:
- **Stage 1 (Builder)**: Compiles source and packages the Spring Boot JAR with Temurin JDK 26.
- **Stage 2 (Runtime)**: Minimal runtime container with memory tuning (`-Xmx350m -XX:+UseSerialGC`) optimized for cloud free tiers.

#### Build & Run Container Locally:
```bash
# Build Docker image
docker build -t vibolsen-inventory-api .

# Run container
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL="jdbc:mysql://host.docker.internal:3306/inventory_mgt_db" \
  -e SPRING_DATASOURCE_USERNAME="root" \
  -e SPRING_DATASOURCE_PASSWORD="YourPassword" \
  vibolsen-inventory-api
```

### Render Deployment Configuration
The repository includes `render.yaml` for zero-configuration Infrastructure as Code (IaC) deployment:
- Region: `singapore`
- Build type: `docker`
- Auto-deploy: `true` on pushes to `master`
- Health check path: `/actuator/health`

---

## Project Directory Structure

```text
vibolSEN-inventory-mgt-system/
├── .github/                       # CI/CD workflows
├── postman/                       # Postman test artifacts
│   ├── vibolSEN_Inventory_System.postman_collection.json
│   ├── Local.postman_environment.json
│   └── Render_Cloud.postman_environment.json
├── src/
│   ├── main/
│   │   ├── java/com/vibolSEN/inventory_mgt_system/
│   │   │   ├── controller/        # REST API Controllers (Sale, Product, Cart, etc.)
│   │   │   ├── dto/               # Data Transfer Objects & API Payloads
│   │   │   ├── exception/         # Custom Exceptions & Global Exception Handler
│   │   │   ├── model/             # JPA Entities & Enums
│   │   │   ├── repository/        # Spring Data JPA Repositories
│   │   │   ├── service/           # Business Logic Interfaces & Implementations
│   │   │   └── InventoryMgtSystemApplication.java
│   │   └── resources/
│   │       └── application.properties # Spring configuration & dynamic env bindings
│   └── test/                      # Unit & integration tests
├── Dockerfile                     # Multi-stage container definition
├── render.yaml                    # Render Cloud deployment blueprint
├── ERD Diagram.png                # Database Entity-Relationship Diagram
├── pom.xml                        # Maven configuration & dependencies
└── README.md                      # Project documentation
```

---

## License

This project was developed for academic purposes as part of the **Term 5 Java Course Project** at **STEP Academy**.