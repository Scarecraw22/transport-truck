package pl.transport.truck.specification

import org.springframework.beans.factory.annotation.Autowired

import java.time.Clock
import java.time.ZonedDateTime

abstract class RepositorySpecification extends DbSpecification {

    @Autowired
    protected Clock clock

    protected ZonedDateTime now

    def setup() {
        if (now == null) {
            now = ZonedDateTime.now(clock)
        }
    }
}
