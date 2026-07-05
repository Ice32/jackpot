# Sporty Jackpot Service

This application manages jackpot pools by processing incoming bets for dynamic pool contribution via Kafka streams and evaluating real-time winning odds using configurable strategies.

---

## 🛠️ Prerequisites

Before starting, ensure you have the following installed:

* **Java 21** or higher
* **Maven 3.8+**
* **Docker & Docker Compose** (Optional, only if using a live Kafka broker cluster)

---

## 🚀 How to Run the Application
Ensure you have Docker/OrbStack installed and then run one of the following commands in your project root:
### Running the whole stack
To spin up both the infrastructure and the compiled Java backend inside Docker simultaneously, explicitly pass the application profile:
> ```bash
> docker compose --profile app up --build -d
> ```
### Running app separately
If no profile is passed, running `docker compose up` only initializes the infrastructure layer (Kafka cluster). This allows you to easily run and debug the Spring Boot backend service separately inside an IDE.
```bash
docker-compose up -d
```
Then start the Spring Boot app from your IDE or with:
```bash
mvn spring-boot:run
```

The API is available at `http://localhost:8080`.

---

## 🗄️ Viewing the H2 Database

The application uses an in-memory H2 database configured in `src/main/resources/application.yaml`. Once the Spring Boot
app is running, open:

```text
http://localhost:8080/h2-console
```

Use these login values:

| Field        | Value                   |
|--------------|-------------------------|
| Driver Class | `org.h2.Driver`         |
| JDBC URL     | `jdbc:h2:mem:jackpotdb` |
| User Name    | `sa`                    |
| Password     | Leave empty             |

Click **Connect** to view tables such as `JACKPOT`, `JACKPOT_CONTRIBUTION`, and `JACKPOT_REWARD`. Because this is an
in-memory database, its contents reset whenever the application process restarts.

---

## ✅ Seeded Jackpots

The in-memory H2 database is initialized with these jackpot records from `src/main/resources/data.sql`. Each jackpot points to its own contribution and reward configuration records, so two jackpots can use the same strategy type with different rates or reward odds without adding strategy-specific fields to the jackpot entity.

| Jackpot ID | Contribution Strategy | Reward Strategy | Initial Balance | Example Config |
| --- | --- | --- | --- | --- |
| `JACKPOT-123` | `VARIABLE` | `VARIABLE` | `5000.00` | 30% starting contribution, 5% base reward chance |
| `JACKPOT-456` | `FIXED` | `FIXED` | `250.00` | 10% fixed contribution, 5% fixed reward chance |
| `JACKPOT-789` | `VARIABLE` | `FIXED` | `25000.00` | 25% starting contribution, 8% fixed reward chance |

---

## 🧱 Architectural Decisions

This implementation uses a state-based Spring Boot service backed by JPA/Hibernate and an in-memory H2 database. The
current jackpot balance is stored directly on the `jackpot` table, while contribution and reward records are persisted
as audit-style records containing the required bet, user, jackpot, amount, and creation-time fields.

Kafka is used for the bet-submission path to decouple request acceptance from jackpot contribution processing. The API
publishes accepted bets to the required `jackpot-bets` topic and returns `202 Accepted`; the consumer then processes
those events asynchronously and creates the jackpot contribution records.

Contribution and reward behavior is implemented with strategy interfaces and per-jackpot configuration entities. This
keeps the initially required fixed and variable options open for extension without putting strategy-specific fields
directly on the `Jackpot` entity.

Concurrency around jackpot balance updates is handled with a pessimistic write lock when loading a jackpot for
contribution or evaluation. This serializes concurrent updates for the same jackpot pool. The `@Version` column remains
as an additional persistence-level guard. Duplicate Kafka delivery is handled defensively through unique `betId`
constraints plus duplicate checks, so the same bet event does not create multiple contribution records.

Foreign keys and indexes are used where they protect core invariants or support common lookups: contribution/reward
records reference the configured jackpot ID, bet IDs are unique, and jackpot IDs are indexed for contribution and reward
queries.

An event-sourcing architecture may be a strong fit for this domain. Jackpot balance changes, contributions, reward
evaluations, wins, resets, and rejected duplicate events are naturally append-only facts. With more time, the service
could persist these as immutable domain events and derive the current jackpot state from projections. For this 90-minute
task, I chose a state-based approach because it is faster to implement and easier to inspect through H2 while still
preserving the key contribution and reward records.

Integration tests use an embedded Kafka broker and H2 database to exercise the real publish/consume flow, persistence
behavior, and evaluation logic without requiring an external Kafka cluster.

---

## 🔌 API Usage

### 1. Publish a bet to Kafka

```bash
curl -X POST http://localhost:8080/api/v1/bets/submit \
  -H "Content-Type: application/json" \
  -d '{
    "betId": "BET-1001",
    "userId": "USER-42",
    "jackpotId": "JACKPOT-456",
    "betAmount": 25.00
  }'
```

This endpoint returns `202 Accepted` after accepting the request for asynchronous publishing to the `jackpot-bets` Kafka topic. The Kafka consumer then processes the event and creates a jackpot contribution record. Because processing is asynchronous, wait briefly before evaluating the same bet.

The request body fields are:

| Field | Required | Description |
| --- | --- | --- |
| `betId` | Yes | Unique bet identifier. Reusing the same value is treated as a duplicate. |
| `userId` | Yes | User placing the bet. |
| `jackpotId` | Yes | Matching jackpot pool ID. Use one of the seeded IDs above. |
| `betAmount` | Yes | Stake amount. Must be at least `0.01`. |

### 2. Evaluate a bet for jackpot reward

```bash
curl -X POST http://localhost:8080/api/v1/bets/evaluate \
  -H "Content-Type: application/json" \
  -d '{
    "betId": "BET-1001"
  }'
```

Example response:

```json
{
  "betId": "BET-1001",
  "won": false,
  "payoutAmount": 0,
  "remainingPoolBalance": 252.5000
}
```

If the bet has not been consumed from Kafka yet, or if it has already been evaluated, the API returns `409 Conflict` with an error message.

### 3. Inspect rewards

```bash
curl http://localhost:8080/api/v1/bets/rewards
```

This returns the persisted jackpot reward records, ordered newest first. It is mainly used by the demo UI.

### 4. Inspect jackpot state

```bash
curl http://localhost:8080/api/v1/jackpots
```

This returns the current jackpot records and balances.

## 🧪 How to Run the Tests

The codebase includes integration tests covering Kafka publishing/consumption, contribution calculation, reward evaluation, and duplicate evaluation protection.

To execute the entire test lifecycle suite, open your terminal and run:
```bash
mvn clean test
```

---

## 🎮 How to Test Through the UI Demo

I have built an ultra-lightweight, zero-dependency interactive testing dashboard inside the application using HTMX.

### Steps to Run the Interactive Test:
1. Ensure the infrastructure and the application are up and running via `docker compose --profile app up --build -d`.
2. Open your preferred web browser and navigate to: `http://localhost:8080`
3. Click anywhere on the dark background of the webpage to focus your browser window.
4. Press the **SPACEBAR** key on your keyboard 🎲
