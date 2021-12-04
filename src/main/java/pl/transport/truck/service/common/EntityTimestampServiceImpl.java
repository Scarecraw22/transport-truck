package pl.transport.truck.service.common;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.transport.truck.db.entity.BaseEntity;

import java.time.Clock;
import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class EntityTimestampServiceImpl implements EntityTimestampService {

    private final Clock clock;

    @Override
    public void setupDatesForNewEntity(BaseEntity entity) {
        entity.setCreatedAt(ZonedDateTime.now(clock));
    }

    @Override
    public void setupDatesForExistingEntity(BaseEntity entity) {
        entity.setUpdatedAt(ZonedDateTime.now(clock));
    }
}
