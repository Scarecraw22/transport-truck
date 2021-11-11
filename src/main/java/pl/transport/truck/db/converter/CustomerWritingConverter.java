package pl.transport.truck.db.converter;

import lombok.NonNull;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.r2dbc.mapping.OutboundRow;
import org.springframework.r2dbc.core.Parameter;
import pl.transport.truck.db.entity.CustomerEntity;

@WritingConverter
public class CustomerWritingConverter extends BaseWritingConverter<CustomerEntity> {

    @Override
    public OutboundRow convert(@NonNull CustomerEntity source) {
        OutboundRow outboundRow = new OutboundRow();
        super.insertBaseColumns(source, outboundRow);

        outboundRow.put(CustomerEntity.FIRST_NAME, Parameter.from(source.getFirstName()));
        outboundRow.put(CustomerEntity.LAST_NAME, Parameter.from(source.getLastName()));
        outboundRow.put(CustomerEntity.PASSWORD, Parameter.from(source.getPassword()));
        outboundRow.put(CustomerEntity.ADDRESS, Parameter.from(source.getAddress()));
        outboundRow.put(CustomerEntity.EMAIL, Parameter.from(source.getEmail()));

        return outboundRow;
    }
}
