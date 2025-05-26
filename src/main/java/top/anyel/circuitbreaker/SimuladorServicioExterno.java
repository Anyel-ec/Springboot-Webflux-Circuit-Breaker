package top.anyel.circuitbreaker;


import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class SimuladorServicioExterno {

    private final Random random = new Random();

    public String llamadaServicio() {
        int chance = random.nextInt(10); // 0 a 9
        if (chance < 9) { // 70% de fallar
            throw new RuntimeException("Error simulado");
        }
        return "Éxito desde el servicio simulado";
    }
}
