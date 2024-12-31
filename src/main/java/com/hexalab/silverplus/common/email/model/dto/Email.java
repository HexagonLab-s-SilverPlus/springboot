package com.hexalab.silverplus.common.email.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data       // getter, setter, ToString, Equals, HashCode 자동생성
@AllArgsConstructor     // 매개변수 있는 생성자 자동생성
@NoArgsConstructor      // 매개변수 없는 생성자 자동생성
@Builder        // 자동 build 어노테이션
public class Email {

    private String email;
    private String verifyCode;
}
