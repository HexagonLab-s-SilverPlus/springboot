package com.hexalab.silverplus.faq.model.dto;

import com.hexalab.silverplus.faq.jpa.entity.FAQEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data    //@Getter, @Setter, @ToString, @EqualsAndHashCode, @RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FAQ {
    private String faqId;               //faq식별코드
    private String faqTitle;            //제목
    private Timestamp faqCreatedAt;     //등록날짜
    private Timestamp faqUpdatedAt;     //수정날짜
    private String faqContent;          //내용
    private String faqCreatedBy;        //질문자
    private String faqUpdatedBy;        //작성자(수정)

    public FAQEntity toEntity() {
        return FAQEntity.builder()
                .faqId(faqId)
                .faqTitle(faqTitle)
                .faqCreatedAt(faqCreatedAt)
                .faqUpdatedAt(faqUpdatedAt)
                .faqContent(faqContent)
                .faqCreatedBy(faqCreatedBy)
                .faqUpdatedBy(faqUpdatedBy)
                .build();
    }
}
