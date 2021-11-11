package pl.transport.truck.db.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.r2dbc.mapping.OutboundRow;
import org.springframework.r2dbc.core.Parameter;
import pl.transport.truck.db.entity.BaseEntity;

public abstract class BaseWritingConverter<T extends BaseEntity> implements Converter<T, OutboundRow> {

    protected void insertBaseColumns(T entity, OutboundRow row) {
        if (entity.getId() != null) {
            row.put(BaseEntity.ID, Parameter.from("default"));
        }
        if (entity.getUpdatedAt() != null) {
            row.put(BaseEntity.UPDATED_AT, Parameter.from(entity.getUpdatedAt()));
        }
        if (entity.getCreatedAt() != null) {
            row.put(BaseEntity.CREATED_AT, Parameter.from(entity.getCreatedAt()));
        }
    }
}
