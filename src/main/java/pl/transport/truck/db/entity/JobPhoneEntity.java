package pl.transport.truck.db.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@AllArgsConstructor
@Table("job_phone_number")
public class JobPhoneEntity {

    public static final String TABLE_NAME = "job_phone_number";

    public static final String JOB_ID = "job_id";
    public static final String PHONE_NUMBER_ID = "phone_number_id";

    private Long jobId;
    private Long phoneNumberId;
}
