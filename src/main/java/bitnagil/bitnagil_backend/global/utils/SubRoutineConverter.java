package bitnagil.bitnagil_backend.global.utils;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class SubRoutineConverter implements AttributeConverter<List<String>, String> {

    // DB에 저장하기 위해 List 타입을 String 타입으로 변환
    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        if (attribute == null || attribute.isEmpty()) return "";
        return String.join(",", attribute);
    }

    // 코드레벨에서 사용하기 위해 String 타입에서 List 타입으로 변환
    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) return List.of();
        return Arrays.stream(dbData.split(","))
            .map(String::valueOf)
            .collect(Collectors.toList());
    }
}
