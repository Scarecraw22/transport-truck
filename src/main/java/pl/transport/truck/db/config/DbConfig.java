package pl.transport.truck.db.config;

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import pl.transport.truck.db.config.properties.SpringFlywayProperties;
import pl.transport.truck.db.converter.PhoneNumberReadingConverter;
import pl.transport.truck.db.converter.UserReadingConverter;
import pl.transport.truck.db.query.PsqlStringQueryBuilderFactory;
import pl.transport.truck.db.query.StringQueryBuilderFactory;
import pl.transport.truck.db.utils.DbConsts;
import pl.transport.truck.db.utils.DbUtils;

import java.util.List;

@Slf4j
@Configuration
@EnableConfigurationProperties
@RequiredArgsConstructor
@EnableR2dbcRepositories(basePackages = {"pl.transport.truck.db.repository"})
public class DbConfig extends AbstractR2dbcConfiguration {

    private final R2dbcProperties r2dbcProperties;
    private final SpringFlywayProperties springFlywayProperties;

    @Bean
    @DependsOn("connectionFactory")
    public Flyway flyway() {

        if (springFlywayProperties.isEnabled()) {
            Flyway flyway = new Flyway(Flyway.configure()
                    .dataSource(r2dbcProperties.getUrl().replace(DbConsts.R2DBC, DbConsts.JDBC), r2dbcProperties.getUsername(), r2dbcProperties.getPassword()));

            log.info("Applying migrate on Flyway");
            flyway.migrate();
            flyway.validate();
            return flyway;
        } else {
            log.warn("Flyway is disabled");
            return null;
        }
    }

    @Bean
    @Override
    public List<Object> getCustomConverters() {
        return List.of(
                new PhoneNumberReadingConverter(),
                new UserReadingConverter()
        );
    }

    @Override
    @Bean
    public ConnectionFactory connectionFactory() {
        String url = r2dbcProperties.getUrl();
        log.info("Preparing ConnectionFactory bean...");
        log.info("Url from config: {}", url);

        String host = DbUtils.getHostFromUrl(url);
        log.info("Retrieved host: {}", host);

        String port = DbUtils.getPortFromUrl(url);
        log.info("Retrieved port: {}", port);

        ConnectionFactory connectionFactory = new PostgresqlConnectionFactory(
                PostgresqlConnectionConfiguration.builder()
                        .port(Integer.parseInt(port))
                        .host(host)
                        .username(r2dbcProperties.getUsername())
                        .password(r2dbcProperties.getPassword())
                        .schema("public")
                        .database(r2dbcProperties.getProperties().get("database"))
                        .build()
        );
//
//        Mono.from(connectionFactory.create())
//                .map(c -> c.createStatement("SET SCHEMA PUBLIC").execute())
//                .subscribe();

        return connectionFactory;
    }

    @Bean
    public StringQueryBuilderFactory stringQueryBuilderFactory() {
        return new PsqlStringQueryBuilderFactory();
    }
}
