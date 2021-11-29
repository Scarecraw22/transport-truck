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

    public static final String PHONE_PREFIX = "phone_prefix";
    public static final String PHONE_NUMBER = "phone_number";

    protected String phonePrefix;
    protected String phoneNumber;
}
