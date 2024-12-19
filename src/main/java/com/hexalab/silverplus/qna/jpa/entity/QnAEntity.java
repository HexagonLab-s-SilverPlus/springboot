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
    private UUID qnaId;
    @Column(name="QNA_TITLE")
    private String qnaTitle;
    @Column(name="QNA_W_CREATED_AT")
    private Timestamp qnaWCreateAt;
    @Column(name="QNA_W_UPDATED_AT")
    private Timestamp qnaWUpdateAt;
    @Column(name="QNA_W_CONTENT")
    private String qnaWContent;
    @Column(name="QNA_W_CREATED_BY")
    private String qnaWCreateBy;
    @Column(name="QNA_AD_CREATED_AT")
    private Timestamp qnaADCreateAt;
    @Column(name="QNA_AD_UPDATED_AT")
    private Timestamp qnaADUpdateAt;
    @Column(name="QNA_AD_CONTENT")
    private String qnaADContent;
    @Column(name="QNA_AD_CREATED_BY")
    private String qnaADCreateBy;
    @Column(name="QNA_AD_UPDATED_BY")
    private String qnaADUpdateBy;

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
        qnaId = UUID.randomUUID();
        qnaWCreateAt = new Timestamp(System.currentTimeMillis());
        qnaWUpdateAt = new Timestamp(System.currentTimeMillis());
    }
}
