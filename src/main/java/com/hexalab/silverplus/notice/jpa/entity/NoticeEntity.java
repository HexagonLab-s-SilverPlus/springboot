package com.hexalab.silverplus.notice.jpa.entity;

import com.hexalab.silverplus.notice.model.dto.Notice;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="NOTICE")
@Entity
public class NoticeEntity {
    @Id
    @Column(name="NOT_ID")
    private UUID notId;                 // NOT_ID	VARCHAR2(100 BYTE)
    @Column(name="NOT_TITLE")
    private String notTitle;            // NOT_TITLE	VARCHAR2(250 BYTE)
    @Column(name="NOT_CONTENT")
    private String notContent;          // NOT_CONTENT	CLOB
    @Column(name="NOT_CREATED_AT")
    private Timestamp notCreateAt;      // NOT_CREATED_AT	TIMESTAMP(6)
    @Column(name="NOT_CREATED_BY")
    private String notCreateBy;         // NOT_CREATED_BY	VARCHAR2(100 BYTE)
    @Column(name="NOT_UPDATED_AT")
    private Timestamp notUpdateAt;      // NOT_UPDATED_AT	TIMESTAMP(6)
    @Column(name="NOT_UPDATED_BY")
    private String notUpdateBy;         // NOT_UPDATED_BY	VARCHAR2(100 BYTE)
    @Column(name="NOT_DELETED_AT")
    private Timestamp notDeleteAt;      // NOT_DELETED_AT	TIMESTAMP(6)
    @Column(name="NOT_DELETED_BY")
    private String notDeleteBy;         // NOT_DELETED_BY	VARCHAR2(100 BYTE)
    @Column(name="NOT_READ_COUNT")
    private int notReadCount;        // NOT_READ_COUNT	NUMBER

    @PrePersist
    public void prePersist() {
        notId = UUID.randomUUID();
    }

    public Notice toDto(){
        return Notice.builder()
                .notId(notId)
                .notTitle(notTitle)
                .notContent(notContent)
                .notCreateAt(notCreateAt)
                .notCreateBy(notCreateBy)
                .notUpdateAt(notUpdateAt)
                .notUpdateBy(notUpdateBy)
                .notDeleteAt(notDeleteAt)
                .notDeleteBy(notDeleteBy)
                .notReadCount(notReadCount)
                .build();
    }
}
