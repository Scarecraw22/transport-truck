package pl.transport.truck.db.converter;

import io.r2dbc.spi.Row;
import org.springframework.core.convert.converter.Converter;
import pl.transport.truck.db.entity.BaseEntity;

import java.time.LocalDateTime;

public abstract class BaseReadingConverter<T extends BaseEntity> implements Converter<Row, T> {

    protected void setBaseFields(Row source, BaseEntity entity) {
        entity.setId(source.get(BaseEntity.ID, Long.class));
        entity.setUpdatedAt(source.get(BaseEntity.UPDATED_AT, LocalDateTime.class));
        entity.setCreatedAt(source.get(BaseEntity.CREATED_AT, LocalDateTime.class));
    }
}
