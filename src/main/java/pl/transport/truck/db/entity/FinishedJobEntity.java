package pl.transport.truck.db.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Table;

import java.time.ZonedDateTime;

@Data
@SuperBuilder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Table("finished_job")
public class FinishedJobEntity extends BaseEntity {

    public static final String TABLE_NAME = "finished_job";
    public static final String PREFIX = "fj.";
    public static final String JOB_ID = "job_id";
    public static final String DRIVER_ID = "driver_id";
    public static final String FINISHED_AT = "finished_at";

    protected Long jobId;
    protected Long driverId;
    protected ZonedDateTime finishedAt;

}
