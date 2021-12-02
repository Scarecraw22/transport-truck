package pl.transport.truck.converter;

import org.mapstruct.Mapper;
import pl.transport.truck.db.entity.PhoneNumberEntity;
import pl.transport.truck.rest.model.phone.PhoneNumberDetails;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface PhoneNumberConverter {

    PhoneNumberDetails convertToDetails(PhoneNumberEntity entity);

    Set<PhoneNumberDetails> convertToDetailsSet(Set<PhoneNumberEntity> entities);
}
