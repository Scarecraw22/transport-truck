package pl.transport.truck.db.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Set;

@Data
@SuperBuilder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"password"})
@ToString(callSuper = true, exclude = {"password"})
@Table("tt.customer")
public class CustomerEntity extends BaseEntity {

    public static final String PREFIX = "c.";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String ADDRESS = "address";
    public static final String EMAIL = "email";
    public static final String PHONE_NUMBER_ID = "phone_number_id";
    public static final String PASSWORD = "password";

    private String password;
    private String firstName;
    private String lastName;
    private String address;
    private String email;
    private Set<PhoneNumberEntity> phoneNumbers;

}
