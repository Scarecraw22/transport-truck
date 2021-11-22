package pl.transport.truck.db.converter;

import lombok.NonNull;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.r2dbc.mapping.OutboundRow;
import org.springframework.r2dbc.core.Parameter;
import pl.transport.truck.db.entity.PhoneNumberEntity;

@WritingConverter
public class PhoneNumberWritingConverter extends BaseWritingConverter<PhoneNumberEntity> {

    @Override
    public OutboundRow convert(@NonNull PhoneNumberEntity source) {
        OutboundRow outboundRow = new OutboundRow();
        super.insertBaseColumns(source, outboundRow);

        outboundRow.put(PhoneNumberEntity.PHONE_PREFIX, Parameter.from(source.getPhonePrefix()));
        outboundRow.put(PhoneNumberEntity.PHONE_NUMBER, Parameter.from(source.getPhoneNumber()));

        return outboundRow;
    }
}
