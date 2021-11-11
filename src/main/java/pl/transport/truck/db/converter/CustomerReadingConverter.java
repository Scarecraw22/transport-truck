package pl.transport.truck.db.converter;

import io.r2dbc.spi.Row;
import org.springframework.data.convert.ReadingConverter;
import pl.transport.truck.db.entity.CustomerEntity;

@ReadingConverter
public class CustomerReadingConverter extends BaseReadingConverter<CustomerEntity> {

    @Override
    public CustomerEntity convert(Row source) {
        CustomerEntity entity = CustomerEntity.builder()
                .firstName(source.get(CustomerEntity.FIRST_NAME, String.class))
                .lastName(source.get(CustomerEntity.LAST_NAME, String.class))
                .address(source.get(CustomerEntity.ADDRESS, String.class))
                .email(source.get(CustomerEntity.EMAIL, String.class))
                .build();

        super.setBaseFields(source, entity);

        return entity;
    }
}
