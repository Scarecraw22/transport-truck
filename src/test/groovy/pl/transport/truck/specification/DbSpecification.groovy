package pl.transport.truck.specification

import groovy.util.logging.Slf4j
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import pl.transport.truck.initializer.DbIntegrationTestInitializer
import spock.lang.Specification

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(initializers = [DbIntegrationTestInitializer.class])
@ActiveProfiles(["test"])
abstract class DbSpecification extends Specification {

}
