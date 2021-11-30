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
@EqualsAndHashCode(callSuper = true, exclude = {"password", "salt"})
@ToString(callSuper = true, exclude = {"password", "salt"})
@Table("tt.driver")
public class DriverEntity extends BaseEntity {

    public static final String TABLE_NAME = "driver";
    public static final String PREFIX = "d.";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String ADDRESS = "address";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String SALT = "salt";

    protected String password;
    protected String salt;
    protected String firstName;
    protected String lastName;
    protected String address;
    protected String email;

}
