package com.hexalab.silverplus.document.jpa.entity;

import com.hexalab.silverplus.document.model.dto.Document;
import com.hexalab.silverplus.member.jpa.entity.MemberEntity;
import jakarta.persistence.*;
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
                .docCompletedAt(this.getDocCompletedAt())
                .isApproved(this.getIsApproved())
                .approvedAt(this.getApprovedAt())
                .writtenBy(this.getWrittenBy())
                .approvedBy(this.getApprovedBy())
                .build();
    }
}
