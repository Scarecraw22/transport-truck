package pl.transport.truck;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"pl.transport.truck"})
public class TransportTruckApp {

    public static void main(String[] args) {
        SpringApplication.run(TransportTruckApp.class, args);
    }
}
