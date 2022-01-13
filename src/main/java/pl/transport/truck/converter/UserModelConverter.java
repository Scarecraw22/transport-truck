package pl.transport.truck.converter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import pl.transport.truck.db.entity.UserDetailsEntity;
import pl.transport.truck.db.entity.UserEntity;
import pl.transport.truck.rest.model.user.CreateUserRequest;
import pl.transport.truck.rest.model.user.CreateUserResponse;
import pl.transport.truck.rest.model.user.GetUserDetailsResponse;

@Mapper(componentModel = "spring", uses = {
        PhoneNumberConverter.class
})
public interface UserModelConverter {

    UserEntity convertToCustomerEntity(CreateUserRequest request);

    CreateUserResponse convertToCreateCustomerResponse(UserEntity entity);

    @Mappings({
            @Mapping(source = "phoneNumbers", target = "phones")
    })
    GetUserDetailsResponse convertToCustomerDetailsResponse(UserDetailsEntity entity);

}
