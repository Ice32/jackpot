# Sporty Jackpot Service

This application manages jackpot pools by processing incoming bets for dynamic pool contribution via Kafka streams and evaluating real-time winning odds using configurable strategies.

---

## 🛠️ Prerequisites

Before starting, ensure you have the following installed:
* **Java 25** or higher
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
curl http://localhost:8080/api/v1/bets/pools
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
