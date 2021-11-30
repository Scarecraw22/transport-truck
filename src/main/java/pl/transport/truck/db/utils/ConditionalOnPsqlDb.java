package pl.transport.truck.db.utils;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@ConditionalOnProperty(value = "pl.transport.truck.database-type", havingValue = "PSQL", matchIfMissing = true)
public @interface ConditionalOnPsqlDb {
}
