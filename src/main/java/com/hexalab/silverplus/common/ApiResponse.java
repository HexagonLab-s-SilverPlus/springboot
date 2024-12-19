package org.myweb.first.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// 성공 응답용 DTO
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private boolean success; // 성공 여부
    private String message; // 메시지
    private T data; // 데이터
}





























