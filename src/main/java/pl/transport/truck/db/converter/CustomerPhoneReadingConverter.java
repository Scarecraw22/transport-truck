package pl.transport.truck.db.converter;

import io.r2dbc.spi.Row;
import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;
import pl.transport.truck.db.entity.CustomerPhoneEntity;

@Component
@ReadingConverter
public class CustomerPhoneReadingConverter implements Converter<Row, CustomerPhoneEntity> {

    @Override
    public CustomerPhoneEntity convert(@NonNull Row source) {

        return CustomerPhoneEntity.builder()
                .customerId(source.get(CustomerPhoneEntity.CUSTOMER_ID, Long.class))
                .phoneNumberId(source.get(CustomerPhoneEntity.PHONE_NUMBER_ID, Long.class))
                .build();
    }
}
