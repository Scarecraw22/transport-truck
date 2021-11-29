package pl.transport.truck.db.converter;

import io.r2dbc.spi.Row;
import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;
import pl.transport.truck.db.entity.DriverPhoneEntity;

@Component
@ReadingConverter
public class DriverPhoneReadingConverter implements Converter<Row, DriverPhoneEntity> {

    @Override
    public DriverPhoneEntity convert(@NonNull Row source) {

        return DriverPhoneEntity.builder()
                .driverId(source.get(DriverPhoneEntity.DRIVER_ID, Long.class))
                .phoneNumberId(source.get(DriverPhoneEntity.PHONE_NUMBER_ID, Long.class))
                .build();
    }
}
