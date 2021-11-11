package pl.transport.truck.db.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
public abstract class BaseEntity {

    public static final String ID = "id";
    public static final String UPDATED_AT = "updated_at";
    public static final String CREATED_AT = "created_at";

    @Id
    protected Long id;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;
}
