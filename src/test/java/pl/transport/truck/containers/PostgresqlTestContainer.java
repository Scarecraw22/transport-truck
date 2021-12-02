package pl.transport.truck.containers;

import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.startupcheck.IndefiniteWaitOneShotStartupCheckStrategy;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitStrategyTarget;

import java.time.Duration;

@Slf4j
public class PostgresqlTestContainer {

    private static final PostgreSQLContainer CONTAINER = (PostgreSQLContainer) new PostgreSQLContainer("postgres:13.2")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");

    private PostgresqlTestContainer() {
    }

    public synchronized void startWithStopOnShutdown() {
        if (!CONTAINER.isRunning()) {
            CONTAINER.start();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                log.info("Stopping postgres container");
                CONTAINER.stop();
                log.info("Postgres container stopped");
            }));
        } else {
            log.info("Postgres container already running");
        }
    }

    public String getJdbcUrl() {
        return CONTAINER.getJdbcUrl();
    }

    public String getUsername() {
        return CONTAINER.getUsername();
    }

    public String getPassword() {
        return CONTAINER.getPassword();
    }

    public static synchronized PostgresqlTestContainer getInstance() {
        return new PostgresqlTestContainer();
    }
}
