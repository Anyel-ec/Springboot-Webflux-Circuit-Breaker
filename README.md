````markdown
# ⚡ Circuit Breaker Demo with Resilience4j and Spring Boot

This project demonstrates how to use **Resilience4j Circuit Breaker** with **Spring Boot WebFlux** to improve the resilience of your applications when dealing with potentially unreliable external services.

## 🚀 Features

- Spring Boot + Resilience4j Circuit Breaker integration
- Reactive REST API with WebFlux
- Simulated unstable external service
- Circuit Breaker metrics logging and status endpoint
- Fallback mechanism when circuit is open or service fails

---

## 🧠 What is a Circuit Breaker?

A **Circuit Breaker** is a resilience pattern that prevents an application from repeatedly trying to call a failing external service. It has 3 key states:

- **CLOSED**: Calls are allowed; metrics are monitored.
- **OPEN**: All calls are blocked; fallback is triggered.
- **HALF_OPEN**: A few test calls are allowed to check if the service has recovered.

---

## 🛠️ Configuration (`application.properties`)

```properties
# Name of the application
spring.application.name=circuit-breaker

# Enable Spring Actuator health monitoring for the Circuit Breaker
resilience4j.circuitbreaker.configs.default.registerHealthIndicator=true

# Sliding window size (last 10 calls are considered for metrics)
resilience4j.circuitbreaker.configs.default.slidingWindowSize=10

# Minimum number of calls before failure rate is calculated
resilience4j.circuitbreaker.configs.default.minimumNumberOfCalls=5

# When HALF_OPEN, allow 3 trial calls to test recovery
resilience4j.circuitbreaker.configs.default.permittedNumberOfCallsInHalfOpenState=3

# Automatically move from OPEN to HALF_OPEN after wait time
resilience4j.circuitbreaker.configs.default.automaticTransitionFromOpenToHalfOpenEnabled=true

# Duration in OPEN state before testing the service (in ms) - 1 minute
resilience4j.circuitbreaker.configs.default.waitDurationInOpenState=60000

# Threshold for opening the circuit (e.g., if 50% of calls fail)
resilience4j.circuitbreaker.configs.default.failureRateThreshold=50

# Buffer size for internal event handling
resilience4j.circuitbreaker.configs.default.eventConsumerBufferSize=10

# Exceptions considered as failures
resilience4j.circuitbreaker.configs.default.recordExceptions=java.lang.Exception

# Create a named circuit breaker instance inheriting the default config
resilience4j.circuitbreaker.instances.googlePing.baseConfig=default
````

---

## 📦 Project Structure

### `SimuladorServicioExterno.java`

Simulates an unreliable service. It randomly fails \~90% of the time to trigger the Circuit Breaker.

```java
public String llamadaServicio() {
    int chance = random.nextInt(10);
    if (chance < 9) {
        throw new RuntimeException("Simulated failure");
    }
    return "Success from simulated service";
}
```

---

### `CircuitBreakerController.java`

REST controller with endpoints:

* **`/api/probar`** → Calls the simulated service, logs metrics and uses fallback if needed.
* **`/api/estado`** → Returns real-time circuit breaker state and metrics.

Fallback is triggered automatically when the circuit is open or failures occur.

---

## 🔄 Example Flow

1. Hit `/api/probar` several times.
2. After 5 failed calls (within the last 10), the circuit opens.
3. For 60 seconds, all calls are blocked and fallback is returned.
4. After 60 seconds, it transitions to HALF\_OPEN and allows 3 trial calls.
5. If those calls succeed, the circuit closes again.

---

## 📊 Monitoring

The state and metrics can be monitored via:

* `/api/estado` (custom endpoint)
* Spring Boot Actuator (`/actuator/health` if enabled)

---

## ✅ How to Run

```bash
./mvnw spring-boot:run
```

Then access:

* `http://localhost:8080/api/probar`
* `http://localhost:8080/api/estado`

---

## 🧪 Tech Stack

* Java 17+
* Spring Boot
* Spring WebFlux
* Resilience4j CircuitBreaker
* SLF4J (logging)
* Reactor Mono

---

## 📄 License

MIT © 2025 – Anyel
