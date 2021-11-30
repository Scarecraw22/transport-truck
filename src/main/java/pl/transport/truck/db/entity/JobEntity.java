package pl.transport.truck.db.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Table;

@Data
@SuperBuilder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Table("tt.job")
public class JobEntity extends BaseEntity {

    public static final String TABLE_NAME = "job";
    public static final String PREFIX = "j.";
    public static final String CUSTOMER_ID = "customer_id";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String SOURCE_ADDRESS = "source_address";
    public static final String DESTINATION_ADDRESS = "destination_address";
    public static final String DESTINATION_EMAIL = "destination_email";

    protected Long customerId;
    protected String title;
    protected String description;
    protected String sourceAddress;
    protected String destinationAddress;
    protected String destinationEmail;

}
