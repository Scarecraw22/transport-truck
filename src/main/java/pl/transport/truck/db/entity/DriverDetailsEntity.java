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
public class DriverDetailsEntity extends DriverEntity {

    private Set<PhoneNumberEntity> phoneNumbers;
}
