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
    private String docId;
    private String docType;
    private String docStatus;
    private Timestamp docCreateAt;
    private String isApproved;
    private Timestamp docCompleted;
    private String writtenBy;
    private String approvedBy;


    // Dto -> Entity 변환
    public DocumentEntity toEntity() {
        return DocumentEntity.builder()
                .docId(this.getDocId())
                .docType(this.getDocType())
                .docStatus(this.getDocStatus())
                .docCreateAt(this.getDocCreateAt())
                .isApproved(this.getIsApproved())
                .docCompleted(this.getDocCompleted())
                .writtenBy(this.getWrittenBy())
                .approvedBy(this.getApprovedBy())
                .build();
    }
}
