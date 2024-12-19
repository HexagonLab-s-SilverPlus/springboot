package com.hexalab.silverplus.security.jwt.filter.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 로그인 시 전송온 정보(아이디 | 비밀번호) 저장용
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InputMember {
    private String memId;
    private String memPw;

    public InputMember(String username) {
        this.memId = username;
    }
}