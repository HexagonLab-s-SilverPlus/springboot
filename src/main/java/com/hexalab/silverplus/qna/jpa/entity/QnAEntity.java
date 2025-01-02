package com.hexalab.silverplus.qna.jpa.entity;

import com.hexalab.silverplus.qna.model.dto.QnA;
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
@Table(name="QNA")  //매핑할 테이블 이름 지정함
@Entity //JPA 가 관리함, 테이블의 컬럼과 DTO 클래스의 프로퍼티를 매핑하는 역할을
public class QnAEntity {
    @Id
    @Column(name="QNA_ID")
    private String qnaId;   //QNA식별코드
    @Column(name="QNA_TITLE")
    private String qnaTitle;    //제목
    @Column(name="QNA_W_CREATED_AT")
    private Timestamp qnaWCreateAt; //질문 등록 날짜
    @Column(name="QNA_W_UPDATED_AT")
    private Timestamp qnaWUpdateAt; //질문 수정 날짜
    @Column(name="QNA_W_CONTENT")
    private String qnaWContent; //질문내용
    @Column(name="QNA_W_CREATED_BY")
    private String qnaWCreateBy;    //질문자
    @Column(name="QNA_AD_CREATED_AT")
    private Timestamp qnaADCreateAt;    //답변 등록 날짜
    @Column(name="QNA_AD_UPDATED_AT")
    private Timestamp qnaADUpdateAt;    //답변 수정 날짜
    @Column(name="QNA_AD_CONTENT")
    private String qnaADContent;    //답변내용
    @Column(name="QNA_AD_CREATED_BY")
    private String qnaADCreateBy;    //답변자
    @Column(name="QNA_AD_UPDATED_BY")
    private String qnaADUpdateBy;   //답변자(수정)

    public QnA toDto(){
        return QnA.builder()
                .qnaId(qnaId)
                .qnaTitle(qnaTitle)
                .qnaWCreateAt(qnaWCreateAt)
                .qnaWUpdateAt(qnaWUpdateAt)
                .qnaWContent(qnaWContent)
                .qnaWCreateBy(qnaWCreateBy)
                .qnaADCreateAt(qnaADCreateAt)
                .qnaADUpdateAt(qnaADUpdateAt)
                .qnaADContent(qnaADContent)
                .qnaADCreateBy(qnaADCreateBy)
                .qnaADUpdateBy(qnaADUpdateBy)
                .build();
    }

    @PrePersist
    public void prePersist(){
        qnaId = UUID.randomUUID().toString();
        qnaWCreateAt = new Timestamp(System.currentTimeMillis());
        qnaWUpdateAt = new Timestamp(System.currentTimeMillis());
    }
}
