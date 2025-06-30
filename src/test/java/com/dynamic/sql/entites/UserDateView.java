package com.dynamic.sql.entites;

import com.dynamic.sql.anno.Column;
import com.dynamic.sql.plugins.conversion.AttributeConverter;
import lombok.Data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class UserDateView {
    private Integer userId;  // 用户 ID
    @Column(converter = RegistrationDateAttributeConverter.class)
    private Date registrationDate;  // 注册日期
}

class RegistrationDateAttributeConverter implements AttributeConverter<Date, String> {
    @Override
    public String convertToDatabaseColumn(Date attribute) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Date convertToEntityAttribute(String dbData) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        try {
            Date date = sdf.parse(dbData);
            return date;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
