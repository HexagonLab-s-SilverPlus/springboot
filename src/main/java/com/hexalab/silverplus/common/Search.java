package com.hexalab.silverplus.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Search {
    private String action;  // 제목, 내용, 기관, 주소, 기간
    private String keyword;  // 키워드
    private Timestamp startDate;  // 검색 시작일
    private Timestamp endDate;   // 검색 종료일
    private int pageNumber;  // 현재페이지
    private int pageSize;  // 한페이지에 출력할 데이터 갯수
    private int listCount;  // 총 데이터갯수
}
