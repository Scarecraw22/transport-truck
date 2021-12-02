package pl.transport.truck.containers;

import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@Slf4j
public class RedisTestContainer {

    private static final int PORT = 6379;
    private static final GenericContainer CONTAINER = new GenericContainer(DockerImageName.parse("redis:6.0.10-alpine"))
            .withExposedPorts(PORT);

    private RedisTestContainer() {
    }

    public synchronized void startWithStopOnShutdown() {
        if (!CONTAINER.isRunning()) {
            CONTAINER.start();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                log.info("Stopping redis container");
                CONTAINER.stop();
                log.info("Redis container stopped");
            }));
        } else {
            log.info("Redis container already running");
        }
    }

    public String getUrl() {
        return CONTAINER.getHost();
    }

    public int getPort() {
        return CONTAINER.getMappedPort(PORT);
    }

    public String getRedisUrl() {
        return "redis://" + getUrl();
    }

    public static synchronized RedisTestContainer getInstance() {
        return new RedisTestContainer();
    }
}
