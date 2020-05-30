package com.trade.utils.easytrade.ext;

import javax.ws.rs.ext.ParamConverter;
import java.time.LocalDate;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

public class LocalDateParamConverter implements ParamConverter<LocalDate> {

    @Override
    public LocalDate fromString(String value) {
        return LocalDate.parse(value, ISO_LOCAL_DATE);
    }

    @Override
    public String toString(LocalDate value) {
        return value.format(ISO_LOCAL_DATE);
    }
}
