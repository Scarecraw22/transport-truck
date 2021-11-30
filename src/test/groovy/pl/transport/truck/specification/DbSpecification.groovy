package pl.transport.truck.specification

import groovy.util.logging.Slf4j
import org.flywaydb.core.Flyway
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import pl.transport.truck.initializer.DbIntegrationTestInitializer
import spock.lang.Specification

import java.util.concurrent.atomic.AtomicBoolean

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(initializers = [DbIntegrationTestInitializer.class])
@ActiveProfiles(["test"])
abstract class DbSpecification extends Specification {

    private static AtomicBoolean migrated = new AtomicBoolean(false)

    @Autowired
    private Flyway flyway

    def setup() {
        synchronized (this) {
            if (!migrated.get()) {
                migrated.set(true)
                flyway.migrate()
            }
        }
    }
}
