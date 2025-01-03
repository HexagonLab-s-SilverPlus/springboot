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
    public Document toDto() {
        return Document.builder()
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
