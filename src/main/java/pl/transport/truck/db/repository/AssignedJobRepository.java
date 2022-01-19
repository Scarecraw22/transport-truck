package pl.transport.truck.db.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import pl.transport.truck.db.entity.AssignedJobEntity;

@Repository
public interface AssignedJobRepository extends R2dbcRepository<AssignedJobEntity, Long> {
}
