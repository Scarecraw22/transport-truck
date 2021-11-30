package pl.transport.truck.db.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@AllArgsConstructor
@Table("tt.customer_phone_number")
public class CustomerPhoneEntity {

    public static final String TABLE_NAME = "customer_phone_number";

    public static final String CUSTOMER_ID = "customer_id";
    public static final String PHONE_NUMBER_ID = "phone_number_id";

    private Long customerId;
    private Long phoneNumberId;
}
