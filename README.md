# Microservices Project (Spring Boot 3, Java 17, Maven)

This ZIP contains a small microservices example with:
- **auth-service** — JWT authentication + MySQL users table
- **product-service** — Product CRUD + MySQL products table (JWT-protected)
- **eureka-server** — Spring Cloud Eureka service registry
- **api-gateway** — Spring Cloud Gateway (routes + JWT prefilter skeleton)

Each service is an independent Maven project. This is a minimal starter — expand code as needed.

## Quick run (local)
1. Create two MySQL databases:
   - `authdb`
   - `productdb`
2. Edit each service's `application.yml` to set your MySQL username/password (default: root / password).
3. Start services in this order:
   - eureka-server: port 8761
   - auth-service:  Dynamic Port
   - product-service:  Dynamic Port
   - Config-server: port 8888
   - api-gateway: port 8080

Example commands (from each service folder):
```bash
cd auth-service
mvn -DskipTests spring-boot:run
```

## Endpoints (default)
- Gateway (port 8080)
  - `/auth/**` → Authentication Service
  - `/products/**` → Product Service

- Auth Service (port 8081)
  - `POST /auth/register` — register user
  - `POST /auth/login` — login and receive JWT

- Config Server
  - this contains all the YML files of the project which have all the DB connections in it.
     
- Product Service (port 8082)
  - `GET /products` — list products (requires Authorization: Bearer <token>)
  - `POST /products` — create product (requires token)
  - `GET /products/{id}`, `PUT /products/{id}`, `DELETE /products/{id}`
## SQL scripts
See `sql/` folder for `authdb.sql` and `productdb.sql`.

## Notes
- This is a functional skeleton with basic JWT utilities and Controllers. including all the user and product details apis and it is production ready.
- Expand security (password hashing, refresh tokens), add DTOs, validations, and exception handling for production use.
