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

    @Column(name = "DOC_COMPLETED_AT", nullable = false)
    private Timestamp docCompletedAt;

    @Column(name = "IS_APPROVED", columnDefinition = "VARCHAR2(20) DEFAULT '대기중'")
    private String isApproved = "대기중";

    @Column(name = "APPROVED_AT")
    private Timestamp approvedAt;

    @Column(name = "WRITTEN_BY", nullable = false)
    private String writtenBy;

    @Column(name = "APPROVED_BY")
    private String approvedBy;



    // Entity -> DTO 변환
    public Document toDto() {
        return Document.builder()
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