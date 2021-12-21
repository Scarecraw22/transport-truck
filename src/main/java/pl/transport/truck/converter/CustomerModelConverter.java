package pl.transport.truck.converter;

import org.mapstruct.Mapper;
import pl.transport.truck.db.entity.UserDetailsEntity;
import pl.transport.truck.db.entity.UserEntity;
import pl.transport.truck.rest.model.customer.CreateCustomerRequest;
import pl.transport.truck.rest.model.customer.CreateCustomerResponse;
import pl.transport.truck.rest.model.customer.GetCustomerDetailsResponse;

@Mapper(componentModel = "spring", uses = {
        PhoneNumberConverter.class
})
public interface CustomerModelConverter {

    UserEntity convertToCustomerEntity(CreateCustomerRequest request);

    CreateCustomerResponse convertToCreateCustomerResponse(UserEntity entity);

    GetCustomerDetailsResponse convertToCustomerDetailsResponse(UserDetailsEntity entity);
}
