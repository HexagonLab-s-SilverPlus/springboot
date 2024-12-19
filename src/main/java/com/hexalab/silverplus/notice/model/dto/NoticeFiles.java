package com.hexalab.silverplus.notice.model.dto;

import com.hexalab.silverplus.notice.jpa.entity.NoticeFilesEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NoticeFiles {
    private UUID nfId;                  // NF_ID	VARCHAR2(100 BYTE)
    private String nfNotId;             // NF_NOT_ID	VARCHAR2(100 BYTE)
    private String nfOreginalName;      // NF_ORIGINAL_NAME	VARCHAR2(1000 BYTE)
    private String nfRename;            // NF_RENAME	VARCHAR2(1000 BYTE)

    public NoticeFilesEntity toEntity() {
        return NoticeFilesEntity.builder()
                .nfId(nfId)
                .nfNotId(nfNotId)
                .nfOriginalName(nfOreginalName)
                .nfRename(nfRename)
                .build();
    }
}
