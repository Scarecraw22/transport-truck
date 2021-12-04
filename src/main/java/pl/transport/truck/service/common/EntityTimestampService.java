package pl.transport.truck.service.common;

import pl.transport.truck.db.entity.BaseEntity;

public interface EntityTimestampService {

    void setupDatesForNewEntity(BaseEntity entity);

    void setupDatesForExistingEntity(BaseEntity entity);
}
