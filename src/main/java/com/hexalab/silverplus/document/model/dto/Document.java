package com.hexalab.silverplus.document.model.dto;

import com.hexalab.silverplus.document.jpa.entity.DocumentEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Document {
    private String docId; // 문서 고유 ID
    private String docType; // 문서 타입(전입신고서 등)
    private Timestamp docCompletedAt; // 문서 생성날짜
    private String isApproved; // 승인 여부(디폴트 '대기중')
    private Timestamp approvedAt; // 담당자의 승인날짜
    private String writtenBy;
    private String approvedBy;


    // Dto -> Entity 변환
    public DocumentEntity toEntity() {
        return DocumentEntity.builder()
                .docId(this.getDocId())
                .docType(this.getDocType())
                .docCompletedAt(this.getDocCompletedAt())
                .isApproved(this.getIsApproved())
                .approvedAt(this.getApprovedAt())
                .writtenBy(this.getWrittenBy())
                .approvedBy(this.getApprovedBy())
                .build();
    }
}