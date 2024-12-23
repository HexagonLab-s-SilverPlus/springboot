package com.hexalab.silverplus.qna.model.dto;

import com.hexalab.silverplus.qna.jpa.entity.QnAEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data    //@Getter, @Setter, @ToString, @EqualsAndHashCode, @RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QnA {
    private String qnaId;                 //QNA식별코드
    private String qnaTitle;            //제목
    private Timestamp qnaWCreateAt;     //질문 등록 날짜
    private Timestamp qnaWUpdateAt;     //질문 수정 날짜
    private String qnaWContent;         //질문내용
    private String qnaWCreateBy;        //질문자
    private Timestamp qnaADCreateAt;    //답변 등록 날짜
    private Timestamp qnaADUpdateAt;    //답변 수정 날짜
    private String qnaADContent;        //답변내용
    private String qnaADCreateBy;       //답변자
    private String qnaADUpdateBy;       //답변자(수정)

    public QnAEntity toEntity() {
        return QnAEntity.builder()
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
}
