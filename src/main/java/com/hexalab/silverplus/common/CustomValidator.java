package com.hexalab.silverplus.common;

import com.hexalab.silverplus.member.model.dto.Member;
import com.hexalab.silverplus.notice.model.dto.Notice;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.lang.reflect.Field;

@Component
public class CustomValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    public void validate(Object target, Errors errors) {
        Class<?> clazz = target.getClass();
        while (clazz != null) { // 상속 관계를 따라 부모 클래스 처리
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true); // private 필드 접근 허용
                try {
                    Object value = field.get(target);
                    if (value instanceof String && "null".equals(value)) {
                        field.set(target, null); // "null" 문자열을 null로 변환
                    }
                } catch (IllegalAccessException e) {
                    System.err.println("Failed to access field: " + field.getName());
                    // 로그를 남기고 예외 무시
                }
            }
            clazz = clazz.getSuperclass(); // 부모 클래스로 이동
        }
    }

}
