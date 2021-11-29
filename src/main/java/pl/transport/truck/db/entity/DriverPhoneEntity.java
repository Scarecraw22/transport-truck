package pl.transport.truck.db.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@AllArgsConstructor
@ToString(callSuper = true)
@Table("tt.driver_phone_number")
public class DriverPhoneEntity {

    public static final String TABLE_NAME = "driver_phone_number";

    public static final String DRIVER_ID = "driver_id";
    public static final String PHONE_NUMBER_ID = "phone_number_id";

    private Long driverId;
    private Long phoneNumberId;
}
