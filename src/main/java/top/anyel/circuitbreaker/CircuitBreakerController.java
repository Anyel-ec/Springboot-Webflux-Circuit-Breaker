package top.anyel.circuitbreaker;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreaker.State;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CircuitBreakerController {

    private static final Logger logger = LoggerFactory.getLogger(CircuitBreakerController.class);

    @Autowired
    private SimuladorServicioExterno simulador;

    @Autowired
    private CircuitBreakerRegistry registry;

    @GetMapping("/probar")
    @CircuitBreaker(name = "simuladorService", fallbackMethod = "fallback")
    public Mono<String> probar() {
        return Mono.fromSupplier(() -> {
            var cb = registry.circuitBreaker("simuladorService");
            logger.info("➡️ Estado del Circuit Breaker ANTES de la llamada: {}", cb.getState());

            String resultado = simulador.llamadaServicio();

            logger.info("✅ Llamada exitosa al servicio externo");
            logger.info("📊 Estado del Circuit Breaker DESPUÉS de la llamada: {}", cb.getState());
            logger.info("📈 Métricas: llamadasExitosas={}, llamadasFallidas={}, tasaFallos={}%",
                    cb.getMetrics().getNumberOfSuccessfulCalls(),
                    cb.getMetrics().getNumberOfFailedCalls(),
                    cb.getMetrics().getFailureRate());

            return resultado;
        });
    }


    @GetMapping("/estado")
    public Map<String, Object> estado() {
        var cb = registry.circuitBreaker("simuladorService");

        Map<String, Object> data = new HashMap<>();
        data.put("estado", cb.getState());
        data.put("tasaFallos", cb.getMetrics().getFailureRate());
        data.put("llamadasFallidas", cb.getMetrics().getNumberOfFailedCalls());
        data.put("llamadasExitosas", cb.getMetrics().getNumberOfSuccessfulCalls());
        data.put("noPermitidas", cb.getMetrics().getNumberOfNotPermittedCalls());

        logger.info("📤 Estado consultado: {}", data);

        return data;
    }

    public Mono<String> fallback(Throwable e) {
        logger.warn("⚠️ Fallback activado por: {}", e.getMessage());
        return Mono.just("⚠️ Fallback activado: " + e.getMessage());
    }
}
