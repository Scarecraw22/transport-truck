package pl.transport.truck.converter;

import org.mapstruct.Mapper;
import pl.transport.truck.db.entity.JobEntity;
import pl.transport.truck.rest.model.job.CreateJobRequest;

@Mapper(componentModel = "spring")
public interface JobModelConverter {

    JobEntity convertToJobEntity(CreateJobRequest request);
}
