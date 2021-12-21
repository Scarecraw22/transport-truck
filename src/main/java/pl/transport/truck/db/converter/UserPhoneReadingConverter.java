package pl.transport.truck.db.converter;

import io.r2dbc.spi.Row;
import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;
import pl.transport.truck.db.entity.UserPhoneEntity;

@Component
@ReadingConverter
public class UserPhoneReadingConverter implements Converter<Row, UserPhoneEntity> {

    @Override
    public UserPhoneEntity convert(@NonNull Row source) {

        return UserPhoneEntity.builder()
                .userId(source.get(UserPhoneEntity.USER_ID, Long.class))
                .phoneNumberId(source.get(UserPhoneEntity.PHONE_NUMBER_ID, Long.class))
                .build();
    }
}
