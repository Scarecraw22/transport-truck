package pl.transport.truck.datetime.utils;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeConsts.DEFAULT_DATE_TIME_FORMAT)
public @interface DefaultDateTimeFormat {
}
