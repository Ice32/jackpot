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

### 1. Configure the Infrastructure (Kafka Setup)
Ensure you have Docker/OrbStack installed, spin up Kafka brokers by running this command in your project root:
```bash
docker-compose up -d
```

## 🧪 How to Run the Tests

The codebase includes exhaustive testing paradigms spanning unit strategy tests, mock pipeline validation, and repository state transactions under isolation constraints.

To execute the entire test lifecycle suite, open your terminal and run:
```bash
mvn clean test
```

---

## 🎮 How to Test Through the UI Demo

I have built an ultra-lightweight, zero-dependency interactive testing dashboard inside the application using HTMX.

### Steps to Run the Interactive Test:
1. Ensure the application is up and running via `mvn spring-boot:run`.
2. Open your preferred web browser and navigate to: `http://localhost:8080`
3. Click anywhere on the dark background of the webpage to focus your browser window.
4. Press the **SPACEBAR** key on your keyboard 🎲
