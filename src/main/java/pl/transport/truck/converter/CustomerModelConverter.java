package pl.transport.truck.converter;

import org.mapstruct.Mapper;
import pl.transport.truck.db.entity.CustomerDetailsEntity;
import pl.transport.truck.db.entity.CustomerEntity;
import pl.transport.truck.rest.model.customer.CreateCustomerRequest;
import pl.transport.truck.rest.model.customer.CreateCustomerResponse;
import pl.transport.truck.rest.model.customer.GetCustomerDetailsResponse;

@Mapper(componentModel = "spring", uses = {
        PhoneNumberConverter.class
})
public interface CustomerModelConverter {

    CustomerEntity convertToCustomerEntity(CreateCustomerRequest request);

    CreateCustomerResponse convertToCreateCustomerResponse(CustomerEntity entity);

    GetCustomerDetailsResponse convertToCustomerDetailsResponse(CustomerDetailsEntity entity);
}
