package com.hexalab.silverplus.notice.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hexalab.silverplus.notice.jpa.entity.NoticeEntity;
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
public class Notice {
    private String notId;                 // NOT_ID	VARCHAR2(100 BYTE)
    private String notTitle;            // NOT_TITLE	VARCHAR2(250 BYTE)
    private String notContent;          // NOT_CONTENT	CLOB
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Timestamp notCreateAt;      // NOT_CREATED_AT	TIMESTAMP(6)
    private String notCreateBy;         // NOT_CREATED_BY	VARCHAR2(100 BYTE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Timestamp notUpdateAt;      // NOT_UPDATED_AT	TIMESTAMP(6)
    private String notUpdateBy;         // NOT_UPDATED_BY	VARCHAR2(100 BYTE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Timestamp notDeleteAt;      // NOT_DELETED_AT	TIMESTAMP(6)
    private String notDeleteBy;         // NOT_DELETED_BY	VARCHAR2(100 BYTE)
    private int notReadCount;        // NOT_READ_COUNT	NUMBER

    public NoticeEntity toEntity(){
        return NoticeEntity.builder()
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
