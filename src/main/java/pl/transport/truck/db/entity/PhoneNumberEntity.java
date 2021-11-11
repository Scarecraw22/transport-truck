package pl.transport.truck.db.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Table;

@Data
@SuperBuilder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table("tt.phone_number")
public class PhoneNumberEntity extends BaseEntity {

    public static final String PREFIX = "pn.";

    public static final String PHONE_PREFIX = "phone_prefix";
    public static final String PHONE_NUMBER = "phone_number";
    public static final String UPDATED_AT = "updated_at";
    public static final String CREATED_AT = "created_at";

    private String phonePrefix;
    private String phoneNumber;
}
