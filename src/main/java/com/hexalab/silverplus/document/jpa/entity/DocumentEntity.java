package com.hexalab.silverplus.document.jpa.entity;

import com.hexalab.silverplus.document.model.dto.Document;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "DOCUMENT")
@Entity
public class DocumentEntity {
    @Id
    @Column(name = "DOC_ID")
    private String docId;

    @Column(name = "DOC_TYPE", nullable = false)
    private String docType;

    @Column(name = "DOC_STATUS")
    private String docStatus;

    @Column(name = "DOC_CREATE_AT", nullable = false)
    private Timestamp docCreateAt;

    @Column(name = "IS_APPROVED")
    private String isApproved;

    @Column(name = "DOC_COMPLETED")
    private Timestamp docCompleted;

    @Column(name = "WRITTEN_BY", nullable = false)
    private String writtenBy;

    @Column(name = "APPROVED_BY")
    private String approvedBy;



    // Entity -> DTO 변환
    public Document toDto(DocumentEntity entity) {
        return Document.builder()
                .docId(entity.getDocId())
                .docType(entity.getDocType())
                .docStatus(entity.getDocStatus())
                .docCreateAt(entity.getDocCreateAt())
                .isApproved(entity.getIsApproved())
                .docCompleted(entity.getDocCompleted())
                .writtenBy(entity.getWrittenBy())
                .approvedBy(entity.getApprovedBy())
                .build();
    }
}
