# RestoFlow - online restaurant system

Application for managing a restaurant: menu, orders (delivery / pickup), table reservations, special events
and an admin panel. Backend (REST API) plus a simple frontend in a single application.

---

## Tech stack

| Layer        | Stack                                                 |
|--------------|-------------------------------------------------------|
| **Backend**  | Java 17+, Spring Boot 3.3, Spring Data JPA, Hibernate |
| **Database** | H2 (in-memory)                                       |
| **Frontend** | HTML5, CSS3, JavaScript (ES6+), Bootstrap 5           |
| **Build**    | Maven                                                 |

---

## Running the app

**Requirements:** Java 17 or 21, Maven (or the embedded `mvnw` wrapper).

**Windows (PowerShell / CMD):**

```bash
.\mvnw.cmd spring-boot:run
```

| Address                            | Description                                                              |
|------------------------------------|--------------------------------------------------------------------------|
| `http://localhost:8080`            | Application (home page, menu, reservations, cart, admin)                |
| `http://localhost:8080/h2-console` | H2 console (JDBC URL: `jdbc:h2:mem:restoflow`, user: `sa`, empty password) |

H2 runs in-memory - **data is lost after restart**. On startup `data.sql` is executed: tables, tables layout,
dishes, accounts.

---

## Test accounts

| Role      | Email                    | Password    |
|-----------|--------------------------|-------------|
| Admin     | `admin@restoflow.pl`     | `admin123`  |
| Employee  | `pracownik@restoflow.pl` | `worker123` |

---

## API - main endpoints

**Base URL:** `http://localhost:8080/api`

### Authorization

- `POST /auth/register` - registration (email, password, first name, last name, phone, address)
- `POST /auth/login` - login (email, password); returns `token`, `email`, `firstName`, `lastName`, `role`
- `GET /auth/me?email={email}` - user profile

### Menu

- `GET /menu` - all dishes; `?q=...` - search
- `GET /menu/available` - only available dishes
- `GET /menu/category/{category}` - e.g. `ZUPA`, `DANIE_GLOWNE`, `DESER`, `NAPOJ_BEZ_ALKOHOLOWE`, `NAPOJE_ALKOHOLOWE`
- `GET /menu/{id}` - single dish

### Orders

- `POST /orders` - new
  order (`items`: `dishId`, `quantity`, `notes`; `type`: `DOSTAWA`|`ODBIOR_OSOBISTY`; `deliveryAddress`)
- `GET /orders` - list of orders
- `GET /orders/{orderNumber}` - details
- `PUT /orders/{id}/status?status=...` -
  status: `OCZEKUJACE`, `POTWIERDZONE`, `W_PRZYGOTOWANIU`, `GOTOWE`, `DOSTARCZONE`, `ANULOWANE`

### Reservations

- `POST /reservations` - new reservation (`date`, `startTime`, `endTime`, `peopleCount`, `notes`); time 12:00-23:00,
  min. 1 h, max. 4 h
- `GET /reservations/available?date=...&startTime=...&endTime=...&peopleCount=...` - availability
- `GET /reservations/{reservationNumber}` - details
- `GET /reservations/day/{date}` - reservations for a day
- `DELETE /reservations/{id}` - cancel

### Events

- `POST /events` - new (`type`, `date`, `time`, `theme`, `decorationPackage`, `cake`, `entertainment`, `notes`);
  types: `URODZINY`, `WESELA`, `CHRZEST`, `KOMUNIA`, `FIRMOWA`
- `GET /events/types` - list of types
- `GET /events/{id}` - details

### Tables

- `GET /tables` - list
- `POST /tables` -
  add (`tableNumber`, `capacity`, `location`: `TARAS`|`SALA_GLOWNA`|`SALON_VIP`|`BAR`, `available`, `active`)
- `PUT /tables/{id}` - edit
- `PUT /tables/{id}/availability?available=true|false` - availability

### Admin

- `GET /admin/statistics` - statistics for today: `ordersCount`, `totalSales`, `reservationsCount`
- `GET /admin/orders/today` - today's orders
- `GET /admin/reservations/today` - today's reservations

---

## Frontend (pages)

Files in `src/main/resources/static/`:

| Path                      | Description                                                                 |
|---------------------------|-----------------------------------------------------------------------------|
| `/` or `/index.html`      | Home page, popular dishes                                                  |
| `/pages/menu.html`        | Menu with filters, pagination, cart                                       |
| `/pages/cart.html`        | Cart and placing an order                                                 |
| `/pages/reservation.html` | Table reservation, availability check                                     |
| `/pages/event.html`       | Event form                                                                |
| `/pages/login.html`       | Login / registration                                                      |
| `/pages/profile.html`     | User profile                                                              |
| `/pages/admin.html`       | Admin panel: statistics, orders, reservations, tables (for role ADMIN)   |

Dish images: `static/images/*.png` (names match `image_url` in `data.sql`).

---

## Initial data (data.sql)

- **10 tables** - Main hall, Terrace, VIP lounge
- **~70 dishes** - soups, starters, main courses (including pizzas), drinks (with/without alcohol)
- **2 accounts** - admin, employee (as in the table above)

---

## Project structure

```
src/main/java/pl/example/restaurant/
├── config/          # GlobalExceptionHandler, JacksonConfig
├── controller/      # Auth, Menu, Order, Reservation, Event, Table, Admin
├── dto/             # AuthDto, OrderDto, ReservationDto
├── entity/          # Category, EventType, FoodType, OrderStatus, Role
├── model/           # JPA entities (Account, Dish, Order, OrderItem, Reservation, …)
├── repository/      # Spring Data JPA
└── service/         # business logic (incl. TableAssignmentService)
```

```
src/main/resources/
├── static/          # frontend: HTML, CSS, JS, images
├── data.sql         # initial data (H2)
└── application.properties
```

---

## Configuration (application.properties)

- **H2:** `jdbc:h2:mem:restoflow`, PostgreSQL mode, console enabled
- **JPA:** `ddl-auto=update`, `data.sql` on startup
- **Thymeleaf:** template checking disabled (static HTML/JS is used)

---

## Tests

```bash
mvnw test
```

Runs unit tests from `src/test/`.
