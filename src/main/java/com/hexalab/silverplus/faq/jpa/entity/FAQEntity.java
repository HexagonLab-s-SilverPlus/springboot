package com.hexalab.silverplus.faq.jpa.entity;

import com.hexalab.silverplus.faq.model.dto.FAQ;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@Data    //@Getter, @Setter, @ToString, @EqualsAndHashCode, @RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="FAQ")  //매핑할 테이블 이름 지정함
@Entity //JPA 가 관리함, 테이블의 컬럼과 DTO 클래스의 프로퍼티를 매핑하는 역할을
public class FAQEntity {
    @Id
    @Column(name="FAQ_ID")
    private String faqId;               //faq식별코드
    @Column(name="FAQ_TITLE")
    private String faqTitle;            //제목
    @Column(name="FAQ_CREATED_AT")
    private Timestamp faqCreatedAt;     //등록날짜
    @Column(name="FAQ_UPDATED_AT")
    private Timestamp faqUpdatedAt;     //수정날짜
    @Column(name="FAQ_CONTENT")
    private String faqContent;          //내용
    @Column(name="FAQ_CREATED_BY")
    private String faqCreatedBy;        //질문자
    @Column(name="FAQ_UPDATED_BY")
    private String faqUpdatedBy;        //작성자(수정)

    public FAQ toDto(){
        return FAQ.builder()
                .faqId(faqId)
                .faqTitle(faqTitle)
                .faqCreatedAt(faqCreatedAt)
                .faqUpdatedAt(faqUpdatedAt)
                .faqContent(faqContent)
                .faqCreatedBy(faqCreatedBy)
                .faqUpdatedBy(faqUpdatedBy)
                .build();
    }

    @PrePersist
    public void prePersist() {
        faqId = UUID.randomUUID().toString();
    }


}
