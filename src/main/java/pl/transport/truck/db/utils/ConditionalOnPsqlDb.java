package pl.transport.truck.db.utils;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@ConditionalOnProperty(value = "pl.transport.truck.database-type", havingValue = "PSQL", matchIfMissing = true)
public @interface ConditionalOnPsqlDb {
}
