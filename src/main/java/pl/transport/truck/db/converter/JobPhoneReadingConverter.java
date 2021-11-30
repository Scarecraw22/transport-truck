package pl.transport.truck.db.converter;

import io.r2dbc.spi.Row;
import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import pl.transport.truck.db.entity.JobPhoneEntity;

@Component
public class JobPhoneReadingConverter implements Converter<Row, JobPhoneEntity> {

    @Override
    public JobPhoneEntity convert(@NonNull Row source) {

        return JobPhoneEntity.builder()
                .jobId(source.get(JobPhoneEntity.JOB_ID, Long.class))
                .phoneNumberId(source.get(JobPhoneEntity.PHONE_NUMBER_ID, Long.class))
                .build();
    }
}
