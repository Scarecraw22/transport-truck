package pl.transport.truck.db.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;

import java.time.ZonedDateTime;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseEntity {

    public static final String ID = "id";
    public static final String UPDATED_AT = "updated_at";
    public static final String CREATED_AT = "created_at";

    @Id
    protected Long id;
    protected ZonedDateTime createdAt;
    protected ZonedDateTime updatedAt;
}
