package pl.transport.truck.converter;

import org.mapstruct.Mapper;
import pl.transport.truck.db.entity.JobDetailsEntity;
import pl.transport.truck.db.entity.JobEntity;
import pl.transport.truck.db.entity.PhoneNumberEntity;
import pl.transport.truck.db.entity.UserEntity;
import pl.transport.truck.rest.model.job.CreateJobRequest;
import pl.transport.truck.rest.model.job.GetJobDetailsResponse;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface JobModelConverter {

    JobEntity convertToJobEntity(CreateJobRequest request);

    GetJobDetailsResponse convertToJobDetailsResponse(JobDetailsEntity jobDetails);

    GetJobDetailsResponse.User convertToJobDetailsUser(UserEntity entity);

    GetJobDetailsResponse.Phone convertToJobDetailsPhone(PhoneNumberEntity entity);

    Set<GetJobDetailsResponse.Phone> convertToJobDetailsPhones(Set<PhoneNumberEntity> entities);
}
