package pl.transport.truck.db.converter;

import io.r2dbc.spi.Row;
import org.springframework.data.convert.ReadingConverter;
import pl.transport.truck.db.entity.UserEntity;

@ReadingConverter
public class UserReadingConverter extends BaseReadingConverter<UserEntity> {

    @Override
    public UserEntity convert(Row source) {
        UserEntity entity = UserEntity.builder()
                .username(source.get(UserEntity.USERNAME, String.class))
                .firstName(source.get(UserEntity.FIRST_NAME, String.class))
                .lastName(source.get(UserEntity.LAST_NAME, String.class))
                .address(source.get(UserEntity.ADDRESS, String.class))
                .email(source.get(UserEntity.EMAIL, String.class))
                .role(source.get(UserEntity.ROLE, String.class))
                .build();

        super.setBaseFields(source, entity);

        return entity;
    }
}
