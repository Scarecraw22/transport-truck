package pl.transport.truck.specification

import org.springframework.beans.factory.annotation.Autowired
import pl.transport.truck.utils.TestRepositoryUtils

import java.time.Clock
import java.time.ZonedDateTime

abstract class RepositorySpecification extends DbSpecification {

    @Autowired
    protected Clock clock

    @Autowired
    protected TestRepositoryUtils testRepositoryUtils

    protected ZonedDateTime now

    def setup() {
        if (now == null) {
            now = ZonedDateTime.now(clock)
        }
    }
}
