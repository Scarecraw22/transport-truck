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
@Table("assigned_job")
public class AssignedJobEntity extends BaseEntity {

    public static final String TABLE_NAME = "assigned_job";
    public static final String PREFIX = "aj.";
    public static final String JOB_ID = "job_id";
    public static final String DRIVER_ID = "driver_id";
    public static final String IN_PROGRESS = "in_progress";
    public static final String ASSIGNED_AT = "assigned_at";

    protected Long jobId;
    protected Long driverId;
    protected Boolean inProgress;
    protected ZonedDateTime assignedAt;

}
