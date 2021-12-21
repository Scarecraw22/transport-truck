package pl.transport.truck.db.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserDetailsEntity extends BaseEntity {

    protected String username;
    protected String firstName;
    protected String lastName;
    protected String address;
    protected String email;
    protected String role;
    private Set<PhoneNumberEntity> phoneNumbers;
}
