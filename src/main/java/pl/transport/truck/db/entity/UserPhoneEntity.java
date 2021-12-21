package pl.transport.truck.db.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@AllArgsConstructor
@Table("tt.user_phone_number")
public class UserPhoneEntity {

    public static final String TABLE_NAME = "user_phone_number";

    public static final String USER_ID = "user_id";
    public static final String PHONE_NUMBER_ID = "phone_number_id";

    private Long userId;
    private Long phoneNumberId;
}
