package pl.transport.truck.db.converter;

import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.r2dbc.mapping.OutboundRow;
import org.springframework.r2dbc.core.Parameter;
import pl.transport.truck.db.entity.CustomerPhoneEntity;

@WritingConverter
public class CustomerPhoneWritingConverter implements Converter<CustomerPhoneEntity, OutboundRow> {

    @Override
    public OutboundRow convert(@NonNull CustomerPhoneEntity source) {
        OutboundRow outboundRow = new OutboundRow();

        outboundRow.put(CustomerPhoneEntity.CUSTOMER_ID, Parameter.from(source.getCustomerId()));
        outboundRow.put(CustomerPhoneEntity.PHONE_NUMBER_ID, Parameter.from(source.getPhoneNumberId()));

        return outboundRow;
    }
}
