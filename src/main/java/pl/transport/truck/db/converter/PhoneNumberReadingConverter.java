package pl.transport.truck.db.converter;

import io.r2dbc.spi.Row;
import org.springframework.data.convert.ReadingConverter;
import pl.transport.truck.db.entity.PhoneNumberEntity;

@ReadingConverter
public class PhoneNumberReadingConverter extends BaseReadingConverter<PhoneNumberEntity> {

    @Override
    public PhoneNumberEntity convert(Row source) {
        PhoneNumberEntity entity = PhoneNumberEntity.builder()
                .phonePrefix(source.get(PhoneNumberEntity.PHONE_PREFIX, String.class))
                .phoneNumber(source.get(PhoneNumberEntity.PHONE_NUMBER, String.class))
                .build();

        super.setBaseFields(source, entity);

        return entity;
    }
}
